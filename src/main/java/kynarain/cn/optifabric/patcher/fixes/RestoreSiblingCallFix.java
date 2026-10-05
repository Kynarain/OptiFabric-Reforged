/*
 * New in the 1.21.x series of this port (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Puts the game's own call back next to the call OptiFine compiled in its place, when both take exactly the same
 * arguments and only the callee (and its return type) differ.
 *
 * This is the second shape of "the call a mixin wraps is gone", and InjectionCallPointFix cannot repair it.
 * InjectionCallPointFix rebuilds the call from the parameters of the method it sits in and inserts it at the very
 * top of the method. Here one of the arguments is not a parameter at all: it is a lambda the method creates a few
 * instructions earlier, so there is nothing to load it from. What *is* true is that OptiFine's replacement call is
 * executed with exactly the arguments the game's call takes - the difference is only the callee and the return
 * type. So the arguments are already on the operand stack where OptiFine's call is, and they can simply be
 * duplicated: DUP or DUP2 copies them, the game's call is made with the copies, and its result is discarded so
 * OptiFine's own call still runs and still produces the value the rest of the method reads.
 *
 * Measured on 1.21.1 with OptiFine HD U J1 (the class OptiFabric serves, read out of a pristine .optifine cache):
 * sodium 0.6.13 / 0.8.13's sodium-common.mixins.json:features.render.world.sky.ClientLevelMixin is a @Redirect
 * whose refmap target is a call to
 *
 *   net/minecraft/class_6491.method_24895(Lnet/minecraft/class_243;Lnet/minecraft/class_6491$class_4859;)Lnet/minecraft/class_243;
 *
 * inside class_638.method_23777 (the game's ClientLevel.getSkyColor). The game's body has it at instruction 42:
 *
 *   33: aload 4      // the Vec3d the method built
 *   35: aload 5      // the class_4543 from method_22385()
 *   37: invokedynamic fetch:(Lnet/minecraft/class_4543;)Lnet/minecraft/class_6491$class_4859;
 *   42: invokestatic  class_6491.method_24895(Lnet/minecraft/class_243;Lnet/minecraft/class_6491$class_4859;)Lnet/minecraft/class_243;
 *
 * OptiFine's body has no such call. It builds its own Vec3M(0,0,0), makes a resolver lambda that captures it, and
 * aims the same two arguments at a method of its own:
 *
 *   45: aload 4
 *   47: aload 6      // OptiFine's Vec3M
 *   49: aload 5
 *   51: invokedynamic fetch:(Lnet/optifine/Vec3M;Lnet/minecraft/class_4543;)Lnet/minecraft/class_6491$class_4859;
 *   56: invokestatic  class_6491.sampleM(Lnet/minecraft/class_243;Lnet/minecraft/class_6491$class_4859;)Lnet/optifine/Vec3M;
 *
 * The two calls take the same argument types in the same order; only the callee and the return type differ. So
 * DUP2 in front of OptiFine's call duplicates the pair, the game's call is made from the copies and its class_243
 * result is popped, and OptiFine's call runs exactly as before - one extra call per frame, with the arguments the
 * method had already computed, and no change to what OptiFine's own code reads.
 *
 * What that costs: the re-created call's result is discarded, so a mixin that redirects it computes a colour
 * nothing reads - its own visual change is inert while OptiFine's path still governs. That is the same trade-off
 * InjectionCallPointFix documents, and it is the choice this fixer makes deliberately: a client that starts with
 * one inert sodium tweak beats a client that does not start at all.
 *
 * Two structural conditions are checked before anything is touched, and the class is left alone when either fails:
 *   - the game's call and OptiFine's call must have identical argument types (the duplication assumes exactly the
 *     values OptiFine's call is about to consume);
 *   - the game's call must be reachable with one DUP or one DUP2, i.e. it passes one or two argument slots counting
 *     the receiver of a non-static call. Anything wider is reported instead of guessed.
 * The opcode of the game's call is taken from the game's own class, so a static, virtual or interface call is
 * reproduced exactly, and a method that already contains the game's call is skipped entirely - which is what keeps
 * this inert on every release where OptiFine kept the call.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.Arrays;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import kynarain.cn.optifabric.util.RemappingUtils;

public class RestoreSiblingCallFix implements ClassFixer {
	/** The class both calls go to, as an intermediary name ({@code class_6491}). */
	private final String owner;
	/** The call OptiFine's body makes, in the runtime namespace. */
	private final String siblingName;
	private final String siblingDesc;
	/** The call the game's own body makes, in the runtime namespace. */
	private final String vanillaName;
	private final String vanillaDesc;
	/** The methods of the patched class to look in, by intermediary name. */
	private final String[] methods;

	/**
	 * @param owner       the class both calls go to, intermediary ({@code class_6491})
	 * @param siblingName the name OptiFine's body calls it by ({@code sampleM})
	 * @param siblingDesc that call's descriptor, with named classes and OptiFine's own return type
	 * @param vanillaName the name the game's own body calls it by ({@code method_24895})
	 * @param vanillaDesc that call's descriptor, with named classes; its arguments have to be exactly
	 *                    {@code siblingDesc}'s and its return type has to be a value, or nothing is changed
	 * @param methods     the methods of the patched class to look in, intermediary ({@code method_23777})
	 */
	public RestoreSiblingCallFix(String owner, String siblingName, String siblingDesc, String vanillaName,
			String vanillaDesc, String... methods) {
		this.owner = RemappingUtils.getClassName(owner);
		this.siblingName = RemappingUtils.getMethodName(owner, siblingName, siblingDesc);
		this.siblingDesc = RemappingUtils.mapMethodDescriptor(siblingDesc);
		this.vanillaName = RemappingUtils.getMethodName(owner, vanillaName, vanillaDesc);
		this.vanillaDesc = RemappingUtils.mapMethodDescriptor(vanillaDesc);
		this.methods = methods;
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		Type[] vanillaArguments = Type.getArgumentTypes(vanillaDesc);
		Type[] siblingArguments = Type.getArgumentTypes(siblingDesc);

		if (!Arrays.equals(vanillaArguments, siblingArguments)) {
			System.err.println("[OptiFabric] " + owner + '.' + vanillaName + vanillaDesc + " does not take the same "
					+ "arguments as " + siblingName + siblingDesc + ", so the call cannot be put back next to it");

			return;
		}

		Type returns = Type.getReturnType(vanillaDesc);

		if (returns.getSort() == Type.VOID) {
			System.err.println("[OptiFabric] " + owner + '.' + vanillaName + vanillaDesc + " returns nothing, so a "
					+ "duplicated call would have no result to discard");

			return;
		}

		for (String name : methods) {
			for (MethodNode method : optifine.methods) {
				if (!method.name.equals(name) || method.instructions == null) continue;
				//OptiFine kept the game's own call here: the mixin's point is already there and OptiFine's call to
				//its own method is its business.
				if (hasCall(method)) continue;

				int opcode = vanillaOpcode(minecraft, name);

				if (opcode < 0) {
					System.err.println("[OptiFabric] Cannot put the call to " + owner + '.' + vanillaName + vanillaDesc
							+ " back in " + optifine.name + '.' + name + method.desc
							+ ": the game's own method does not make it");

					continue;
				}

				int slots = (opcode == Opcodes.INVOKESTATIC ? 0 : 1);
				for (Type argument : vanillaArguments) slots += argument.getSize();

				if (slots != 1 && slots != 2) {
					System.err.println("[OptiFabric] Cannot duplicate the " + slots + " argument slot(s) of "
							+ owner + '.' + vanillaName + vanillaDesc + " in " + optifine.name + '.' + name + method.desc
							+ " with one DUP or DUP2");

					continue;
				}

				int duplicated = 0;

				for (AbstractInsnNode insn : method.instructions.toArray()) {
					if (!(insn instanceof MethodInsnNode call)) continue;
					if (!call.owner.equals(owner) || !call.name.equals(siblingName) || !call.desc.equals(siblingDesc)) continue;
					//The duplicated values have to be exactly the arguments (and receiver) this call consumes.
					if (call.getOpcode() != opcode) continue;

					method.instructions.insertBefore(call, new InsnNode(slots == 1 ? Opcodes.DUP : Opcodes.DUP2));
					method.instructions.insertBefore(call, new MethodInsnNode(opcode, owner, vanillaName, vanillaDesc,
							call.itf));
					method.instructions.insertBefore(call, new InsnNode(returns.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
					duplicated++;
				}

				if (duplicated > 0) {
					System.out.println("[OptiFabric] Put the call " + owner + '.' + vanillaName + vanillaDesc + " back in "
							+ optifine.name + '.' + name + method.desc + " " + duplicated + " time(s), next to "
							+ siblingName + siblingDesc + " - its arguments are duplicated and its result is discarded, so "
							+ "OptiFine's own call and everything it feeds is untouched");
				}
			}
		}
	}

	/** Opcode of that call in the game's own version of the method, or -1 when it is not there either. */
	private int vanillaOpcode(ClassNode minecraft, String methodName) {
		if (minecraft == null) return -1;

		for (MethodNode method : minecraft.methods) {
			if (!method.name.equals(methodName) || method.instructions == null) continue;

			for (AbstractInsnNode insn : method.instructions.toArray()) {
				if (insn instanceof MethodInsnNode call && call.owner.equals(owner) && call.name.equals(vanillaName)
						&& call.desc.equals(vanillaDesc)) {
					return call.getOpcode();
				}
			}
		}

		return -1;
	}

	private boolean hasCall(MethodNode method) {
		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn instanceof MethodInsnNode call && call.owner.equals(owner) && call.name.equals(vanillaName)
					&& call.desc.equals(vanillaDesc)) {
				return true;
			}
		}

		return false;
	}
}
