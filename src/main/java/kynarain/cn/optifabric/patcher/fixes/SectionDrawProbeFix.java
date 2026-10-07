/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * Read-only, and only with -Doptifabric.experimentalFormatProbe=true set. It puts a single logging call at the head
 * of ChunkSectionsToRender.renderGroup, which is where the chunk-section (terrain) pass for one layer group runs -
 * and, not coincidentally, where OptiFine's own ShadersRender.preRenderChunkLayer/postRenderChunkLayer hooks live.
 *
 * Why this and not the PreparedRenderType probe: measured on 2026-09-27, the pipelines that go through
 * PreparedRenderType.drawFromBuffer are entity_cutout, entity_solid, entity_translucent, item_cutout and lines -
 * with shaders ON and OFF alike. Terrain does not go through it, so an experiment wrapped around that method says
 * nothing about terrain. This call is the first place that can say whether the section pass still runs when the
 * shaderpack is loaded, and for which layer groups.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class SectionDrawProbeFix implements ClassFixer {
	private static final String PROBE = "kynarain/cn/optifabric/mod/OptifineSectionProbe";
	private static final String ENTER = "enter";
	/** (ChunkSectionLayerGroup, GpuSampler) - the layer group is the first argument, so slot 1. */
	private static final String RENDER_GROUP = "renderGroup";
	private static final String GROUP_DESC = "Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;";

	@Override
	public void fix(ClassNode source, ClassNode game) {
		if (!Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifineFormatProbe.PROPERTY)) return;

		int patched = 0;

		for (MethodNode method : source.methods) {
			if (!RENDER_GROUP.equals(method.name)) continue;
			if (method.desc == null || !method.desc.startsWith('(' + GROUP_DESC)) continue;

			InsnList probe = new InsnList();
			probe.add(new VarInsnNode(Opcodes.ALOAD, 1));
			probe.add(new MethodInsnNode(Opcodes.INVOKESTATIC, PROBE, ENTER, "(Ljava/lang/Object;)V", false));
			method.instructions.insert(probe);
			method.maxStack = Math.max(method.maxStack, 2);
			patched++;
		}

		if (patched == 0) {
			System.out.println("[OptiFabric] No " + RENDER_GROUP + '(' + GROUP_DESC + ", ...) in " + source.name
					+ ", so the section probe has nothing to log there (26.2 may have moved the section pass again)");
			return;
		}

		System.out.println("[OptiFabric] Section probe installed in " + source.name + '.' + RENDER_GROUP + " (" + patched
				+ " method(s)): logs whether the chunk-section pass still runs, and for which layer group");
	}
}
