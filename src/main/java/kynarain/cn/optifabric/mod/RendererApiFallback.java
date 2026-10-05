/*
 * New in the 1.21.11 port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 *
 * Registers an inert rendering plug-in with Fabric's renderer API, so that Fabric API's own hooks find *something*
 * where they look instead of throwing on the lookup itself.
 *
 * fabric.mod.json declares "fabric-renderer-api-v1:contains_renderer": true (the same value Sodium declares) to
 * tell Indigo to step aside, because OptiFine is the terrain renderer here. That flag is what Fabric API reads when
 * it decides who renders: Indigo refuses to apply its mixins and never registers itself. Fabric API's renderer
 * modules, however, do not check the flag - they check the *registry*:
 *
 *   private Renderer activeRenderer;          // net.fabricmc.fabric.impl.renderer.RendererAccessImpl
 *   public Renderer getRenderer() { return this.activeRenderer; }
 *
 * Two live paths reach that: the "Renderer:" line of the F3 debug screen (an entry fabric-renderer-api-v1 registers
 * for itself), and - before StubInjectionTargetFix hid it - the moving block hook that was the multiplayer crash.
 * The first one is still there, so pressing F3 would take the game down. A third path is a mod of its own: anything
 * that asks RendererAccess.getRenderer() and uses the answer, which is what Twilight Forest's ForceFieldModel does
 * in its static initialiser - a null there is a NullPointerException during model loading, not a missing drawing.
 *
 * OptiFabric cannot become a real renderer (OptiFine draws; this is not an Indigo replacement), so what gets
 * registered is a placeholder that throws a sentence worth reading if anything ever does ask it to draw, and whose
 * class name is what shows up behind "Renderer:" in F3. That is the honest description of the situation: a Fabric
 * renderer exists as far as the API is concerned, and it is OptiFine doing the rendering.
 *
 * On 26.1.2 that stopped being the right answer. Fabric API moved the terrain and submit-node integration into
 * fabric-renderer-api-v1 itself, and what Indigo has left there is one item mixin and two accessors - it is not a
 * terrain renderer any more, so the key that tells it to step aside has nothing left to step aside from. All it
 * did on that line was disable a rendering plug-in Fabric API keeps asking for: FRAPI mods emitted their quads
 * into the inert placeholder, which is invisible geometry and no error at all. The 26.x line therefore does not
 * declare the key, Indigo registers its own IndigoRenderer, and this class steps out of the way - see
 * indigoWillRegister(). The 1.21.x line keeps the key, because there Indigo *is* the terrain renderer and what it
 * steps aside from is an injection point OptiFine's rewrite of the chunk build removes (fatal under
 * "defaultRequire": 1).
 *
 * Two ordering rules come with it:
 *
 *   - it runs in the preLaunch entrypoint, after OptiFine's patched classes have been handed to Fabric Loader.
 *     Registering first would win the race against any other renderer, but the classes this touches have to be
 *     resolved *through* the patched set - loading one of them around it leaves the game with the vanilla class for
 *     the rest of the run (that is exactly how the getBlockStateBaseCacheClass crash happened, see
 *     RendererApiStubGenerator);
 *   - the registration itself is done through the API's *registry object*, never through a method on the interface:
 *     {@code RendererAccess.INSTANCE.registerRenderer(renderer)} on 1.21.x. Looking the static
 *     {@code Renderer.register(Renderer)} up instead - which is what this did - resolves nothing on a current
 *     Fabric API: the re-verification in compat-recheck found the method gone from 0.116.17 (checked with
 *     {@code javap -p -c} on the fabric-renderer-api-v1-0.116.17.jar nested in fabric-api-0.116.17+1.21.1.jar,
 *     where RendererAccess declares only registerRenderer/getRenderer/hasRenderer). The lookup threw
 *     NoSuchMethodException, the placeholder was therefore never registered, and every FRAPI mod ran against a null
 *     renderer - with OptiFine on the classpath as well as without it. Only "register" is still looked up, and its
 *     only argument type is the interface itself; nothing here resolves the interface's other signatures, so no
 *     game type is loaded (see RendererApiStubGenerator).
 *
 * If something else registered a renderer first, that one is left alone and this only logs a line.
 */
