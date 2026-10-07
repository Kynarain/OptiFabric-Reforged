/*
 * Ported to the 1.20.6 line from the 1.21.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * Puts back a call site a Fabric mixin injects at, without touching OptiFine's own code.
 *
 * OptiFine rewrites the methods it patches, and a mixin point that names a call inside such a method then simply
 * does not exist any more. Mixin fails the whole class for a missing point (require = 1), which is a crash.
 * Restoring the vanilla method is the blunt repair (this line already has RestoreVanillaMethodsFix for it) but it
 * throws OptiFine's own work away - and for a method OptiFine rewrote for its renderer that is exactly what this
 * mod must not do.
 *
 * So this fixer keeps OptiFine's body and only re-creates the missing call site, in front of the method: it loads
 * the arguments the call needs from the method's own parameters and discards the result. The inserted sequence is
 * real, verifiable bytecode and the called method is a pure getter, so the method behaves as before; the mixin
 * finds its point and applies. Its handler then runs on a value nothing uses, which is the honest trade-off: the
 * mod keeps OptiFine's behaviour, the mod's hook becomes inert instead of failing the class.
 *
 * The opcode is taken from the vanilla counterpart, so a static, virtual or interface call is reproduced exactly.
 *
 * One argument of the re-created call can be named as a static field of the callee's owner instead of being
 * taken from the method's parameters: javac also inlines <em>arguments</em> away, and when the game passed a
 * constant the patched method no longer mentions it at all.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import kynarain.cn.optifabric.util.RemappingUtils;

public class InjectionCallPointFix implements ClassFixer {
	private final String calleeOwner;
	private final String calleeName;
	private final String calleeDesc;
	private final String[] methods;
	/** An argument the call needs that is not a parameter of the method: the field holding it, or null. */
	private final boolean hasArgumentField;
	private final String argumentFieldOwner;
	private final String argumentFieldName;
	private final String argumentFieldDesc;

	/**
	 * @param calleeOwner the class the missing call goes to, as an intermediary name ({@code class_1163})
	 * @param calleeName  the method name in the runtime namespace ({@code method_4961})
	 * @param calleeDesc  the descriptor with named classes, as {@code RemappingUtils} expects it
	 * @param methods     the methods of the patched class to look in (intermediary names)
	 */
	public InjectionCallPointFix(String calleeOwner, String calleeName, String calleeDesc, String... methods) {
		this.calleeOwner = RemappingUtils.getClassName(calleeOwner);
		this.calleeName = RemappingUtils.getMethodName(calleeOwner, calleeName, calleeDesc);
		this.calleeDesc = RemappingUtils.mapMethodDescriptor(calleeDesc);
		this.methods = methods;
		this.hasArgumentField = false;
		this.argumentFieldOwner = null;
		this.argumentFieldName = null;
		this.argumentFieldDesc = null;
	}

	/**
	 * The same repair for a call one of whose arguments is a static field rather than a parameter of the method.
	 * A separate static factory, not a second constructor, because Java cannot tell a six-String constructor call
	 * apart from the five-String one above plus a single method name.
	 *
	 * @param argumentFieldOwner the class holding that argument, intermediary
	 * @param argumentFieldName  the field in that class, in the runtime namespace
	 * @param argumentFieldDesc  that field's descriptor, with named classes
	 * @param method             the single method of the patched class to look in (intermediary name)
	 */
	public static InjectionCallPointFix withArgumentField(String calleeOwner, String calleeName, String calleeDesc,
			String argumentFieldOwner, String argumentFieldName, String argumentFieldDesc, String method) {
		return new InjectionCallPointFix(calleeOwner, calleeName, calleeDesc, argumentFieldOwner, argumentFieldName,
				argumentFieldDesc, method);
	}

	private InjectionCallPointFix(String calleeOwner, String calleeName, String calleeDesc,
			String argumentFieldOwner, String argumentFieldName, String argumentFieldDesc, String method) {
		this.calleeOwner = RemappingUtils.getClassName(calleeOwner);
		this.calleeName = RemappingUtils.getMethodName(calleeOwner, calleeName, calleeDesc);
		this.calleeDesc = RemappingUtils.mapMethodDescriptor(calleeDesc);
		this.methods = new String[] { method };
		this.hasArgumentField = true;
		this.argumentFieldDesc = RemappingUtils.mapMethodDescriptor(argumentFieldDesc);
		this.argumentFieldOwner = RemappingUtils.getClassName(argumentFieldOwner);
		this.argumentFieldName = RemappingUtils.mapFieldName(this.argumentFieldOwner, argumentFieldName, this.argumentFieldDesc);
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		for (String name : methods) {
			for (MethodNode method : optifine.methods) {
				if (!method.name.equals(name)) continue;
				if (hasCall(method)) continue; //OptiFine kept it: the mixin point is already there

				MethodInsnNode call = vanillaCall(minecraft, name);
				List<AbstractInsnNode> arguments = call == null ? null : argumentsFromVanilla(method, call, optifine.name);

				if (arguments == null && call != null) {
					System.out.println("[OptiFabric] The game's call to " + calleeOwner + '.' + calleeName + " in "
							+ optifine.name + '.' + name + method.desc
							+ " could not be reproduced from its own argument loads, matching this method's parameters by type instead");

					arguments = arguments(method, call.getOpcode());
				}

				if (arguments == null) {
					System.err.println("[OptiFabric] Cannot re-create the call to " + calleeOwner + '.' + calleeName
							+ " in " + optifine.name + '.' + name + method.desc
							+ ": the vanilla method does not have it, or its arguments are neither this method's parameters nor the configured field");

					continue;
				}

				AbstractInsnNode anchor = method.instructions.getFirst();

				for (AbstractInsnNode insn : arguments) method.instructions.insertBefore(anchor, insn);

				method.instructions.insertBefore(anchor, new MethodInsnNode(call.getOpcode(), calleeOwner, calleeName, calleeDesc, false));
				// discard the result: the call exists for the injection point ...
				method.instructions.insertBefore(anchor, new InsnNode(Type.getReturnType(calleeDesc).getSize() == 2
						? Opcodes.POP2 : Type.getReturnType(calleeDesc).getSort() == Type.VOID ? Opcodes.NOP : Opcodes.POP));

				System.out.println("[OptiFabric] Re-created the injection point " + calleeOwner + '.' + calleeName
						+ calleeDesc + " in " + optifine.name + '.' + name + method.desc
						+ " (OptiFine's body is untouched, the result is discarded"
						+ (hasArgumentField ? "; one argument is read from " + argumentFieldOwner + '.' + argumentFieldName : "")
						+ ')');
			}
		}
	}

	/** The call in the game's own version of the method, or null when it is not there either. */
	private MethodInsnNode vanillaCall(ClassNode minecraft, String methodName) {
		if (minecraft == null) return null;

		for (MethodNode method : minecraft.methods) {
			if (!method.name.equals(methodName) || method.instructions == null) continue;

			for (AbstractInsnNode insn : method.instructions.toArray()) {
				if (insn instanceof MethodInsnNode call && call.owner.equals(calleeOwner)
						&& call.name.equals(calleeName) && call.desc.equals(calleeDesc)) {
					return call;
				}
			}
		}

		return null;
	}

	private boolean hasCall(MethodNode method) {
		if (method.instructions == null) return false;

		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn instanceof MethodInsnNode call && call.owner.equals(calleeOwner)
					&& call.name.equals(calleeName) && call.desc.equals(calleeDesc)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * The argument loads the game's own call uses, read straight off the vanilla method.
	 *
	 * <p>This is the only way to tell a call that passed one parameter twice from one that passed two parameters
	 * of the same type: matching by type cannot, and guessing wrong means calling the method with values it was
	 * never given. It also recovers the receiver, which the type-based path can only find among the parameters.
	 *
	 * <p>Null when any of them is not something that can be reproduced faithfully - a parameter of the patched
	 * method at that same slot and of that same type, {@code this} where the patched method is an instance
	 * method, the configured static field, or a constant literal. Anything else (a field read off an object, a
	 * nested call, an array load) means the patched method no longer holds that value in a slot, so this path
	 * declines instead of inventing one.
	 */
	private List<AbstractInsnNode> argumentsFromVanilla(MethodNode method, MethodInsnNode call, String owner) {
		List<Type> wanted = new ArrayList<>();

		if (call.getOpcode() != Opcodes.INVOKESTATIC) wanted.add(Type.getObjectType(calleeOwner));

		wanted.addAll(List.of(Type.getArgumentTypes(calleeDesc)));

		//Walk backwards over exactly the instructions that pushed the receiver and the arguments.
		List<AbstractInsnNode> loads = new ArrayList<>();
		AbstractInsnNode insn = call.getPrevious();

		for (int i = wanted.size() - 1; i >= 0; i--) {
			//Labels, line numbers and frames are not values, and the game's own call is full of them.
			while (insn != null && insn.getOpcode() < 0) insn = insn.getPrevious();

			if (insn == null || producedSize(insn) != wanted.get(i).getSize()) return null;

			//A copy, not the game's own node: the caller inserts what it is handed into the patched method,
			//and an instruction cannot live in two lists at once - inserting the original would leave the
			//vanilla method's list inconsistent (its size no longer matching its nodes).
			loads.add(0, insn.clone(new java.util.HashMap<>()));
			insn = insn.getPrevious();
		}

		for (int i = 0; i < loads.size(); i++) {
			AbstractInsnNode load = loads.get(i);
			Type want = wanted.get(i);

			if (load instanceof VarInsnNode variable) {
				Type actual = slotType(method, owner, variable.var);

				if (actual == null || !actual.equals(want) || variable.getOpcode() != want.getOpcode(Opcodes.ILOAD)) return null;
			} else if (load instanceof FieldInsnNode field) {
				if (!hasArgumentField || load.getOpcode() != Opcodes.GETSTATIC) return null;
				if (!field.owner.equals(argumentFieldOwner) || !field.name.equals(argumentFieldName)
						|| !field.desc.equals(argumentFieldDesc)) return null;
			} else if (!(load instanceof InsnNode || load instanceof IntInsnNode || load instanceof LdcInsnNode)) {
				return null;
			}
		}

		return loads;
	}

	/**
	 * Loads for the call, reconstructed from the patched method's parameters by type: the fallback for when the
	 * game's own argument loads could not be reproduced. Each value is taken from a <em>different</em> parameter
	 * and in order, because rescanning from the first parameter every time made two parameters of the same type
	 * resolve to the same slot - the call then carried the wrong value, silently.
	 *
	 * <p>Null when one of them is neither a parameter of this method nor the one configured static field; the
	 * caller says so and leaves the class alone.
	 */
	private List<AbstractInsnNode> arguments(MethodNode method, int opcode) {
		List<Type> wanted = new ArrayList<>();

		if (opcode != Opcodes.INVOKESTATIC) wanted.add(Type.getObjectType(calleeOwner));

		wanted.addAll(List.of(Type.getArgumentTypes(calleeDesc)));

		Type[] parameters = Type.getArgumentTypes(method.desc);
		boolean instance = (method.access & Opcodes.ACC_STATIC) == 0;
		boolean fieldUsed = false;
		List<AbstractInsnNode> out = new ArrayList<>();
		int next = 0;
		int nextSlot = instance ? 1 : 0;

		for (Type want : wanted) {
			boolean found = false;
			int slot = nextSlot;

			for (int i = next; i < parameters.length; i++) {
				if (parameters[i].equals(want)) {
					out.add(new VarInsnNode(want.getOpcode(Opcodes.ILOAD), slot));
					next = i + 1;
					nextSlot = slot + parameters[i].getSize();
					found = true;
					break;
				}

				slot += parameters[i].getSize();
			}

			if (!found) {
				if (fieldUsed || !hasArgumentField || !argumentFieldDesc.equals(want.getDescriptor())) return null;

				fieldUsed = true;
				out.add(new FieldInsnNode(Opcodes.GETSTATIC, argumentFieldOwner, argumentFieldName, argumentFieldDesc));
			}
		}

		return out;
	}

	/** The type a load of the given slot would carry in this method, or null when no parameter lives there. */
	private static Type slotType(MethodNode method, String owner, int slot) {
		boolean instance = (method.access & Opcodes.ACC_STATIC) == 0;

		if (instance && slot == 0) return Type.getObjectType(owner);

		int at = instance ? 1 : 0;

		for (Type parameter : Type.getArgumentTypes(method.desc)) {
			if (at == slot) return parameter;

			at += parameter.getSize();
		}

		return null;
	}

	/** How many stack values the instruction pushes, or -1 when it pushes something other than one value. */
	private static int producedSize(AbstractInsnNode insn) {
		if (insn instanceof VarInsnNode) {
			return switch (insn.getOpcode()) {
				case Opcodes.ILOAD, Opcodes.FLOAD, Opcodes.ALOAD -> 1;
				case Opcodes.LLOAD, Opcodes.DLOAD -> 2;
				default -> -1;
			};
		}

		if (insn instanceof FieldInsnNode field) {
			return field.getOpcode() == Opcodes.GETSTATIC ? Type.getType(field.desc).getSize() : -1;
		}

		int opcode = insn.getOpcode();

		if (opcode == Opcodes.LCONST_0 || opcode == Opcodes.LCONST_1 || opcode == Opcodes.DCONST_0 || opcode == Opcodes.DCONST_1) return 2;
		if (opcode >= Opcodes.ACONST_NULL && opcode <= Opcodes.ICONST_5) return 1;
		if (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2) return 1;
		if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) return 1;
		if (opcode == Opcodes.LDC) return insn instanceof LdcInsnNode ldc && (ldc.cst instanceof Long || ldc.cst instanceof Double) ? 2 : 1;

		return -1;
	}
}
