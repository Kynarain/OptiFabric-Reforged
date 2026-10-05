/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kynarain.cn.optifabric.util.RemappingUtils;

public class OptifineFixer {

	public static final OptifineFixer INSTANCE = new OptifineFixer();

	private final Map<String, List<ClassFixer>> classFixes = new HashMap<>();
	private final Set<String> skippedClass = new HashSet<>();

	private OptifineFixer() {
		//net/minecraft/client/render/chunk/ChunkBuilder$ChunkData
		registerFix("class_846$class_849", new ChunkDataFix());

		//net/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask
		registerFix("class_846$class_851$class_4578", new ChunkRendererFix());

		//net/minecraft/client/render/block/BlockModelRenderer$AmbientOcclusionCalculator
		registerFix("class_778$class_780", new AmbientOcclusionCalculatorFix());

		//class_778 itself (BlockModelRenderer), which this line's OptiFine (HD_U_J1 pre18) damages the same way
		//1.21.1's does. Measured, not assumed: the 426 patched classes were unpacked from this line's own
		//.optifine cache and compared against the intermediary client jar, and every mod mixin in the cross-line
		//set was cross-referenced against what the comparison found missing. Two repairs came out of that:
		//
		//  - method_3374 no longer makes the class_2680.method_26213()I call: the identical call the 1.21.x line
		//    repairs with this identical registration, so the point is reconstructed in front of OptiFine's body
		//    instead of replacing it;
		//  - method_23073 no longer exists under that name, and sodium's
		//    features.textures.animations.tracking.BlockModelRendererMixin injects into it by name, which fails
		//    the whole class. The vanilla body is put back under that name - it is the same repair 2.2.10 shipped
		//    for 1.21.1 before the per-quad observer replaced it there.
		//
		//The other members this scan found missing (method_3363, method_3370) are named by no mixin in the set, so
		//they are left alone: a repair nobody can fail on is churn.
		registerFix("class_778", new InjectionCallPointFix("class_2680", "method_26213", "()I", "method_3374"));
		registerFix("class_778", new RestoreVanillaMethodsFix("method_23073"));

		//net/minecraft/client/Keyboard
		registerFix("class_309", new KeyboardFix());

		//net/minecraft/client/texture/SpriteAtlasTexture
		registerFix("class_1059", new SpriteAtlasTextureFix());

		//net/minecraft/client/particle/ParticleManager
		registerFix("class_702", new ParticleManagerFix());

		//net/minecraft/client/render/model/json/ModelOverrideList
		registerFix("class_806", new ModelOverrideListFix());

		//net/minecraft/client/gl/ShaderProgram
		//OptiFine's convenience constructor creates the Identifier before this(), so Fabric API's
		//@ModifyArg into the constructor lands before super() and Mixin refuses to apply it
		registerFix("class_5944", new DelegatingConstructorFix());

		//Helpers OptiFine's recompiled classes no longer have, but Fabric API's mixins inject into.
		//(class_309/Keyboard needs no entry: KeyboardFix already puts the vanilla methods back.)
		//net/minecraft/client/render/entity/EntityRenderers (fabric-rendering-v1 EntityRenderersMixin)
		registerFix("class_5619", new RestoreVanillaMethodsFix("method_32174", "method_32175"));

		//net/minecraft/server/world/ThreadedAnvilChunkStorage (fabric-lifecycle-events-v1)
		//This one runs first on purpose: OptiFine's recompile emitted the game's own lambda bodies under
		//javac's names (method_17252 became lambda$protoChunkToFullChunk$36, method_17227 became
		//lambda$protoChunkToFullChunk$35, and 57 more). Giving them the game's names back occupies them, so
		//the RestoreVanillaMethodsFix below finds them present and leaves them alone - which keeps the
		//class's own bootstrap method handles pointing at a method that exists, and keeps OptiFine's body on
		//the path that actually runs. Registering the restore first would add a second method beside each
		//lambda and leave every handle pointing at the lambda.
		//
		//c2me is why this matters: c2me-opts-scheduling @Overwrites method_17252, method_19487 and
		//method_20579, so with the lambdas still under javac's names the world-load transform fails the whole
		//class ("Mixin transformation of net.minecraft.class_3898 failed") on the integrated server thread.
		//c2me-threading-worldgen also injects into method_17224, which this does not repair - see the note on
		//LambdaMethodRefFix, that registration's argument list, descriptor and body disagree in OptiFine's own
		//bytes, so no reordering can both make them consistent and present the game's signature.
		registerFix("class_3898", new LambdaMethodRefFix());
		registerFix("class_3898", new RestoreVanillaMethodsFix("method_17227", "method_18843"));

		//net/minecraft/client/render/chunk/ChunkRendererRegionBuilder (fabric-block-view-api-v2)
		//OptiFine reduced build() to a call to its own createRegion() and moved the loop - and the four loop
		//counters plus the Chunk[][] array Fabric's createDataMap captures with CAPTURE_FAILHARD - into it.
		//The vanilla body has exactly the local layout Fabric was compiled against, and OptiFine's createRegion
		//stays available for OptiFine's own callers.
		registerFix("class_6850", new RestoreVanillaMethodsFix(true, "method_39969"));

		//net/minecraft/client/render/model/ModelLoader$BakerImpl (fabric-model-loading-api-v1)
		//Same pattern: OptiFine's bake(id, settings) only forwards to its own bake(id, settings, textureGetter),
		//which is where Fabric's @ModifyVariable (INVOKE_ASSIGN of getOrLoadModel) and its @Redirect of
		//UnbakedModel.bake live. Without the vanilla body in the method the mixin targets, its transformation
		//fails and every single model fails to bake (56042 warnings in one run). The forwarding call passes
		//this.field_40572, which is what the vanilla body uses itself, so behaviour is unchanged.
		registerFix("class_1088$class_7778", new RestoreVanillaMethodsFix(true, "method_45873"));

		//net/minecraft/client/world/ClientChunkManager (fabric-lifecycle-events-v1)
		//OptiFine creates its own net.optifine.ChunkOF instead of WorldChunk, so the mixin's
		//@At(value = "NEW", target = "WorldChunk") point is gone and the whole class fails to transform: the
		//client chunk manager cannot load and opening a world ends in "network protocol error".
		registerFix("class_631", new ObjectCreationPointFix(RemappingUtils.getClassName("class_2818"), "net/optifine/ChunkOF", "method_16020"));

		//Synthetic outer-instance / captured fields javac named while OptiFine recompiled these classes;
		//mods shadow them, so they have to carry the names the game has (see SyntheticFieldFix)
		registerFix("class_638$class_5612", new SyntheticFieldFix()); //ClientWorld$ClientEntityHandler.this$0
		registerFix("class_1088$class_7778", new SyntheticFieldFix()); //ModelLoader$BakerImpl.this$0
		registerFix("class_846$class_851$class_4578", new SyntheticFieldFix()); //ChunkBuilder$BuiltChunk$RebuildTask.this$1

		//net/minecraft/block/entity/BlockEntity
		skipClass("class_2586");
	}

	private void registerFix(String className, ClassFixer classFixer) {
		classFixes.computeIfAbsent(RemappingUtils.getClassName(className), s -> new ArrayList<>()).add(classFixer);
	}

	@SuppressWarnings("SameParameterValue") //Might be useful in future
	private void skipClass(String className) {
		skippedClass.add(RemappingUtils.getClassName(className));
	}

	public boolean shouldSkip(String className) {
		return skippedClass.contains(className);
	}

	public List<ClassFixer> getFixers(String className) {
		return classFixes.getOrDefault(className, Collections.emptyList());
	}
}
