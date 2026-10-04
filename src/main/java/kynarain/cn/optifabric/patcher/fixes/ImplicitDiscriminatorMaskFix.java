/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.patcher.fixes;

import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Makes an implicit {@code @ModifyVariable} discriminator count exactly one local again.
 *
 * <p>Mixin resolves an implicit discriminator (an {@code @At("STORE")} / {@code @At("LOAD")} point with no
 * {@code ordinal}, {@code index} or {@code name}) by walking the target method's locals and counting every entry
 * whose descriptor equals the handler's captured type - see {@code LocalVariableDiscriminator.findImplicitLocal}.
 * Exactly one match is required; two or more throw {@code InvalidImplicitDiscriminatorException}, the injection
 * point is dropped, and with {@code require = 1} the whole class fails to transform. One real case is Porting
 * Lib's {@code porting_lib_blocks} LevelRendererMixin on 1.21.1, which wraps the block-entity
 * {@code Iterator} in {@code class_761.method_22710} and finds two ({@code Found 2 candidate variables but
 * exactly 1 is required}).
 *
 * <p>The types that count come from {@code org.spongepowered.asm.util.Locals.getLocalVariableAt}, which reads
 * the {@code LocalVariableTable} first and - this is the part that matters here - falls back to a table ASM's
 * own data-flow {@code Analyzer} generates from the code ({@code Locals.getGeneratedLocalVariableTable})
 * whenever the real table has no entry in range for that slot. The ambiguous locals are usually from that
 * generated table: OptiFine's recompiled body holds iterators in slots its {@code LocalVariableTable} does not
 * describe (the generated entries are even named {@code varNN} after their slot). Retyping an existing entry
 * therefore cannot reach them.
 *
 * <p>Appending one is what does reach them: {@code getLocalVariableAt} scans {@code method.localVariables} in
 * order and keeps the <em>last</em> in-range entry for a slot, so a descriptor appended at the end wins over
 * both the shipped table and the generated one. This fixer appends a {@code Ljava/lang/Object;} entry covering
 * the mixin's own slice for every same-typed local except one, which leaves the discriminator with exactly one
 * candidate - the local the handler means.
 *
 * <p>It is a deliberate mis-type in a debug attribute. The JVM verifier reads the {@code StackMapTable},
 * {@code max_locals}, the handlers and the code; it never reads the {@code LocalVariableTable}, so the class
 * verifies and runs unchanged. What does see it is anything that reads the LVT: Mixin's own {@code Locals}
 * (which is the point), MixinExtras' {@code @Local} sugar, debuggers, agents and bytecode scanners - inside the
 * configured slice only. It never touches a primitive, never touches an argument slot, never creates or removes
 * a local, and never changes the code, the slots, the ranges, the names or {@code maxLocals}.
 *
 * <p>Opt-in by registration: the fixer is constructed with one exact target (method, captured type and the two
 * instructions that bound the mixin's slice) and does nothing at all when that shape is not present - no slice
 * boundaries, no second same-typed local, no local table to append to.
 */
public class ImplicitDiscriminatorMaskFix implements ClassFixer {

	/** The method the mixin injects into, by intermediary name ({@code method_22710} = LevelRenderer.renderLevel). */
	private final String methodName;
	/** The descriptor the discriminator compares against - the {@code @ModifyVariable} handler's captured type. */
	private final String typeDesc;
	/** The mixin's {@code @Slice(from = @At(INVOKE, ...))}, as {@code owner.name+desc}. */
	private final String sliceFrom;
	/** The mixin's {@code @Slice(to = @At(INVOKE, ...))}, as {@code owner.name+desc}. */
	private final String sliceTo;

	public ImplicitDiscriminatorMaskFix(String methodName, String typeDesc, String sliceFrom, String sliceTo) {
		this.methodName = methodName;
		this.typeDesc = typeDesc;
		this.sliceFrom = sliceFrom;
		this.sliceTo = sliceTo;
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		for (MethodNode method : optifine.methods) {
			if (method.name.equals(methodName)) {
				mask(optifine, method);
			}
		}
	}

	private void mask(ClassNode owner, MethodNode method) {
		if (method.localVariables == null || method.localVariables.isEmpty()) {
			//Without a real table the mixin's own lookups are served by the generated one alone, and appending to an
			//empty list would make the generated table unreachable for every slot instead of shadowing two of them.
			return;
		}

		InsnList insns = method.instructions;
		AbstractInsnNode from = findCall(insns, sliceFrom);
		AbstractInsnNode to = findCall(insns, sliceTo);

		if (from == null || to == null || insns.indexOf(to) <= insns.indexOf(from)) {
			return;
		}

		Set<Integer> candidates = sameTypedSlots(insns);

		if (candidates.size() < 2) {
			return;
		}

		int keep = keeper(insns, candidates, insns.indexOf(from), insns.indexOf(to));
		int firstNonArg = firstNonArgLocalIndex(method);

		if (keep < 0 || keep < firstNonArg) {
			return;
		}

		LabelNode start = new LabelNode();
		LabelNode end = new LabelNode();
		//The shadow has to start at the top of the method, not at the slice. Locals.getLocalsAt fills a slot's entry
		//when it walks past the instruction that establishes it and then carries that same node forward across
		//frames without asking again; a slot whose Iterator value is established *before* the slice therefore keeps
		//that Iterator entry at the mixin's stores even with a shadow that covers them, which is exactly how the
		//first - and only usable - injection point stayed at "Found 2". Ending at the slice keeps the method's tail
		//out of it.
		insns.insert(start);

		AbstractInsnNode after = to.getNext();
		if (after != null) {
			insns.insertBefore(after, end);
		} else {
			insns.add(end);
		}

		StringBuilder masked = new StringBuilder();

		for (int slot : candidates) {
			if (slot == keep || slot < firstNonArg) {
				continue;
			}

			//Appended at the end on purpose: Locals.getLocalVariableAt keeps the *last* in-range entry for a slot,
			//so this one wins over both the shipped table and the table generated from the code.
			method.localVariables.add(new LocalVariableNode("optifabric$ambiguous" + slot, "Ljava/lang/Object;",
					null, start, end, slot));

			if (masked.length() > 0) {
				masked.append(", ");
			}

			masked.append(slot);
		}

		if (masked.length() == 0) {
			return;
		}

		System.out.println("[OptiFabric] Masked " + Type.getType(typeDesc).getClassName() + " local(s) " + masked
				+ " in " + owner.name + '.' + methodName + " as java.lang.Object inside the slice " + sliceFrom + " .. "
				+ sliceTo + " so its implicit @ModifyVariable discriminator counts only slot " + keep + " (of "
				+ candidates.size() + " same-typed locals)");
	}

	/**
	 * Every slot the method ever puts a value of the captured type into. This is a syntactic over-approximation -
	 * it cannot say when a slot holds the type, only that it can - which is what the mask wants: broad enough to
	 * catch every candidate the discriminator could count, narrow enough to leave every other local alone. The
	 * three shapes are the ones javac emits for a local of a reference type: the result of a call, a checked cast,
	 * and a copy from another local of the same type.
	 */
	private Set<Integer> sameTypedSlots(InsnList insns) {
		Set<Integer> slots = new LinkedHashSet<>();

		for (AbstractInsnNode insn : insns) {
			if (insn instanceof VarInsnNode var && var.getOpcode() == Opcodes.ASTORE) {
				AbstractInsnNode previous = previousReal(insn);

				if (producesType(previous)) {
					slots.add(var.var);
				} else if (previous instanceof TypeInsnNode cast && cast.getOpcode() == Opcodes.CHECKCAST
						&& ('L' + cast.desc + ';').equals(typeDesc)) {
					slots.add(var.var);
				} else if (previous instanceof VarInsnNode copy && copy.getOpcode() == Opcodes.ALOAD && slots.contains(copy.var)) {
					slots.add(var.var);
				}
			} else if (insn instanceof MethodInsnNode call && call.owner.equals(Type.getType(typeDesc).getInternalName())) {
				//A call *on* the type: its receiver was loaded from one of these slots.
				AbstractInsnNode previous = previousReal(insn);

				if (previous instanceof VarInsnNode receiver && receiver.getOpcode() == Opcodes.ALOAD) {
					slots.add(receiver.var);
				}
			}
		}

		return slots;
	}

	private boolean producesType(AbstractInsnNode insn) {
		if (insn instanceof MethodInsnNode call) {
			return call.desc.endsWith(')' + typeDesc);
		}

		if (insn instanceof TypeInsnNode cast && cast.getOpcode() == Opcodes.CHECKCAST) {
			return ('L' + cast.desc + ';').equals(typeDesc);
		}

		return false;
	}

	/**
	 * The one local to leave as the captured type: the slot the first {@code ASTORE} inside the slice fills from a
	 * call of that type - the store the mixin was written against. The discriminator's own check accepts an
	 * injection point only when the single candidate is the slot that store writes to, so keeping this one keeps
	 * the injection working instead of merely silencing the error.
	 */
	private int keeper(InsnList insns, Set<Integer> candidates, int from, int to) {
		int fromProducer = -1;
		int fallback = -1;

		for (int index = from; index <= to && index < insns.size(); index++) {
			AbstractInsnNode insn = insns.get(index);

			if (insn instanceof VarInsnNode var && var.getOpcode() == Opcodes.ASTORE) {
				if (producesType(previousReal(insn))) {
					fromProducer = var.var;
					break;
				}

				if (fallback < 0 && candidates.contains(var.var)) {
					fallback = var.var;
				}
			}
		}

		return fromProducer >= 0 ? fromProducer : fallback;
	}

	private static AbstractInsnNode findCall(InsnList insns, String ownerNameDesc) {
		for (AbstractInsnNode insn : insns) {
			if (insn instanceof MethodInsnNode call
					&& (call.owner + '.' + call.name + call.desc).equals(ownerNameDesc)) {
				return insn;
			}
		}

		return null;
	}

	private static AbstractInsnNode previousReal(AbstractInsnNode insn) {
		AbstractInsnNode previous = insn.getPrevious();

		while (previous instanceof LabelNode || previous instanceof LineNumberNode || previous instanceof FrameNode) {
			previous = previous.getPrevious();
		}

		return previous;
	}

	private static int firstNonArgLocalIndex(MethodNode method) {
		int index = (method.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;

		for (Type argument : Type.getArgumentTypes(method.desc)) {
			index += argument.getSize();
		}

		return index;
	}
}
