/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * Read-only diagnostic, on with -Doptifabric.experimentalFormatProbe=true. It answers the one question the
 * PreparedRenderType probe could not: whether the chunk sections are drawn at all.
 *
 * Measured with this mod's vertex format probe (2026-09-27): with shaders forced, the pipelines that go through
 * PreparedRenderType.drawFromBuffer are entity_cutout, entity_solid, entity_translucent, item_cutout and lines -
 * and with shaders OFF the list is the same minus entity_solid. So that entry point is not where terrain is drawn;
 * chunk sections go through ChunkSectionsToRender.renderGroup, which is also where OptiFine's own
 * ShadersRender.preRenderChunkLayer/postRenderChunkLayer hooks sit. If the world is missing with shaders on, this
 * call is the first place that can say whether the section pass still runs and for which layers.
 */
package kynarain.cn.optifabric.mod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class OptifineSectionProbe {
	/** Same switch as the vertex format probe: one property turns both on. */
	public static final String PROPERTY = OptifineFormatProbe.PROPERTY;

	private static final boolean ENABLED = Boolean.getBoolean(PROPERTY);
	private static final Map<String, AtomicInteger> CALLS = new ConcurrentHashMap<>();
	private static int logged;

	private OptifineSectionProbe() {
	}

	/** Last values seen, so only changes are logged: the shadow pass draws terrain too, so "true" alone means nothing. */
	private static boolean lastShadow;
	private static String lastProgram = "";
	private static final AtomicInteger TOTAL = new AtomicInteger();

	/** Called from the head of ChunkSectionsToRender.renderGroup with that method's group argument. */
	public static void enter(Object group) {
		if (!ENABLED) return;

		String name = group == null ? "(null)" : String.valueOf(group);
		int calls = TOTAL.incrementAndGet();

		boolean shadow = false;
		String program = "?";

		try {
			Class<?> shaders = Class.forName("net.optifine.shaders.Shaders", false, OptifineSectionProbe.class.getClassLoader());
			Object shadowValue = field(shaders, "isShadowPass");
			Object programValue = field(shaders, "activeProgram");
			shadow = shadowValue instanceof Boolean && (Boolean) shadowValue;
			program = String.valueOf(programValue);
		} catch (Throwable t) {
			program = "<" + t.getClass().getSimpleName() + ">";
		}

		//Log the first calls and then every change: what matters is whether the flag ever goes back to false while
		//sections are being drawn, and whether anything but "shadow" is ever the active program.
		if (calls <= 30 || shadow != lastShadow || !program.equals(lastProgram)) {
			System.out.println("[OptiFabric] section probe: #" + calls + " group=" + name + " shadowPass=" + shadow
					+ " program=" + shortProgram(program));
			lastShadow = shadow;
			lastProgram = program;
		}
	}

	private static String shortProgram(String program) {
		int at = program.indexOf("real: ");
		return at >= 0 ? program.substring(at + 6).trim() : program;
	}

	private static Object field(Class<?> owner, String name) {
		try {
			return owner.getField(name).get(null);
		} catch (Throwable t) {
			return "<" + t.getClass().getSimpleName() + ">";
		}
	}

	/** Called from the probe's own summary: how often each group went through. */
	public static void summary() {
		if (!ENABLED) return;

		for (Map.Entry<String, AtomicInteger> entry : CALLS.entrySet()) {
			System.out.println("[OptiFabric] section probe total: renderGroup(" + entry.getKey() + ") x" + entry.getValue().get());
		}
	}
}
