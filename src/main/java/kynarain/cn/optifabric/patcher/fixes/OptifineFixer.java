/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kynarain.cn.optifabric.util.RemappingUtils;

public class OptifineFixer {

	public static final OptifineFixer INSTANCE = new OptifineFixer();

	private final Map<String, List<ClassFixer>> classFixes = new HashMap<>();
	private final List<ClassFixer> globalFixes = new ArrayList<>();
	private final Set<String> skippedClass = new HashSet<>();
	private final Set<String> extraClasses = new LinkedHashSet<>();

	private OptifineFixer() {
		//Applies to every class: members OptiFine kept under a name of its own, which neither the mappings nor a
		//contextual entry can resolve, are bridged to the name the game calls them by (see MissingOverrideFix).
		registerGlobalFix(new MissingOverrideFix());

		//This is the 26.x line, so the table below addresses the game by the official names it ships with:
		//Minecraft 26.1 and newer is unobfuscated, and there is no intermediary to address it by. (The
		//obfuscated 1.21.x releases keep their own table of intermediary ids, on their own branch, where this
		//table would name classes that do not exist.)
		registerOfficialNameFixes();
	}

	/**
	 * The registrations for the unobfuscated releases (Minecraft 26.1 and newer), where the runtime names are
	 * the official ones.
	 *
	 * <p>These conflicts were found by running the offline harness and its scanners against 26.1.2; only what
	 * the scanners report is listed, and every entry below is a real injection point that had gone missing.</p>
	 */
	private void registerOfficialNameFixes() {
		//net/minecraft/client/resources/model/ModelManager (fabric-model-loading-api-v1 ModelManagerMixin)
		//The 1.21.x entry for this class (class_1092 / method_65750 above) is the same conflict in a different
		//shape: there OptiFine's recompile renamed the lambda the mixin injects into, here the lambda keeps its
		//name and descriptor but the recompile dropped the Pair.of call the @At point needs - vanilla has one,
		//OptiFine's body has none, so the injection has no instruction to land on. Verified against both jars:
		//  javap -c <vanilla>  ... lambda$loadBlockModels$2 ... invokestatic com/mojang/datafixers/util/Pair.of
		//  javap -c <patched>  ... lambda$loadBlockModels$2 ... (no Pair.of at all)
		registerFix("net/minecraft/client/resources/model/ModelManager",
				new RestoreVanillaMethodsFix(true, "lambda$loadBlockModels$2"));

		//net/minecraft/client/multiplayer/ClientChunkCache (fabric-lifecycle-events-v1 ClientChunkCacheMixin)
		//This is the 1.21.x class_631 / method_16020 entry by its official names: OptiFine creates its own
		//net.optifine.ChunkOF instead of a LevelChunk, so the mixin's @At(value = "NEW", target = "LevelChunk")
		//point is gone and the whole class fails to transform - the client chunk cache cannot load and opening
		//a world ends in "network protocol error".
		registerFix("net/minecraft/client/multiplayer/ClientChunkCache",
				new ObjectCreationPointFix("net/minecraft/world/level/chunk/LevelChunk", "net/optifine/ChunkOF", "replaceWithPacketData"));

		//net/minecraft/client/renderer/LevelRenderer (fabric-renderer-api-v1 LevelRendererMixin.hasMaterialFlagProxy)
		//OptiFine reduced the vanilla extractBlockOutline to a thin wrapper that forwards to its own
		//three-argument overload, so the vanilla body - and the BlockStateModel.hasMaterialFlag call the mixin's
		//@Redirect needs - is gone. The vanilla body goes back, and OptiFine's now-unused overload goes away with
		//it: the mixin names this method without a descriptor, and two methods with that name made MixinExtras
		//fail to build the local-variable context for the handler (LVTGeneratorError), which fails the class.
		registerFix("net/minecraft/client/renderer/LevelRenderer",
				new RestoreVanillaMethodsFix(true, "extractBlockOutline"));
		registerFix("net/minecraft/client/renderer/LevelRenderer",
				new DropVanillaAbsentOverloadsFix("extractBlockOutline"));

		//net/minecraft/client/renderer/extract/LevelExtractor (fabric-renderer-api-v1 LevelExtractorMixin)
		//The same conflict one release later, on the class 26.2 moved it to. The entry above stopped matching
		//because 26.2 extracted the whole level-render-state pass into this new class, and Fabric API followed
		//the move (its handler is LevelExtractorMixin.hasMaterialFlagProxy now, not LevelRendererMixin):
		//
		//  AtTargetScan: [NO INSTRUCTION] LevelExtractor.extractBlockOutline(Camera, LevelRenderState)V has no
		//  INVOKE of BlockStateModel.hasMaterialFlag(I)Z
		//
		//OptiFine again reduced the vanilla body to a wrapper that forwards to its own three-argument overload,
		//so the call the handler redirects is not there. Both entries are kept rather than swapped: this one is
		//inert on 26.1.2 (the class does not exist there, and a fixer whose target is absent does nothing) and
		//the one above is inert here, which is what lets one source serve both releases of the line.
		registerFix("net/minecraft/client/renderer/extract/LevelExtractor",
				new RestoreVanillaMethodsFix(true, "extractBlockOutline"));
		registerFix("net/minecraft/client/renderer/extract/LevelExtractor",
				new DropVanillaAbsentOverloadsFix("extractBlockOutline"));

		//net/minecraft/client/renderer/ScreenEffectRenderer (fabric-renderer-api-v1 ScreenEffectRendererMixin)
		//The 1.21.x class_4603 / method_24225 entry by its official names. Its onReturnGetInWallBlockState takes a
		//@Local BlockPos$MutableBlockPos, and vanilla's getViewBlockingState keeps one in scope while OptiFine's
		//recompiled body does not - so the callback cannot be built and the class fails to transform. The vanilla
		//body has exactly the local layout the mixin was written against.
		registerFix("net/minecraft/client/renderer/ScreenEffectRenderer",
				new RestoreVanillaMethodsFix(true, "getViewBlockingState"));

		//net/minecraft/client/renderer/item/CuboidItemModelWrapper (fabric-renderer-api-v1 CuboidItemModelWrapperMixin)
		//The 1.21.x class_10430 / method_65584 entry, on the class that replaced BlockModelWrapper. Its update()
		//@Inject(at = RETURN) needs locals OptiFine's recompiled body no longer has, so item models fail to bake
		//and the item textures disappear.
		//
		//The drop below is not optional, and the same pairing is needed wherever OptiFine reduced the vanilla
		//method to a wrapper and moved the body into an overload of its own: with two methods of that name left
		//in the class, MixinExtras cannot build the local-variable context for the handler
		//("LVTGeneratorError: Could not locate method metadata for update generating LVT") and every item model
		//fails to transform. ScreenEffectRenderer below needs no drop because OptiFine left it a single method.
		registerFix("net/minecraft/client/renderer/item/CuboidItemModelWrapper",
				new RestoreVanillaMethodsFix(true, "update"));
		//allowNonPrivate: OptiFine's nine-argument update is declared nowhere in the game jar - checked against
		//the whole client - so the in-class reference scan is complete after all.
		registerFix("net/minecraft/client/renderer/item/CuboidItemModelWrapper",
				new DropVanillaAbsentOverloadsFix(true, "update"));

		//net/minecraft/client/gui/render/GuiRenderer$Draw (fabric-rendering-v1 GuiRendererDrawAccessor, used by
		//GuiRendererMixin.fixNonQuadIndexing)
		//The 26.x baseline crash. fabric-rendering-v1 wraps RenderPass.setIndexBuffer inside GuiRenderer.executeDraw
		//and takes the Draw it is drawing as a "... @Coerce GuiRendererDrawAccessor draw" argument. Mixin validates
		//that coercion with ClassInfo#canCoerce, i.e. "is GuiRendererDrawAccessor a supertype of GuiRenderer$Draw
		//right now" - and against OptiFine's recompiled Draw it is not, so the whole class fails:
		//
		//  Mixin apply for mod fabric-rendering-v1 failed fabric-rendering-v1.mixins.json:GuiRendererMixin from mod
		//  fabric-rendering-v1 -> net.minecraft.client.gui.render.GuiRenderer: InvalidInjectionException
		//  @WrapOperation operation wrapper method net/minecraft/client/gui/render/GuiRenderer::fixNonQuadIndexing
		//  ... Cannot @Coerce argument type net.minecraft.client.gui.render.GuiRenderer$Draw at index 4 to
		//  net.fabricmc.fabric.mixin.client.rendering.GuiRendererDrawAccessor
		//
		//This is NOT a renamed member, and it is not a method-body problem: the wrapped method
		//(GuiRenderer.executeDraw(Draw, RenderPass)V) has the same descriptor and the same local layout on both
		//sides (local 1 = draw:Lnet/minecraft/client/gui/render/GuiRenderer$Draw;), and OptiFine's GuiRenderer$Draw
		//has the same members as the game's. What is missing is the *interface* Fabric API's accessor mixin puts on
		//Draw, so declaring it - and leaving Mixin to contribute the accessors it generates from that same mixin - is
		//the repair. RestoreVanillaMethodsFix cannot express this (it only swaps method bodies) and there is no method
		//of Draw's to restore here. Measured both ways: with this entry the line reaches the title screen, without it
		//the same three jars die at 23 s on the message above.
		//
		//Do not add the accessors here: GuiRendererDrawAccessor generates fabric$pipeline()/fabric$Draw() itself, and
		//a class that already declares them fails the class in MixinApplicatorStandard.applyAccessors with
		//"cannot overwrite method ... because @Overwrite is required by the parent configuration" (measured).
		registerFix("net/minecraft/client/gui/render/GuiRenderer$Draw",
				new AddInterfaceFix("net/minecraft/client/gui/render/GuiRenderer$Draw"));


		//net/minecraft/client/renderer/chunk/SectionCompiler (fabric-renderer-api-v1 SectionCompilerMixin)
		//Two of its handlers inject into the compile loop: one wraps ModelBlockRenderer.tesselateBlock, the other
		//sits before BlockPos.betweenClosed. Neither call survives in OptiFine's recompiled body. The vanilla body
		//goes back, and OptiFine's own compile(SectionPos, ChunkCacheOF, ..., int, int, int) has its name moved
		//aside rather than removed: it is public and SectionRenderDispatcher$RenderSection$RebuildTask.doTask
		//calls it, so removing it is a NoSuchMethodError on the first chunk rebuild. Leaving both names in place
		//is no good either - the mixin names compile without a descriptor and then scans 0 targets.
		registerFix("net/minecraft/client/renderer/chunk/SectionCompiler",
				new RestoreVanillaMethodsFix(true, "compile"));
		registerFix("net/minecraft/client/renderer/chunk/SectionCompiler",
				new DropVanillaAbsentOverloadsFix(true, true, "compile"));

		//...and the caller follows it to the new name. Only the invoked name changes, so no stack map or
		//injection offset moves.
		registerFix("net/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask",
				new CallSiteRedirectFix("net/minecraft/client/renderer/chunk/SectionCompiler", "compile",
						"(Lnet/minecraft/core/SectionPos;Lnet/optifine/override/ChunkCacheOF;Lcom/mojang/blaze3d/vertex/VertexSorting;"
								+ "Lnet/minecraft/client/renderer/SectionBufferBuilderPack;III)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
						"optifabric$compile",
						"OptiFine's compile overload was renamed so the mixin's descriptor-less name is unambiguous again"));

		//...and 26.2's caller of the same method. The class is the same code under a new name: Mojang renamed
		//SectionRenderDispatcher$RenderSection$RebuildTask to ...$CompileTask in 26.2, so the redirect above
		//stopped finding any call site and the renamed overload was left with its only caller still invoking
		//the old name - a NoSuchMethodError on the first chunk rebuild, which is the first frame after a world
		//is opened. RuntimeContractScan reported exactly that:
		//
		//  [patched caller] ...$CompileTask.doTask(...) -> SectionCompiler.compile(SectionPos, ChunkCacheOF,
		//  VertexSorting, SectionBufferBuilderPack, III)Results      (unpatched callers 0, optifine callers 0)
		//
		//Kept alongside the RebuildTask entry for the same reason as the LevelExtractor pair above: one source,
		//two releases of the line, and an absent target is a no-op.
		registerFix("net/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$CompileTask",
				new CallSiteRedirectFix("net/minecraft/client/renderer/chunk/SectionCompiler", "compile",
						"(Lnet/minecraft/core/SectionPos;Lnet/optifine/override/ChunkCacheOF;Lcom/mojang/blaze3d/vertex/VertexSorting;"
								+ "Lnet/minecraft/client/renderer/SectionBufferBuilderPack;III)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
						"optifabric$compile",
						"26.2 renamed the chunk rebuild task class, so its call to OptiFine's renamed compile has to follow too"));

		//Fabric's FRAPI hook for terrain models injects into the vanilla loop (at BlockPos.betweenClosed) and
		//redirects the block tesselation call in it. OptiFine's own overload has no such loop, so that hook now
		//lives in the restored method above, which nothing calls: a model's emitQuads - better grass, and anything
		//else that needs the world around a block - was never asked for anything and its geometry was simply
		//absent. The call inside OptiFine's method is pointed at our bridge instead, which does what Fabric's hook
		//would have done (see FrapiTesselateBridgeFix and OptifineFrapiBridge).
		registerFix("net/minecraft/client/renderer/chunk/SectionCompiler", new FrapiTesselateBridgeFix());

		//net/minecraft/client/renderer/feature/BlockFeatureRenderer (fabric-renderer-api-v1 BlockFeatureRendererMixin)
		//The moving-block path, and the crash the first real world produced: beforeInitBlockRenderer hands FRAPI's
		//own AltModelBlockRenderer and QuadEmitter to renderMovingBlockSubmits through @Local, so on the first
		//moving block the game calls Renderer.get().altModelBlockRenderer(...) - which finds the placeholder
		//registered above, whose whole purpose is to refuse to draw. This is the 1.21.x moving-block entry
		//(class_11681 / method_72998) by its official names: OptiFine does not patch this class, so it is taken
		//over on our own, the method the hook injects into is moved aside as dead code carrying the vanilla body
		//(the handler's @Local sugar needs its locals), and the real method keeps drawing through OptiFine.
		//
		//That redirect is what the 26.1.2 baseline was measured on, and it is kept for now. A real renderer does
		//exist behind the hook again - the 26.x jar no longer declares contains_renderer, so Indigo registers its
		//own (see RendererApiFallback) - which means the hook *could* be left live and these submits could go
		//through Indigo instead. That is a different rendering path from the one verified in game today, so it is
		//a change to measure on its own rather than assume.
		registerExtraClass("net/minecraft/client/renderer/feature/BlockFeatureRenderer",
				new StubInjectionTargetFix("renderMovingBlockSubmits", null, "optifabric$movingBlocks"));
		registerExtraClass("net/minecraft/client/renderer/feature/BlockFeatureRenderer",
				new CallSiteRedirectFix("net/minecraft/client/renderer/feature/BlockFeatureRenderer",
						"renderMovingBlockSubmits", null, "optifabric$movingBlocks",
						"the hook has to inject into a copy nobody calls, and the real method still has to draw moving blocks"));

		//...and the same for the ordinary block model path. Its onReturnRenderBlockModelSubmits ends by asking the
		//renderer for a QuadEmitter to put FRAPI's own quads through, which is why it had to be inert while the
		//only renderer around was a placeholder returning inert objects: the quads went nowhere and OptiFine drew
		//the world. Kept inert for the same reason as the moving-block path above - the hook injects into the dead
		//copy and the real method draws, which is the path this baseline was measured on.
		registerExtraClass("net/minecraft/client/renderer/feature/BlockFeatureRenderer",
				new StubInjectionTargetFix("renderBlockModelSubmits", null, "optifabric$blockModels"));
		registerExtraClass("net/minecraft/client/renderer/feature/BlockFeatureRenderer",
				new CallSiteRedirectFix("net/minecraft/client/renderer/feature/BlockFeatureRenderer",
						"renderBlockModelSubmits", null, "optifabric$blockModels",
						"the hook has to inject into a copy nobody calls, and the real method still has to draw block models"));
		//EXPERIMENTAL, off unless -Doptifabric.experimentalPerDraw=true is passed to the game. 26.1.2 ran OptiFine's
		//per-draw shader state calls (pushProgram/popProgram + ShadersRender.preRender/postRender) around every draw
		//inside renderer/rendertype/RenderType.draw(MeshData). 26.2 moved that entry point to
		//renderer/rendertype/PreparedRenderType.drawFromBuffer, a class OptiFine's 26.2 patch set does not patch at
		//all, so the calls have no caller left (see docs/PORT_26.x.md for the measurement: 150 distinct shader call
		//targets reach game classes on 26.1.2, 137 on 26.2, and the 13 that disappeared were exactly these plus the
		//entity/particle/hand ones). This takes the class over and wraps its draw entry, so the hypothesis "terrain
		//is drawn but never lands in the shader pipeline because OptiFine's program is not rebound per draw" can be
		//tested in game. It changes nothing unless the property is set, and whether it helps is only knowable there.
		//
		//The registration itself is behind the property too, not just the fixer: taking a class over is a change to
		//the patched set on its own (the pipeline would report 563 classes instead of the 562 the released build
		//records), and an experiment that is off has to leave the default path exactly as it was.
		//
		//The format probe (-Doptifabric.experimentalFormatProbe=true) is read-only and rides on the same method, so
		//the takeover happens when either switch is on.
		if (Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifinePerDrawState.PROPERTY)
				|| Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifineFormatProbe.PROPERTY)) {
			registerExtraClass("net/minecraft/client/renderer/rendertype/PreparedRenderType",
					new PerDrawShaderStateFix());
		}

		//The chunk-section pass is OptiFine-patched already, so the section probe is a normal fix on it rather than
		//a class takeover. Measured: terrain does not go through PreparedRenderType.drawFromBuffer at all, which is
		//why the per-draw experiment there could not have said anything about terrain.
		if (Boolean.getBoolean(kynarain.cn.optifabric.mod.OptifineFormatProbe.PROPERTY)) {
			registerFix("net/minecraft/client/renderer/chunk/ChunkSectionsToRender", new SectionDrawProbeFix());
			registerFix("com/mojang/blaze3d/opengl/GlCommandEncoder", new RenderPassProbeFix());
		}
	}

	private void registerFix(String className, ClassFixer classFixer) {
		//RemappingUtils prefixes "net.minecraft." - right for intermediary names and renames, wrong for the classes
		//that keep their Mojang name (com/mojang/...), which the lookup asks for verbatim.
		String key = className.indexOf('/') >= 0 ? className : RemappingUtils.getClassName(className);
		classFixes.computeIfAbsent(key, s -> new ArrayList<>()).add(classFixer);
	}

	/** A class OptiFine does not patch, but that still needs one of our fixers (Fabric API injects into it). */
	private void registerExtraClass(String className, ClassFixer fixer) {
		String name = RemappingUtils.getClassName(className);
		classFixes.computeIfAbsent(name, s -> new ArrayList<>()).add(fixer);
		extraClasses.add(name);
	}

	public Set<String> getExtraClasses() {
		return extraClasses;
	}

	private void registerGlobalFix(ClassFixer classFixer) {
		globalFixes.add(classFixer);
	}

	@SuppressWarnings("SameParameterValue") //Might be useful in future
	private void skipClass(String className) {
		skippedClass.add(RemappingUtils.getClassName(className));
	}

	public boolean shouldSkip(String className) {
		return skippedClass.contains(className);
	}

	public List<ClassFixer> getFixers(String className) {
		List<ClassFixer> specific = classFixes.get(className);

		if (globalFixes.isEmpty()) {
			return specific == null ? Collections.emptyList() : specific;
		}

		List<ClassFixer> fixers = new ArrayList<>(specific == null ? Collections.emptyList() : specific);
		fixers.addAll(globalFixes);

		return fixers;
	}
}
