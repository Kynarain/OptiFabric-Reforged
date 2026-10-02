/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Drops Mixin's cached class metadata for the classes OptiFabric replaced with OptiFine's copies.
 *
 * Mixin keeps one ClassInfo per class (org.spongepowered.asm.mixin.transformer.ClassInfo#cache) and builds it
 * from the bytes its bytecode provider hands out - in Fabric that is Knot, and therefore our game transformer.
 * The cache is filled while Mixin prepares its configs, which Fabric does BEFORE a preLaunch entrypoint runs, so
 * for these classes it can already describe the GAME's copies by the time OptiFine's are installed. Measured on
 * 1.21.1: the entry for net/minecraft/class_757 is there 5ms after the takeover, while the provider already
 * answers with OptiFine's class.
 *
 * As long as cached and served bytes agree about the members a mixin looks up, nothing notices. Where they
 * disagree the lookup fails, and Locals - the machinery @ModifyVariable and locals capture use - cannot recover
 * from it: it resolves the method it is transforming with
 *
 *   ClassInfo#findMethod(name, descriptor, method.access | INCLUDE_INITIALISERS)
 *
 * whose ClassInfo.Member#matchesFlags requires a member stored as private to be queried with ACC_PRIVATE.
 * OptiFine recompiles GameRenderer.getFov and widens it from private to public, so against the cached (game)
 * entry the lookup misses and Mixin fails the whole class with
 *
 *   LVTGeneratorError: Could not locate method metadata for method_3196 generating LVT in net/minecraft/class_757
 *
 * which Fabric reports only as "Mixin transformation of net.minecraft.class_757 failed". It is thrown from
 * ModifyVariableInjector.preInject, i.e. before require/expect are consulted, so the affected mod cannot work
 * around it from its own side. Dropping the entry makes Mixin rebuild it from the bytes it is actually handed,
 * which is what it does for every class Fabric itself does not replace: the class being transformed reaches
 * Mixin's target context through ClassInfo#fromClassNode, and that returns the cached instance whenever there
 * is one.
 *
 * The cache is keyed by the internal (slash separated) name ClassInfo.forName builds, not by the dotted name
 * the class is referred to by everywhere else.
 *
 * Removing the entry does not refresh Mixin state that already holds the old ClassInfo object; see the class
 * that calls this (GameTransformerHook) for what is known about that.
 */
package kynarain.cn.optifabric.mod;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MixinClassMetadata {
	/** The classes whose entry this has already dropped; see drop. */
	private static final Set<String> dropped = new HashSet<>();

	/** Set once the cache could not be reached, so that is reported once and nothing else changes. */
	private static boolean unreachable;

	private MixinClassMetadata() {
	}

	/**
	 * Drops Mixin's cached metadata for the given classes, at most once per class.
	 *
	 * <p>Best effort by design: if Mixin's cache cannot be reached this reports it once and returns 0, because a
	 * class whose metadata stays stale still runs - only the mixins that resolve class metadata keep failing the
	 * way they did before this existed.
	 *
	 * @param classNames the replaced classes, as dotted or internal names
	 * @return how many cache entries were removed
	 */
	public static int drop(Collection<String> classNames) {
		if (classNames.isEmpty() || unreachable) return 0;

		Map<String, ?> cache;

		try {
			Class<?> classInfo = Class.forName("org.spongepowered.asm.mixin.transformer.ClassInfo", false,
					MixinClassMetadata.class.getClassLoader());
			Field field = classInfo.getDeclaredField("cache");
			field.setAccessible(true);
			@SuppressWarnings("unchecked") //The field is a Map<String, ClassInfo> in every Mixin this port supports
			Map<String, ?> found = (Map<String, ?>) field.get(null);
			cache = found;
		} catch (ReflectiveOperationException | RuntimeException e) {
			unreachable = true;
			System.out.println("[OptiFabric] Could not reach Mixin's class metadata cache, the classes it already"
					+ " cached keep the game's own members (" + e + ')');
			return 0;
		}

		if (cache == null) {
			unreachable = true;
			System.out.println("[OptiFabric] Mixin's class metadata cache is null, the classes it already cached"
					+ " keep the game's own members");
			return 0;
		}

		int removed = 0;

		for (String name : classNames) {
			String internalName = name.replace('.', '/');

			//Once per class only: a second drop could not reach anything older than the metadata Mixin has
			//meanwhile rebuilt from the bytes we installed, which is exactly the metadata that has to stay.
			if (!dropped.add(internalName)) continue;

			//ClassInfo.forName treats a present key as "already known", including one whose value is null (a
			//class Mixin could not read before, and now can), so ask the same question it asks.
			if (cache.containsKey(internalName)) {
				cache.remove(internalName);
				removed++;
			}
		}

		return removed;
	}
}
