/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * EXPERIMENTAL, and it does nothing unless -Doptifabric.experimentalPerDraw=true is set (see
 * OptifinePerDrawState, which carries the reasoning and the measurement this follows from).
 *
 * 26.1.2 wrapped each draw in OptiFine's per-draw shader state calls inside RenderType.draw(MeshData). 26.2 moved
 * that entry point to PreparedRenderType.drawFromBuffer(StagedVertexBuffer$ExecuteInfo) - a class OptiFine's 26.2
 * patch set never patches - so the calls have no caller left. This fixer puts them back, by wrapping that method
 * in a call to our own bridge.
 *
 * Why it is this cheap to do: drawFromBuffer(ExecuteInfo) is a three-instruction forwarder that unpacks the record
 * and tail-calls drawFromBuffer(GpuBuffer, GpuBuffer, IndexType, I, I, I). Injecting one invokestatic at the top and
 * one before its single return therefore needs no branches of its own, so no new stack map frames are required and
 * the class can be written without recomputing them (the same reason FrapiTesselateBridgeFix can use ClassWriter(0)
 * after it edits a method body).
 *
 * What it deliberately does not do: RenderType itself is not carried into 26.2's prepared draw object, so the
 * per-render-type special cases that 26.1.2 ran through ShadersRender.preRender/postRender - glint, spider eyes,
 * block damage, lines, water mask, beacon beam - cannot be reconstructed here, and they are not what this test is
 * about. The pair that is put back is the general one: save OptiFine's active program before the draw and rebind it
 * afterwards.
 */
package kynarain.cn.optifabric.patcher.fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PerDrawShaderStateFix implements ClassFixer {
	/** Our bridge, resolved by name so this mod keeps having no OptiFine compile dependency. */
	private static final String BRIDGE = "kynarain/cn/optifabric/mod/OptifinePerDrawState";
	/** The read-only vertex format probe, injected into the same method. */
	private static final String PROBE = "kynarain/cn/optifabric/mod/OptifineFormatProbe";
	private static final String PRE_DRAW = "preDraw";
	private static final String POST_DRAW = "postDraw";

	private static final String DRAW = "drawFromBuffer";
	/** The record-taking overload: every staged (i.e. section mesh) draw goes through it and then onward. */
	private static final String DRAW_DESC = "(Lnet/minecraft/client/renderer/StagedVertexBuffer$ExecuteInfo;)V";

	@Override
	public void fix(ClassNode source, ClassNode game) {
		boolean perDraw = Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifinePerDrawState.PROPERTY);
		boolean probeFormats = Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifineFormatProbe.PROPERTY);

		if (!perDraw && !probeFormats) return;

		MethodNode method = find(source);

		if (method == null) {
			System.out.println("[OptiFabric] No " + DRAW + DRAW_DESC + " in " + source.name
					+ ", so the per-draw shader state test has nothing to wrap");
			return;
		}

		if (probeFormats) {
			// Read-only: hand the drawn PreparedRenderType to the probe, which logs the vertex formats the terrain
			// path uses. this is at slot 0 because drawFromBuffer is an instance method.
			InsnList probe = new InsnList();
			probe.add(new VarInsnNode(Opcodes.ALOAD, 0));
			probe.add(new MethodInsnNode(Opcodes.INVOKESTATIC, PROBE, "probe", "(Ljava/lang/Object;)V", false));
			method.instructions.insert(probe);
			method.maxStack = Math.max(method.maxStack, 1);
		}

		if (!perDraw) {
			System.out.println("[OptiFabric] Vertex format probe installed in " + source.name + '.' + DRAW
					+ " (read-only; -D" + kynarain.cn.optifabric.mod.OptifineFormatProbe.PROPERTY + "=true)");
			return;
		}

		InsnList pre = new InsnList();
		pre.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BRIDGE, PRE_DRAW, "()V", false));
		method.instructions.insert(pre);

		int wrapped = 0;

		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn.getOpcode() != Opcodes.RETURN) continue;

			InsnList post = new InsnList();
			post.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BRIDGE, POST_DRAW, "()V", false));
			method.instructions.insertBefore(insn, post);
			wrapped++;
		}

		if (wrapped == 0) {
			System.out.println("[OptiFabric] " + source.name + '.' + DRAW + " has no return to wrap, so the per-draw"
					+ " shader state test is not applied");
			return;
		}

		System.out.println("[OptiFabric] Wrapped " + source.name + '.' + DRAW + " (" + wrapped + " return path(s)) in"
				+ " OptiFine's per-draw program save/restore, as an experiment (clear the .optifine cache and pass"
				+ " -D" + kynarain.cn.optifabric.mod.OptifinePerDrawState.PROPERTY + "=true to the game)");
	}

	private static MethodNode find(ClassNode node) {
		for (MethodNode method : node.methods) {
			if (DRAW.equals(method.name) && DRAW_DESC.equals(method.desc)) return method;
		}

		return null;
	}
}
