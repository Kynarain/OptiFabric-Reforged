/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * EXPERIMENTAL, and read-only: with -Doptifabric.experimentalFormatProbe=true it logs the vertex formats that the
 * 26.2 terrain path actually uses, so the "vertex format / pipeline mismatch" theory can be measured instead of
 * argued about.
 *
 * Why that is the open question: OptiFine's shader pipeline works on an *extended* vertex format
 * (SVertexFormat.makeExtendedFormatBlock adds Normal, PaddingN, MidOffset and more on top of the game's own block
 * format), and DefaultVertexFormat.updateVertexFormats() swaps the game's format for it as soon as shaders are on -
 * on 26.2 as well as on 26.1.2. What differs is who then uses it:
 *
 *   - 26.1.2's renderer/rendertype/RenderType carries shader calls (its draw() ran ShadersRender.preRender/
 *     postRender and pushed/popped the program), and sections were built with DefaultVertexFormat.BLOCK read at
 *     build time;
 *   - 26.2 draws through PreparedRenderType (which OptiFine does not patch at all), and
 *     ChunkSectionLayer.vertexFormat() returns pipeline().getVertexFormatBinding(0) - a RenderPipeline built in
 *     ChunkSectionLayer's static initialiser, long before a shaderpack loads. OptiFine's patch to RenderPipelines
 *     contains no OptiFine call at all, so nothing rebuilds those pipelines with the extended format.
 *
 * If that is the cause, then with shaders on: DefaultVertexFormat.BLOCK lists the extended attributes while the
 * layer/pipeline format that the terrain vertices are actually written and drawn with does not - and a shader
 * program compiled against the extended format reads the wrong bytes per vertex, which is exactly "the world is
 * not drawn". The three lists printed here are the measurement that settles it.
 */
package kynarain.cn.optifabric.mod;

import java.util.List;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public final class OptifineFormatProbe {
	/** The switch; the fixer injects the call only when it is set. */
	public static final String PROPERTY = "optifabric.experimentalFormatProbe";

	private static final boolean ENABLED = Boolean.getBoolean(PROPERTY);
	/** Pipelines already logged, so a frame does not repeat the same lines. */
	private static final java.util.Map<String, Boolean> SEEN = new java.util.concurrent.ConcurrentHashMap<>();
	private static final int LIMIT = 12;
	private static int logged;

	private OptifineFormatProbe() {
	}

	/** Called from the injected call site with the PreparedRenderType being drawn (or null). */
	public static void probe(Object prepared) {
		if (!ENABLED || logged >= LIMIT) return;

		String name = prepared == null ? "(null)" : prepared.getClass().getName();
		String key = name + "/" + pipelineOf(prepared);
		if (SEEN.putIfAbsent(key, Boolean.TRUE) != null) return;

		logged++;

		System.out.println("[OptiFabric] vertex format probe: DefaultVertexFormat.BLOCK = " + describe(DefaultVertexFormat.BLOCK));
		System.out.println("[OptiFabric] vertex format probe: DefaultVertexFormat.ENTITY = " + describe(DefaultVertexFormat.ENTITY));

		RenderPipeline pipeline = pipelineOf(prepared);
		if (pipeline != null) {
			System.out.println("[OptiFabric] vertex format probe: drawn pipeline " + safeLocation(pipeline) + " binding0 = "
					+ describe(bindingOf(pipeline)));
		}

		try {
			for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
				System.out.println("[OptiFabric] vertex format probe: layer " + layer + " vertexFormat = " + describe(layer.vertexFormat())
						+ " pipeline " + safeLocation(layer.pipeline()));
			}
		} catch (Throwable t) {
			System.out.println("[OptiFabric] vertex format probe: layers unreadable: " + t);
		}
	}

	private static RenderPipeline pipelineOf(Object prepared) {
		if (prepared == null) return null;

		try {
			return (RenderPipeline) prepared.getClass().getMethod("pipeline").invoke(prepared);
		} catch (Throwable t) {
			return null;
		}
	}

	private static VertexFormat bindingOf(RenderPipeline pipeline) {
		try {
			return pipeline.getVertexFormatBinding(0);
		} catch (Throwable t) {
			return null;
		}
	}

	private static String safeLocation(RenderPipeline pipeline) {
		try {
			return String.valueOf(pipeline.getLocation());
		} catch (Throwable t) {
			return "?";
		}
	}

	/** Element names in order plus the stride, which is what the GPU has to agree with the shader about. */
	private static String describe(VertexFormat format) {
		if (format == null) return "(none)";

		StringBuilder out = new StringBuilder();
		out.append(format.getVertexSize()).append(" bytes [");

		try {
			List<VertexFormatElement> elements = format.getElements();
			for (int i = 0; i < elements.size(); i++) {
				if (i > 0) out.append(", ");
				out.append(elements.get(i));
			}
		} catch (Throwable t) {
			out.append("elements unreadable: ").append(t);
		}

		return out.append(']').toString();
	}
}
