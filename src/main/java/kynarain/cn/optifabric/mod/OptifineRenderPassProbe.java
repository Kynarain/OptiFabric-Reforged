/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * Read-only, on with -Doptifabric.experimentalFormatProbe=true. It logs which render targets the game creates
 * passes for while OptiFine is drawing the world, which is the one thing left to look at after everything else
 * checked out.
 *
 * Measured on 26.2 with the shaderpack forced on (2026-09-27): the pack loads, 27 programs compile, both
 * framebuffers are created, the section pass runs every frame, the shadow pass and the main pass alternate
 * correctly (shadowPass true with program "shadow", then false with program "gbuffers_terrain"/"gbuffers_water"),
 * the vertex formats of the layers and of DefaultVertexFormat.BLOCK are identical (72 bytes, same attributes at the
 * same offsets) - and the screen still shows nothing. So the draws happen with the right program, format and flags;
 * what is unverified is the target they land in and what the composite chain then reads.
 *
 * Render-pass labels and target texture labels are what the game itself names them, so this prints the answer
 * without needing to know OptiFine's internals: if the world's passes target a texture that is not the one the
 * composite chain samples, the picture is missing while every log line looks healthy - which is exactly the shape
 * of this bug.
 */
package kynarain.cn.optifabric.mod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OptifineRenderPassProbe {
	/** Same switch as the other probes: one property turns them all on. */
	public static final String PROPERTY = OptifineFormatProbe.PROPERTY;

	private static final boolean ENABLED = Boolean.getBoolean(PROPERTY);
	private static final Map<String, Boolean> SEEN = new ConcurrentHashMap<>();
	private static final int LIMIT = 25;
	private static int logged;

	private OptifineRenderPassProbe() {
	}

	/** Called from the head of GlCommandEncoder.createRenderPass with that method's descriptor. */
	public static void enter(Object descriptor) {
		if (!ENABLED || logged >= LIMIT || descriptor == null) return;

		//Only passes created while OptiFine is drawing the world: the first run of this probe spent its whole budget
		//on the passes the title screen creates, which said nothing about the world.
		if (!Boolean.TRUE.equals(shaderFlag("isRenderingWorld"))) return;

		String text = describe(descriptor);
		if (SEEN.putIfAbsent(text, Boolean.TRUE) != null) return;

		logged++;
		System.out.println("[OptiFabric] render pass probe: " + text);
	}

	/** Pass label, the colour/depth textures it points at, and OptiFine's flags at that moment. */
	private static String describe(Object descriptor) {
		StringBuilder out = new StringBuilder();

		Object label = call(descriptor, "label");
		if (label instanceof java.util.function.Supplier) {
			try {
				out.append(((java.util.function.Supplier<?>) label).get());
			} catch (Throwable t) {
				out.append("label?").append(t.getClass().getSimpleName());
			}
		} else {
			out.append(label);
		}

		out.append(" color=[");
		Object colors = call(descriptor, "colorAttachments");
		if (colors instanceof Iterable) {
			boolean first = true;
			for (Object attachment : (Iterable<?>) colors) {
				if (!first) out.append(", ");
				first = false;
				out.append(textureOf(attachment));
			}
		} else {
			out.append(colors);
		}
		out.append("] depth=").append(textureOf(call(descriptor, "depthAttachment")));

		out.append("  [isRenderingWorld=").append(shaderFlag("isRenderingWorld"));
		out.append(" isShadowPass=").append(shaderFlag("isShadowPass"));
		out.append(" activeProgram=").append(shortProgram(String.valueOf(shaderFlag("activeProgram"))));
		out.append(']');

		return out.toString();
	}

	/** The texture a colour/depth attachment points at, by the label and size the game gave it. */
	private static String textureOf(Object attachment) {
		if (attachment == null) return "(none)";

		Object view = call(attachment, "textureView");
		if (view == null) view = call(attachment, "getTextureView");
		if (view == null) return String.valueOf(attachment);

		Object texture = call(view, "texture");
		if (texture == null) return String.valueOf(view);

		Object label = call(texture, "getLabel");
		Object width = call(texture, "getWidth", int.class, 0);
		Object height = call(texture, "getHeight", int.class, 0);

		return label + "(" + width + "x" + height + ")";
	}

	private static Object call(Object target, String name, Class<?> argType, Object arg) {
		try {
			return target.getClass().getMethod(name, argType).invoke(target, arg);
		} catch (Throwable t) {
			return null;
		}
	}

	private static Object call(Object target, String name) {
		try {
			return target.getClass().getMethod(name).invoke(target);
		} catch (Throwable t) {
			return null;
		}
	}

	private static Object shaderFlag(String name) {
		try {
			Class<?> shaders = Class.forName("net.optifine.shaders.Shaders", false, OptifineRenderPassProbe.class.getClassLoader());
			return shaders.getField(name).get(null);
		} catch (Throwable t) {
			return "<" + t.getClass().getSimpleName() + ">";
		}
	}

	private static String shortProgram(String program) {
		int at = program.indexOf("real: ");
		return at >= 0 ? program.substring(at + 6).trim() : program;
	}
}
