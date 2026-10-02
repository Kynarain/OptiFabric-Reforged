/*
 * New in the 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Hooks the patched Minecraft classes into Fabric Loader's own game transformer.
 *
 * Loader patches game classes through net.fabricmc.loader.impl.game.patch.GameTransformer, whose
 * transform(String) is the FIRST thing KnotClassDelegate.getPreMixinClassByteArray consults when it
 * loads a class - before the classpath, and before Mixin runs. OptiFine's patched classes therefore
 * go straight into that transformer's patched class map:
 *
 *   - it works for any class name, with no generated stub mixins and no Mixin API coupling;
 *   - Mixin (and every other mod's mixins into those classes) still applies afterwards, because the
 *     bytes we hand over are the input to the Mixin transformer, not its output;
 *   - Mixin's cached class metadata for the classes just taken over is dropped (see MixinClassMetadata), since
 *     Mixin fills that cache before the patched classes are installed here and would otherwise go on describing
 *     the game's copies of them;
 *   - classes loader patched itself (the client brand retriever, the entrypoint) are left alone.
 *
 * The field is located by type rather than by name, so a rename in Loader does not break it, but the
 * shape is asserted and any mismatch is reported instead of silently doing nothing.
 */
package kynarain.cn.optifabric.mod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.game.patch.GameTransformer;

public final class GameTransformerHook {
	private GameTransformerHook() {
	}

	/** Injects into the game transformer of the running game. */
	public static int inject(Map<String, byte[]> patchedClasses) throws ReflectiveOperationException {
		GameTransformer transformer = FabricLoaderImpl.INSTANCE.getGameProvider().getEntrypointTransformer();

		return inject(transformer, patchedClasses);
	}

	/**
	 * Injects into the given transformer; separate from the lookup above so it can be exercised off-line.
	 *
	 * <p>Whatever was actually installed here also has its Mixin class metadata dropped, which is the other half
	 * of the takeover - see {@link MixinClassMetadata}.
	 */
	public static int inject(GameTransformer transformer, Map<String, byte[]> patchedClasses) throws ReflectiveOperationException {
		Field field = null;

		for (Field candidate : transformer.getClass().getDeclaredFields()) {
			if (Map.class.isAssignableFrom(candidate.getType())) {
				field = candidate;
				break;
			}
		}

		if (field == null) {
			throw new IllegalStateException("Found no patched class map in " + transformer.getClass().getName()
					+ ", OptiFabric cannot apply OptiFine's patches on this Fabric Loader version");
		}

		field.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<String, byte[]> target = (Map<String, byte[]>) field.get(transformer);

		if (target == null) {
			throw new IllegalStateException("The patched class map in " + transformer.getClass().getName()
					+ " is still null, Loader has not located the game entrypoints yet");
		}

		int added = 0;
		int kept = 0;

		List<String> replaced = new ArrayList<>();

		for (Map.Entry<String, byte[]> entry : patchedClasses.entrySet()) {
			if (target.containsKey(entry.getKey())) {
				//Loader patched this class itself (client brand, entrypoint, ...), its version wins
				kept++;
			} else {
				target.put(entry.getKey(), entry.getValue());
				replaced.add(entry.getKey());
				added++;
			}
		}

		if (kept > 0) {
			System.out.println("[OptiFabric] Kept Fabric's own patch for " + kept + " class(es)");
		}

		//Now that OptiFine's copies are the ones Fabric will serve, Mixin's metadata for them has to go: Mixin
		//cached whatever these classes looked like before this ran, and a mixin that resolves class metadata
		//(Locals, and therefore @ModifyVariable) fails the whole class when the two disagree. Only the classes
		//this actually replaced - a class whose bytes are still Loader's are unchanged and stay cached.
		int dropped = MixinClassMetadata.drop(replaced);

		System.out.println("[OptiFabric] Dropped " + dropped + " of " + patchedClasses.size()
				+ " Mixin class metadata entries that described the game's own members, so mixins see the patched"
				+ " ones");

		return added;
	}
}
