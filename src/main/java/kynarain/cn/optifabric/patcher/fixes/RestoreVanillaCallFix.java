/*
 * New in the 1.21.x series of this port (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Points a call OptiFine re-aimed at a method of its own back at the method the game's own body calls.
 *
 * This is the case InjectionCallPointFix cannot repair. That fixer re-creates a missing call in front of
 * OptiFine's body, which is right when what the mixin wraps is a pure getter: the re-created call computes a value
 * nothing reads. When the call the mixin names *does* something, a re-created call is a second execution of it, and
 * putting it in front of the method is worse still - Mixin injects its handler at the re-created instruction, so a
 * handler that draws would draw before the whole frame. Here OptiFine did not drop the call: it compiled the very
 * same call to a different callee, with the same receiver and the same arguments. So the repair is to aim that one
 * call back where the game aimed it, and there is no second call to run.
 *
 * Measured on 1.21.1 with OptiFine HD U J1 (the class OptiFabric serves, read out of a pristine .optifine cache):
 *
 *   the game's class_761.method_22710 = LevelRenderer.renderLevel, first of two particle calls
 *     aload_0
 *     getfield  class_761.field_4088 : Lnet/minecraft/class_310;    // this.minecraft
 *     getfield  class_310.field_1713 : Lnet/minecraft/class_702;    // minecraft.particleEngine
 *     aload 5 / aload_3 / fload 9                                   // LightTexture, Camera, tickDelta
 *     invokevirtual class_702.method_3049(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;F)V
 *
 *   OptiFine's class_761.method_22710, same place, three times (three mutually exclusive branches: the first is
 *   jumped over, and the other two are the two arms of Shaders.isParticlesBeforeDeferred - so exactly one of the
 *   three runs per frame, as exactly one of the game's two did)
 *     aload_0
 *     getfield  class_761.field_4088 : Lnet/minecraft/class_310;    // byte for byte the same
 *     getfield  class_310.field_1713 : Lnet/minecraft/class_702;    // byte for byte the same
 *     aload 5 / aload_3 / fload 9                                   // byte for byte the same
 *     aload 20                                                      // OptiFine's own local: the frustum
 *     invokevirtual class_702.render(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;FLnet/minecraft/class_4604;)V
 *
 * The receiver and the three arguments are the same instructions; the only difference is the callee, plus one
 * argument OptiFine's method takes and the game's does not. OptiFine's class_702.method_3049 still exists in the
 * patched class - OptiFine recompiled it into a six-instruction forwarder to its own render(..., null) - so the
 * method the mixin's refmap names is present, and the mixin's point is a *call* to it.
 *
 * That is what breaks carryon 2.2.6.13 on 1.21.1: its LevelRendererMixin is
 *
 *   @Inject(method = "renderLevel", at = @At(target = "...ParticleEngine;render(LightTexture;Camera;F)V"))
 *   private void onRenderLevel(..., CallbackInfo ci, @Local PoseStack poseStack)
 *
 * which the refmap resolves to class_702.method_3049(Lclass_765;Lclass_4184;F)V inside class_761.method_22710.
 * OptiFine's body has no such call, so Mixin scans 0 targets and, with carryon.fabric.mixins.json's
 * defaultRequire = 1, fails the whole class:
 *
 *   InjectionError: Critical injection failure: Callback method onRenderLevel(...)V in
 *   carryon.fabric.mixins.json:LevelRendererMixin from mod carryon failed injection check, (0/1) succeeded.
 *   Scanned 0 target(s). Using refmap carryon.refmap.json
 *
 * which surfaces as "Mixin transformation of net.minecraft.class_761 failed" during OptiFine's own Reflector
 * bootstrap and kills the client before the title screen (reproduced against OptiFabric 2.2.8, log and counters in
 * the lane report).
 *
 * What the redirect costs, and why it is the smallest change that works:
 *   - The arguments OptiFine's method does not take are dropped by replacing the instruction that pushes them
 *     with NOP, not by removing it: the instruction list, every offset, every label and every stack map frame stay
 *     exactly where they were in a method of several thousand instructions. Reading a local and discarding it has
 *     no effect.
 *   - The one argument in this shape is class_4604 = the camera frustum, and OptiFine's render uses it for one
 *     thing only: "if (frustum != null && particle.shouldCull() && !frustum.isVisible(box)) continue;". Passing
 *     null - which is exactly what OptiFine's own method_3049 forwarder does - skips that per-particle frustum
 *     culling and nothing else. OptiFine's body, its shader stages (Shaders.beginParticles/endParticles), its
 *     AFTER_PARTICLES dispatch and the whole rest of renderLevel are untouched, and the particle pass still runs
 *     once, in the same place in the frame, through OptiFine's own method.
 *   - It is not registered for a release where the shape is not there: a method that still has the call the mixin
 *     names is left alone entirely, so this can only ever fire on the class and method it was written for.
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
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import kynarain.cn.optifabric.util.RemappingUtils;

public class RestoreVanillaCallFix implements ClassFixer {
	/** The class the call goes to, as an intermediary name ({@code class_702}). */
	private final String owner;
	/** The method OptiFine's body calls, in the runtime namespace. */
	private final String optifineName;
	private final String optifineDesc;
	/** The method the game's own body calls, in the runtime namespace. */
	private final String vanillaName;
	private final String vanillaDesc;
	/** The methods of the patched class to look in, by intermediary name. */
	private final String[] methods;

	/**
	 * @param calleeOwner  the class the call goes to, intermediary ({@code class_702})
	 * @param optifineName the name OptiFine's body calls it by ({@code render})
	 * @param optifineDesc that call's descriptor, with named classes and OptiFine's own extra arguments
	 * @param vanillaName  the name the game's own body calls it by ({@code method_3049})
	 * @param vanillaDesc  that call's descriptor, with named classes; its arguments have to be a prefix of
	 *                     {@code optifineDesc}'s and its return type has to match, or nothing is changed
	 * @param methods      the methods of the patched class to look in, intermediary ({@code method_22710})
	 */
	public RestoreVanillaCallFix(String calleeOwner, String optifineName, String optifineDesc, String vanillaName,
			String vanillaDesc, String... methods) {
		this.owner = RemappingUtils.getClassName(calleeOwner);
		this.optifineName = RemappingUtils.getMethodName(calleeOwner, optifineName, optifineDesc);
		this.optifineDesc = RemappingUtils.mapMethodDescriptor(optifineDesc);
		this.vanillaName = RemappingUtils.getMethodName(calleeOwner, vanillaName, vanillaDesc);
		this.vanillaDesc = RemappingUtils.mapMethodDescriptor(vanillaDesc);
		this.methods = methods;
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		Type[] wanted = Type.getArgumentTypes(vanillaDesc);
		Type[] have = Type.getArgumentTypes(optifineDesc);

		if (have.length <= wanted.length || !Type.getReturnType(vanillaDesc).equals(Type.getReturnType(optifineDesc))) {
			System.err.println("[OptiFabric] " + vanillaDesc + " is not " + optifineDesc
					+ " with arguments dropped, so the call cannot be aimed back at " + owner + '.' + vanillaName);

			return;
		}

		for (int i = 0; i < wanted.length; i++) {
			if (!wanted[i].equals(have[i])) {
				System.err.println("[OptiFabric] " + vanillaDesc + " does not start with the arguments of "
						+ optifineDesc + " (argument " + i + " is " + wanted[i] + " against " + have[i]
						+ "), so the call cannot be aimed back at " + owner + '.' + vanillaName);

				return;
			}
		}

		List<Type> dropped = new ArrayList<>(List.of(have).subList(wanted.length, have.length));
		int redirected = 0;

		for (String name : methods) {
			for (MethodNode method : optifine.methods) {
				if (!method.name.equals(name) || method.instructions == null) continue;
				//OptiFine kept the game's own call here: the mixin's point is already there and OptiFine's call to
				//its own method is its business.
				if (hasCall(method, vanillaName, vanillaDesc)) continue;

				for (AbstractInsnNode insn : method.instructions.toArray()) {
					if (!(insn instanceof MethodInsnNode call)) continue;
					if (!call.owner.equals(owner) || !call.name.equals(optifineName) || !call.desc.equals(optifineDesc)) continue;

					if (!dropArguments(method, call, dropped)) {
						System.err.println("[OptiFabric] Cannot aim the call to " + owner + '.' + optifineName
								+ optifineDesc + " in " + optifine.name + '.' + name + method.desc
								+ " back at " + owner + '.' + vanillaName + vanillaDesc
								+ ": the argument(s) " + dropped + " are not each pushed by one instruction");

						continue;
					}

					call.name = vanillaName;
					call.desc = vanillaDesc;
					redirected++;
				}
			}
		}

		if (redirected > 0) {
			System.out.println("[OptiFabric] Aimed " + redirected + " call(s) in " + optifine.name + " at "
					+ owner + '.' + vanillaName + vanillaDesc + " again, which is the call the game's own method makes"
					+ " and the injection point that named it expects (OptiFine's own method "
					+ optifineName + optifineDesc + " stays, it is simply not what this call is any more)");
		}
	}

	/**
	 * Replaces the instruction that pushes each of the trailing arguments OptiFine's method has and the game's does
	 * not with NOP, so the stack under the call matches the game's descriptor. False when one of them is not pushed
	 * by a single instruction that can be checked.
	 */
	private boolean dropArguments(MethodNode method, MethodInsnNode call, List<Type> dropped) {
		AbstractInsnNode cursor = previousInstruction(call);

		for (int i = dropped.size() - 1; i >= 0; i--) {
			if (cursor == null || !pushesOneValue(cursor)) return false;
			if (!pushes(method, cursor, dropped.get(i))) return false;

			method.instructions.set(cursor, new InsnNode(Opcodes.NOP));
			cursor = previousInstruction(cursor);
		}

		return true;
	}

	/** The previous real instruction, skipping the pseudo-instructions that carry no operand. */
	private static AbstractInsnNode previousInstruction(AbstractInsnNode from) {
		AbstractInsnNode node = from.getPrevious();

		while (node != null && (node.getOpcode() < 0)) node = node.getPrevious();

		return node;
	}

	/** Whether the instruction puts exactly one value on the stack and has no other effect. */
	private static boolean pushesOneValue(AbstractInsnNode node) {
		if (node instanceof VarInsnNode var) {
			return var.getOpcode() >= Opcodes.ILOAD && var.getOpcode() <= Opcodes.ALOAD;
		}

		if (node instanceof FieldInsnNode field) {
			return field.getOpcode() == Opcodes.GETFIELD || field.getOpcode() == Opcodes.GETSTATIC;
		}

		if (node instanceof LdcInsnNode) return true;
		if (node instanceof IntInsnNode) return true;

		return node instanceof InsnNode insn
				&& (insn.getOpcode() >= Opcodes.ICONST_M1 && insn.getOpcode() <= Opcodes.DCONST_1
						|| insn.getOpcode() == Opcodes.ACONST_NULL);
	}

	/**
	 * Whether that instruction pushes the wanted type. A field read says so itself; a local is read out of the
	 * method's own variable table, and when the table has nothing to say about that slot - OptiFine's recompiled
	 * classes drop entries - the instruction is accepted as it is, because its kind has already been checked.
	 */
	private static boolean pushes(MethodNode method, AbstractInsnNode node, Type wanted) {
		if (node instanceof FieldInsnNode field) return field.desc.equals(wanted.getDescriptor());

		if (node instanceof VarInsnNode var) {
			int index = method.instructions.indexOf(var);

			for (org.objectweb.asm.tree.LocalVariableNode local : method.localVariables) {
				int start = method.instructions.indexOf(local.start);
				int end = method.instructions.indexOf(local.end);

				if (local.index == var.var && start <= index && index <= end) return local.desc.equals(wanted.getDescriptor());
			}
		}

		return true;
	}

	private boolean hasCall(MethodNode method, String name, String desc) {
		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn instanceof MethodInsnNode call && call.owner.equals(owner) && call.name.equals(name)
					&& call.desc.equals(desc)) {
				return true;
			}
		}

		return false;
	}
}