package kynarain.cn.optifabric.mod;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class RendererApiFallback {
	/**
	 * The key Indigo reads ({@code IndigoMixinConfigPlugin}) and the one the two lines' {@code fabric.mod.json}
	 * files differ in: declaring it says "another renderer is here, step aside", and this class reads it back to
	 * know whether it still has to supply a fallback for the plug-in Indigo then never registers.
	 */
	private static final String CONTAINS_RENDERER = "fabric-renderer-api-v1:contains_renderer";

	/** Where Indigo's entrypoint lives: enough to tell whether it is on the classpath at all. */
	private static final String INDIGO_CLASS = "net/fabricmc/fabric/impl/client/indigo/Indigo.class";

	/**
	 * The API's registry object on 1.21.x. Its {@code INSTANCE} field is the singleton whose
	 * {@code registerRenderer(Renderer)} is the only way to register a rendering plug-in on every Fabric API of this
	 * line (verified against fabric-renderer-api-v1-0.116.17, see the class comment). Loaded without being
	 * initialised: its static initialiser mentions no game type, but nothing here has any reason to run it early.
	 */
	private static final String RENDERER_ACCESS_CLASS = "net.fabricmc.fabric.api.renderer.v1.RendererAccess";

	/**
	 * Where the interface lives, newest first. 26.1 moved it and its implementation down into the client package
	 * ({@code api.renderer.v1.Renderer} became {@code api.client.renderer.v1.Renderer}, and the registry with it:
	 * {@code impl.renderer.RendererManager} became {@code impl.client.renderer.RendererManager}). Looking up the
	 * old name there throws ClassNotFoundException, and because that is the documented "no Fabric API" path the
	 * method used to return quietly - so the placeholder was never registered at all and the first
	 * {@code Renderer.get()} from a rendering hook took the game down:
	 *
	 * <pre>UnsupportedOperationException: Attempted to retrieve active rendering plug-in before one was registered.
	 *   at net.fabricmc.fabric.impl.client.renderer.RendererManager.getRenderer
	 *   at net.fabricmc.fabric.api.client.renderer.v1.Renderer.get
	 *   at ...BlockFeatureRenderer.handler$znj000$fabric-renderer-api-v1$beforeInitBlockRenderer</pre>
	 */
	private static final String[] RENDERER_API_CLASSES = {
			"net.fabricmc.fabric.api.client.renderer.v1.Renderer", //26.1 and newer
			"net.fabricmc.fabric.api.renderer.v1.Renderer", //1.21.x
	};

	private RendererApiFallback() {
	}

	public static void install() {
		if (indigoWillRegister()) {
			System.out.println("[OptiFabric] Indigo is present and nothing declared " + CONTAINS_RENDERER
					+ ", so it registers Fabric's rendering plug-in itself - not registering a placeholder");
			return;
		}

		if (sodiumWillRegister()) {
			System.out.println("[OptiFabric] Sodium is present, so it registers Fabric's rendering plug-in itself -"
					+ " not registering a placeholder, because the registry refuses a second one and the first one to"
					+ " arrive keeps the slot");
			return;
		}

		Class<?> renderer = null;

		for (String candidate : RENDERER_API_CLASSES) {
			try {
				//Asked for directly rather than through FabricLoader.isModLoaded: the only thing that matters is
				//whether the interface is there, and this way a missing Fabric API is not an error path at all
				renderer = Class.forName(candidate, false, RendererApiFallback.class.getClassLoader());
				break;
			} catch (ClassNotFoundException ignored) {
				//try the next location
			}
		}

		if (renderer == null) {
			return; //No Fabric API renderer API on the classpath: nothing ever asks for a rendering plug-in
		}

		try {
			Object placeholder = RendererApiStubGenerator.newInstance(renderer);
			register(renderer, placeholder);

			System.out.println("[OptiFabric] Registered " + placeholder.getClass().getSimpleName()
					+ " as Fabric's rendering plug-in (" + renderer.getName() + "): Fabric API expects one to exist"
					+ " even though OptiFine is the renderer and Indigo steps aside, and its own hooks crash without it");
		} catch (UnsupportedOperationException e) {
			System.out.println("[OptiFabric] Another rendering plug-in is already registered, leaving it alone");
		} catch (Throwable t) {
			System.err.println("[OptiFabric] Could not register a placeholder for Fabric's renderer API, "
					+ "Fabric API hooks that ask for it may crash: " + t);
		}
	}

	/**
	 * Hands the placeholder to the API's registry, in the shape that API actually has.
	 *
	 * <p>On 1.21.x the registry is a singleton the API interface carries as a static field, and registering is a call
	 * on <em>that</em>: {@code RendererAccess.INSTANCE.registerRenderer(renderer)} - the method on the interface
	 * itself, which is where the implementation's null check and its "a second rendering plug-in attempted to
	 * register" refusal live. The older shape, a static {@code Renderer.register(Renderer)}, is tried first and kept
	 * rather than dropped: it is what the API used before this line's releases and what an older Fabric API on the
	 * classpath still has. When neither is there the caller logs the failure it already did - this method has no
	 * second way to report it.
	 *
	 * <p>Only "register" is ever looked up, and both of its possible owners are asked for by name without being
	 * initialised, so no game type is resolved here (see RendererApiStubGenerator).
	 */
	private static void register(Class<?> renderer, Object placeholder) throws Throwable {
		try {
			MethodHandle register = MethodHandles.publicLookup().findStatic(renderer, "register",
					MethodType.methodType(void.class, renderer));
			register.invoke(placeholder);

			return;
		} catch (NoSuchMethodException ignored) {
			//the shape of the API before this registry object existed; registerRenderer below is the current one
		}

		Class<?> access = Class.forName(RENDERER_ACCESS_CLASS, false, RendererApiFallback.class.getClassLoader());
		Object registry = MethodHandles.publicLookup().findStaticGetter(access, "INSTANCE", access).invoke();
		MethodHandle register = MethodHandles.publicLookup().findVirtual(access, "registerRenderer",
				MethodType.methodType(void.class, renderer));
		register.invoke(registry, placeholder);
	}

	/**
	 * Whether Sodium is about to register a rendering plug-in of its own, in which case it has to be left to do it -
	 * exactly the race {@link #indigoWillRegister()} describes, with a mod that really is a renderer.
	 *
	 * <p>Sodium 0.8.13 declares {@code fabric-renderer-api-v1:contains_renderer} too, so Indigo stays away for the
	 * same reason it does for OptiFine, and its client entrypoint registers
	 * {@code net.caffeinemc.mods.sodium.client.render.frapi.SodiumRenderer} through the same slot. The registry
	 * refuses the second registration, and the first one to arrive keeps the slot: with the placeholder registered
	 * first, Sodium dies on its own entrypoint with "A second rendering plug-in attempted to register", which is a
	 * crash during startup rather than the missing drawing it looks like. Measured on 1.21.1 / Sodium 0.8.13: with
	 * the placeholder suppressed and Sodium's registration allowed through, the client reaches the title screen and
	 * loads a world (log lines, not a claim from the code).
	 *
	 * <p>The placeholder exists because Fabric API's hooks ask the registry for a renderer and throw when it is
	 * empty. With Sodium present it will not be empty, and what FRAPI mods get is a real renderer instead of the
	 * inert placeholder - a strict improvement, not a trade.
	 *
	 * <p>If the mod list cannot be read, this answers "no" and the placeholder is registered as before: the failure
	 * this avoids is a hard crash when Sodium is there, and guessing "Sodium is here" wrongly would leave Fabric
	 * API's own hooks with nothing at all.
	 *
	 * <p><b>Unreachable in the jars this line ships.</b> fabric.mod.json declares {@code breaks: sodium}, so a
	 * loader refuses an instance that has Sodium before any of this runs - the two cannot share the terrain pipeline
	 * (measured: with every repair in place the client loads a world and renders nothing, and neither the renderer
	 * slot nor OptiFine's Fast Render accounted for it). This branch is kept as the starting point if that is ever
	 * revisited, and it is deliberately left as the answer that would be right if Sodium were allowed in.
	 */
	private static boolean sodiumWillRegister() {
		try {
			for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
				if ("sodium".equals(mod.getMetadata().getId())) return true;
			}
		} catch (Throwable t) {
			System.err.println("[OptiFabric] Could not read the mod list to tell whether Sodium is present ("
					+ t + "), registering a placeholder as before");
		}

		return false;
	}

	/**
	 * Whether Indigo is about to register a rendering plug-in of its own, in which case it has to be left to do it:
	 * {@code RendererManager} refuses a second registration, so registering the placeholder first would win the race
	 * and hand Fabric API the inert one instead of the real one.
	 *
	 * The question asked is the one {@code IndigoMixinConfigPlugin.shouldApplyMixin} asks, in the same order: is
	 * Indigo on the classpath, and has any mod declared the key that tells it to step aside. Reading the answer
	 * back from the metadata rather than compiling it in is what lets one shared class serve both lines - the
	 * 1.21.x jar declares the key and still needs the placeholder, the 26.x jar does not declare it and gets
	 * Indigo's own {@code IndigoRenderer}.
	 *
	 * Indigo is looked up as a <em>resource</em>, never loaded: nothing here resolves a game type during preLaunch,
	 * which is the mistake that once left the game running with vanilla classes for the whole session (see
	 * RendererApiStubGenerator).
	 */
	private static boolean indigoWillRegister() {
		if (RendererApiFallback.class.getClassLoader().getResource(INDIGO_CLASS) == null) {
			return false; //No Indigo on the classpath: nobody else is going to register anything
		}

		try {
			for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
				if (mod.getMetadata().containsCustomValue(CONTAINS_RENDERER)) return false; //Told to step aside
			}
		} catch (Throwable t) {
			//Reading metadata must never be what decides the game comes up without a renderer, so an unreadable
			//mod list falls back to registering the placeholder: the behaviour that does not depend on the answer
			System.err.println("[OptiFabric] Could not read the mod metadata to tell whether Indigo steps aside ("
					+ t + "), registering a placeholder as before");
			return false;
		}

		return true;
	}
}
