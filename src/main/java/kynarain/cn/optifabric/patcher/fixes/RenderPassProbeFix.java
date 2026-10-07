/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * Read-only, only with -Doptifabric.experimentalFormatProbe=true. Puts one logging call at the head of
 * GlCommandEncoder.createRenderPass, where the game decides which textures a draw will write into - see
 * OptifineRenderPassProbe for why that is the interesting place on 26.2.
 *
 * GlCommandEncoder is OptiFine-patched already, so this is a normal fix rather than a class takeover.
 */
package kynarain.cn.optifabric.patcher.fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RenderPassProbeFix implements ClassFixer {
	private static final String PROBE = "kynarain/cn/optifabric/mod/OptifineRenderPassProbe";
	private static final String ENTER = "enter";
	private static final String CREATE = "createRenderPass";
	/** The 26.2 shape: one descriptor argument, so slot 1. */
	private static final String DESCRIPTOR_DESC = "Lcom/mojang/blaze3d/systems/RenderPassDescriptor;";

	@Override
	public void fix(ClassNode source, ClassNode game) {
		if (!Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifineFormatProbe.PROPERTY)) return;

		int patched = 0;

		for (MethodNode method : source.methods) {
			if (!CREATE.equals(method.name)) continue;
			if (method.desc == null || !method.desc.contains(DESCRIPTOR_DESC)) continue;

			InsnList probe = new InsnList();
			probe.add(new VarInsnNode(Opcodes.ALOAD, 1));
			probe.add(new MethodInsnNode(Opcodes.INVOKESTATIC, PROBE, ENTER, "(Ljava/lang/Object;)V", false));
			method.instructions.insert(probe);
			method.maxStack = Math.max(method.maxStack, 2);
			patched++;
		}

		if (patched == 0) {
			System.out.println("[OptiFabric] No " + CREATE + "(RenderPassDescriptor) in " + source.name
					+ ", so the render pass probe has nothing to log there");
			return;
		}

		System.out.println("[OptiFabric] Render pass probe installed in " + source.name + '.' + CREATE + " (" + patched
				+ " method(s)): logs which render targets the world's draws use, and OptiFine's flags at that moment");
	}
}
