/*
 * New in the 1.21.x series of this port (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Re-creates a field store OptiFine's recompile moved out of a method, when the method the mixin names still exists
 * but no longer performs that store.
 *
 * InjectionCallPointFix handles the same family for a *call*: it re-creates the call at the top of the method so the
 * mixin's @At(INVOKE) anchor is there again. A @WrapOperation or @Redirect anchored on a FIELD has no such fallback -
 * the store either happens in the method the annotation names, or Mixin finds nothing and, with the mod config's
 * require = 1, fails the whole class.
 *
 * Measured on 1.21.1 with OptiFine HD U J1, class_7764 = SpriteContents (the class OptiFabric serves, read out of a
 * pristine .optifine cache):
 *
 *   the game's class_7764.<init>(IIIILnet/minecraft/class_3298;)V writes the sprite's own image on the way out:
 *     ...
 *     aload_0
 *     aload_3                       // the NativeImage parameter
 *     putfield  class_7764.field_40539 : Lnet/minecraft/class_1011;
 *
 *   OptiFine's class_7764 keeps that five-argument constructor's shape but turns it into a 13-byte delegator:
 *     aload_0 / iload_1 / iload_2 / iload_3 / aload 4
 *     aconst_null
 *     invokespecial <init>(IIIILnet/minecraft/class_3298;Lnet/minecraftforge/client/textures/ForgeTextureMetadata;)V
 *     return
 *
 *   and moves the vanilla body - the field store at its original offset included - into that new private constructor.
 *   OptiFine's own callers use the new one, so they are untouched by this fixer; what is missing is the store on the
 *   vanilla-shaped path, which is the one sodium's features.textures.mipmaps / .scan SpriteContentsMixin wraps:
 *
 *     @WrapOperation(method = "<init>", at = @At(value = "FIELD", opcode = 181,
 *         target = "Lnet/minecraft/class_7764;field_40539:Lnet/minecraft/class_1011;"))   // sodium$beforeGenerateMipLevels
 *
 *   The annotation names no descriptor, so Mixin's MemberInfo.matches treats it as unconstrained and
 *   TargetSelectors.findRootTargets binds to the first exact match - which the class file lists as the delegator
 *   (MemberInfo.getMaxMatchCount() = Quantifier.getClampedMax() = 1 for the unbounded default). Hence "expected 1
 *   invocation(s) but 0 succeeded" although both constructors exist.
 *
 * Why this is the smallest change that works, and what it costs: the store is re-created immediately before the
 * delegator's RETURN, so the value OptiFine's own constructor wrote on the way through is overwritten by the same
 * argument the game's own constructor stored there. The stack is empty at that point (an invokespecial <init> leaves
 * nothing behind), the code length grows by three instructions that push and consume exactly two values, and no
 * label, stack map frame or existing offset moves. OptiFine's body, its scaleFactor store and its own callers all
 * stay as they are - unlike restoring the vanilla body, which would drop OptiFine's scaleFactor on this path and
 * make rescale() a no-op through its own guard.
 */
package kynarain.cn.optifabric.patcher.fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import kynarain.cn.optifabric.util.RemappingUtils;

public class InjectionFieldPointFix implements ClassFixer {
	/** The class that declares the field, in intermediary notation ({@code class_7764}). */
	private final String owner;
	/** The field, in the runtime namespace. */
	private final String fieldName;
	private final String fieldDesc;
	/** The local slot holding the value to store - the receiver is always slot 0. */
	private final int valueSlot;
	/** The load that reads that slot: FLOAD for a float field, ALOAD for a reference, and so on. */
	private final int valueOpcode;
	/** The methods of the patched class to look in, intermediary names. */
	private final String[] methods;

	/**
	 * @param owner     the class declaring the field, intermediary ({@code class_7764})
	 * @param fieldName the field in the runtime namespace ({@code field_40539})
	 * @param fieldDesc its descriptor with named classes ({@code Lnet/minecraft/class_1011;})
	 * @param valueSlot the local slot the value is read from ({@code 3} for the fourth parameter)
	 * @param methods   the methods of the patched class to look in (intermediary names)
	 */
	public InjectionFieldPointFix(String owner, String fieldName, String fieldDesc, int valueSlot, String... methods) {
		this.owner = RemappingUtils.getClassName(owner);
		this.fieldDesc = RemappingUtils.mapMethodDescriptor(fieldDesc);
		this.fieldName = RemappingUtils.mapFieldName(this.owner, fieldName, this.fieldDesc);
		this.valueSlot = valueSlot;
		//FLOAD for a float field, DLOAD/LLOAD for the wide ones, ALOAD for a reference: reading the slot with the wrong
		//kind of load is what the verifier rejects as "Bad local variable type", and a float field is easy to get wrong.
		this.valueOpcode = Type.getType(this.fieldDesc).getOpcode(Opcodes.ILOAD);
		this.methods = methods;
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		FieldNode field = null;

		for (FieldNode candidate : optifine.fields) {
			if (candidate.name.equals(fieldName) && candidate.desc.equals(fieldDesc)) {
				field = candidate;

				break;
			}
		}

		if (field == null) {
			System.err.println("[OptiFabric] " + owner + " has no field " + fieldName + ':' + fieldDesc
					+ ", so the injection point cannot be re-created");

			return;
		}

		for (String name : methods) {
			for (MethodNode method : optifine.methods) {
				if (!method.name.equals(name) || method.instructions == null) continue;

				//OptiFine kept the store here: the anchor is already there.
				if (hasStore(method)) continue;

				AbstractInsnNode last = method.instructions.getLast();

				while (last != null && last.getOpcode() < 0) last = last.getPrevious();

				if (last == null || last.getOpcode() != Opcodes.RETURN) {
					System.err.println("[OptiFabric] Cannot re-create the store of " + owner + '.' + fieldName + " in "
							+ optifine.name + '.' + name + method.desc + ": the method does not end in RETURN");

					continue;
				}

				method.instructions.insertBefore(last, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(last, new VarInsnNode(valueOpcode, valueSlot));
				method.instructions.insertBefore(last, new FieldInsnNode(Opcodes.PUTFIELD, owner, fieldName, fieldDesc));

				System.out.println("[OptiFabric] Re-created the injection point " + owner + '.' + fieldName + ':'
						+ fieldDesc + " in " + optifine.name + '.' + name + method.desc
						+ " (OptiFine's own body and callers are untouched, the stored value is the method's own argument "
						+ valueSlot + ')');
			}
		}
	}

	private boolean hasStore(MethodNode method) {
		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn instanceof FieldInsnNode store && store.getOpcode() == Opcodes.PUTFIELD
					&& store.owner.equals(owner) && store.name.equals(fieldName) && store.desc.equals(fieldDesc)) {
				return true;
			}
		}

		return false;
	}
}
