/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * EXPERIMENTAL, and off unless the system property below is set. It exists to test one specific hypothesis about
 * 26.2, not to be shipped as a fix:
 *
 *   OptiFine 26.1.2 wrapped every draw in RenderType.draw with Shaders.pushProgram() / ShadersRender.preRender()
 *   before it and ShadersRender.postRender() / Shaders.popProgram() after it. Shaders.pushProgram() pushes the
 *   program OptiFine currently has active onto a ProgramStack, and Shaders.popProgram() pops it and calls
 *   Shaders.useProgram with it - i.e. the pair saves OptiFine's active program across a draw and rebinds it
 *   afterwards, so a draw that switches the GL program through the game's own pipeline cannot leave OptiFine's
 *   bookkeeping pointing at a program that is no longer bound.
 *
 *   26.2 moved the draw entry to PreparedRenderType.drawFromBuffer, which OptiFine's 26.2 patch set does not touch
 *   at all (measured: 150 distinct net/optifine/shaders call targets reach game classes on 26.1.2, 137 on 26.2, and
 *   every one of the 13 that disappeared used to be called from RenderType, the model/custom feature renderers, the
 *   particle feature renderer or LevelRenderer). The restores that survive happen once per render pass
 *   (GlCommandEncoder), not once per draw.
 *
 *   So: if terrain is drawn but never reaches the shader pipeline's framebuffer, this is a candidate cause, and
 *   adding the pair back around the new draw entry is the cheap way to find out. Whether it helps is only knowable
 *   in game - hence the property, the log line, and the note in docs/PORT_26.x.md. Nothing here runs otherwise.
 *
 * Everything OptiFine-side is resolved by name on first use (this mod has no OptiFine compile dependency, and the
 * class is only ever reached from bytecode this mod injects, which itself only exists when OptiFine is present).
 */
package kynarain.cn.optifabric.mod;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class OptifinePerDrawState {
	/** The switch: without it preDraw()/postDraw() return immediately and the fixer never injects anything. */
	public static final String PROPERTY = "optifabric.experimentalPerDraw";

	private static final boolean ENABLED = Boolean.getBoolean(PROPERTY);

	private static boolean resolved;
	private static boolean available;
	private static String why;

	private static MethodHandle isShaders;
	private static MethodHandle isRenderingWorld;
	private static MethodHandle isShadowPass;
	private static MethodHandle pushProgram;
	private static MethodHandle popProgram;
	private static MethodHandle setFlushRenderBuffers;

	/** One draw at a time (the render thread), so plain fields are enough and no allocation happens per draw. */
	private static boolean pushed;
	private static boolean flush;

	private OptifinePerDrawState() {
	}

	/** Before a draw: save OptiFine's active program, and stop flushing render buffers for the duration. */
	public static void preDraw() {
		if (!ENABLED || !resolve()) return;

		try {
			if (!(Boolean) isShaders.invoke()) return;
			if (!(Boolean) isRenderingWorld.invoke()) return;
			if ((Boolean) isShadowPass.invoke()) return;

			flush = (Boolean) setFlushRenderBuffers.invoke(false);
			pushProgram.invoke();
			pushed = true;
		} catch (Throwable t) {
			//Never let this break a frame: without the pair the draw is simply back to OptiFine 26.2's behaviour.
			pushed = false;
			note(t);
		}
	}

	/** After a draw: rebind the program saved by {@link #preDraw()} and restore the flush flag. */
	public static void postDraw() {
		if (!pushed) return;

		pushed = false;

		try {
			popProgram.invoke();
			setFlushRenderBuffers.invoke(flush);
		} catch (Throwable t) {
			note(t);
		}
	}

	private static boolean resolve() {
		if (resolved) return available;

		resolved = true;

		try {
			ClassLoader loader = OptifinePerDrawState.class.getClassLoader();
			Class<?> config = Class.forName("net.optifine.Config", false, loader);
			Class<?> shaders = Class.forName("net.optifine.shaders.Shaders", false, loader);
			Class<?> utils = Class.forName("net.optifine.render.RenderUtils", false, loader);
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();

			isShaders = lookup.findStatic(config, "isShaders", MethodType.methodType(boolean.class));
			isRenderingWorld = lookup.findStaticGetter(shaders, "isRenderingWorld", boolean.class);
			isShadowPass = lookup.findStaticGetter(shaders, "isShadowPass", boolean.class);
			pushProgram = lookup.findStatic(shaders, "pushProgram", MethodType.methodType(void.class));
			popProgram = lookup.findStatic(shaders, "popProgram", MethodType.methodType(void.class));
			setFlushRenderBuffers = lookup.findStatic(utils, "setFlushRenderBuffers",
					MethodType.methodType(boolean.class, boolean.class));

			available = true;
			System.out.println("[OptiFabric] Per-draw shader state is ON (" + PROPERTY + "): every"
					+ " PreparedRenderType.drawFromBuffer is wrapped in OptiFine's pushProgram/popProgram pair, which is"
					+ " what the 26.1.2 pipeline had around RenderType.draw and 26.2's patch set dropped");
		} catch (Throwable t) {
			why = String.valueOf(t);
			System.err.println("[OptiFabric] Per-draw shader state was requested but OptiFine's shader entry points"
					+ " could not be resolved, so nothing is wrapped: " + why);
		}

		return available;
	}

	private static void note(Throwable t) {
		if (resolved && !available) return; //already reported once
		resolved = false; //report the first real failure once, then stop trying to be loud about it
		System.err.println("[OptiFabric] Per-draw shader state failed for a draw, which is now unwrapped: " + t);
	}
}
