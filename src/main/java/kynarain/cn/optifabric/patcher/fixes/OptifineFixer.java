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

		//This line addresses the game by its stable intermediary ids, which is what the obfuscated 1.21.x
		//releases run on. (A second table, addressing the game by its official names, used to be registered
		//here for the 26.x line; that line has its own branch and its own copy of this code now.)
		registerIntermediaryNameFixes();
	}

	/** The registrations for the obfuscated releases, addressed by intermediary ids. */
	private void registerIntermediaryNameFixes() {
		//net/minecraft/client/render/chunk/ChunkBuilder$ChunkData
		registerFix("class_846$class_849", new ChunkDataFix());

		//net/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask
		registerFix("class_846$class_851$class_4578", new ChunkRendererFix());

		//net/minecraft/client/render/block/BlockModelRenderer$AmbientOcclusionCalculator
		registerFix("class_778$class_780", new AmbientOcclusionCalculatorFix());

		//net/minecraft/client/Keyboard
		//1.21.11 rewrote the key dispatch: the methods upstream reverted (method_1454/1458/1473 and the
		//five argument method_1466) no longer exist in the game at all, so there is nothing left to revert there.
		//The releases before 1.21.6 still have them, and OptiFine's build for those drops method_1454, which
		//fabric-screen-api-v1's KeyboardMixin injects into - so the fixer is registered again, now skipping the
		//methods a release does not have instead of throwing over them.
		registerFix("class_309", new KeyboardFix());

		//net/minecraft/client/texture/SpriteAtlasTexture
		registerFix("class_1059", new SpriteAtlasTextureFix());

		//net/minecraft/client/particle/ParticleManager
		registerFix("class_702", new ParticleManagerFix());

		//net/minecraft/client/particle/ParticleEngine (sophisticatedcore's client.ParticleEngineMixin)
		//The recompile moved the block-breaking particle loop into a lambda of its own - the patched class has
		//lambda$addBlockDestroyEffects$13 with exactly the descriptor the mixin asks for, and no method_34020 at
		//all. @ModifyArgs resolves its target by name AND descriptor, finds nothing, and with require = 1 that
		//fails the whole class: the crash surfaces as "Mixin transformation of net.minecraft.class_702 failed"
		//during OptiFine's own Reflector bootstrap. The vanilla body has the ParticleEngine.add call site the
		//mixin injects at, and nothing in the patched class (or in any other patched class) refers to
		//method_34020, so restoring it gives the injection a target and stays dead code.
		//(This is RestoreVanillaMethodsFix and not StubInjectionTargetFix: the stub fixer needs the method to
		//still be in OptiFine's class so it can rename it, and returns immediately when it is gone.)
		registerFix("class_702", new RestoreVanillaMethodsFix("method_34020"));

		//net/minecraft/client/render/model/json/ModelOverrideList
		registerFix("class_806", new ModelOverrideListFix());

		//net/minecraft/client/gl/ShaderProgram
		//OptiFine's convenience constructor creates the Identifier before this(), so Fabric API's
		//@ModifyArg into the constructor lands before super() and Mixin refuses to apply it
		registerFix("class_5944", new DelegatingConstructorFix());

		//1.21.1 needs more than the constructor: OptiFine's rewritten loadShader creates the Identifier with
		//Identifier.of (method_60654) while the game's own body uses Identifier.ofVanilla (method_60656) - and
		//ShaderProgramMixin wraps the latter in *both* places, so without this the class still fails to
		//transform ("Mixin transformation of net.minecraft.class_5944 failed" before the title screen).
		registerFix("class_5944", new VanillaFactoryCallFix("<init>", "method_34579"));

		//Helpers OptiFine's recompiled classes no longer have, but Fabric API's mixins inject into.
		//(class_309/Keyboard needs no entry: KeyboardFix already puts the vanilla methods back.)
		//net/minecraft/client/render/entity/EntityRenderers (fabric-rendering-v1 EntityRenderersMixin)
		registerFix("class_5619", new RestoreVanillaMethodsFix("method_32174", "method_32175"));

		//net/minecraft/server/world/ThreadedAnvilChunkStorage (fabric-lifecycle-events-v1)
		//method_60440 (1.21.11): the recompile moved it into a differently named lambda, and
		//fabric-lifecycle-events-v1 injects into it - the same shape as the two entries above.
		registerFix("class_3898", new RestoreVanillaMethodsFix("method_17227", "method_18843", "method_60440"));

		//net/minecraft/Util (The Twilight Forest's twilightforest.mixins.json:UtilMixin, @Redirect)
		//OptiFine recompiled this class from a release in which the game itself had downgraded one logging call:
		//inside method_29191 - the private data fixer lookup its public helper method_29187 delegates to - the same
		//catch block that vanilla 1.21.1 writes as an org.slf4j.Logger.error(String, Object) call on field_1129
		//with the caught exception's type reference reads Logger.debug(...) in the patched class. That single
		//opcode is the whole difference: the rest of the method is byte for byte the same, and so is its exception
		//table. That call site is exactly what Twilight Forest redirects, so with the debug call in place the
		//redirect scans nothing, fails its injection check (0/1 succeeded, "Scanned 0 target(s)") and takes
		//class_156 down with it: "Mixin transformation of net.minecraft.class_156 failed" before the title screen.
		//The vanilla body is put back - and it has to be the *body*, not a name: no fixer can reach the call
		//because it is inside a try block whose argument is the caught exception, and a re-created call in front of
		//the method would log on every lookup instead of only when the fixer is missing (see InjectionCallPointFix).
		//Reverting OptiFine's own edit costs nothing here: it changed a log level and nothing else, that level is
		//the one the game's own release states, and no OptiFine code calls the method expecting debug output.
		registerFix("class_156", new RestoreVanillaMethodsFix(true, "method_29191"));

		//net/minecraft/client/render/LevelRenderer (c2me-client-uncapvd MixinFogRenderer, @ModifyArg)
		//The same class, the other half of what C2ME needs: OptiFine's recompile emitted the Runnable body the game
		//declares as method_37365(Lnet/minecraft/class_4184;FZF)V under javac's own lambda name
		//lambda$updateCameraAndRender$1 and pointed the class's own bootstrap method handle at it. Descriptor,
		//access flags, registration site (method_22710, three LambdaMetafactory indys on both sides) and the
		//class_758.method_3211 call the mixin's @ModifyArg wraps are all unchanged - only the name moved, so the
		//mixin's name+descriptor lookup finds nothing and defaultRequire 1 fails the class during OptiFine's
		//Reflector bootstrap, exactly like the method_62214 entry below. LambdaMethodRefFix gives the lambda the
		//game's name back - the same repair class_329 gets - and it has to be that and not
		//RestoreVanillaMethodsFix("method_37365"): restoring the body would put vanilla's copy next to OptiFine's
		//lambda and leave OptiFine's path in use, so the clamp the mixin injects would be applied to code that
		//never runs. It is registered before the method_62214 entry because getFixers preserves registration order.
		registerFix("class_761", new LambdaMethodRefFix());

		//Synthetic outer-instance fields OptiFine's recompile renamed to this$0 while the game's names are
		//field_17443 / field_18255. c2me-base's @Accessor IThreadedAnvilChunkStorageLevelManager and
		//c2me-rewrites-chunk-system's @Shadow MixinChunkTicketManagerTicketDistanceLevelPropagator ask for them by
		//name, and an accessor or shadow that cannot be located fails the whole target class - reachable only once
		//a world starts ("Mixin transformation of net.minecraft.class_3898$class_3216 failed" /
		//"…class_3204$class_4077 failed" on the integrated-server thread). Same rule as the four SyntheticFieldFix
		//entries below.
		registerFix("class_3898$class_3216", new SyntheticFieldFix());
		registerFix("class_3204$class_4077", new SyntheticFieldFix());

		//net/minecraft/client/render/LevelRenderer (fabric-rendering-v1 LevelRendererMixin, @ModifyExpressionValue)
		//OptiFine's recompile turned this lambda body into lambda$addMainPass$1 with one extra parameter, so the
		//vanilla method - name and descriptor - is simply not in the patched class any more. Mixin resolves an
		//injection target by name AND descriptor, fails the whole class when it cannot find it (require = 1) and
		//the crash surfaces as "Mixin transformation of net.minecraft.class_761 failed" during OptiFine's own
		//Reflector bootstrap. Restoring the vanilla body gives the injection its target back.
		registerFix("class_761", new RestoreVanillaMethodsFix("method_62214"));

		//net/minecraft/client/resources/model/ModelManager (fabric-model-loading-api-v1)
		//Same shape again: the lambda the mixin injects into is called lambda$loadBlockModels$7 after the
		//recompile, and the vanilla name the mixin asks for is gone.
		registerFix("class_1092", new RestoreVanillaMethodsFix("method_65750"));

		//net/minecraft/client/resources/model/ModelBakery (fabric-model-loading-api-v1 ModelBakeryMixin)
		//The second real launch crashed here: @WrapOperation asks for these two methods by name and descriptor
		//and OptiFine's recompiled ModelBakery no longer has either.
		registerFix("class_1088", new RestoreVanillaMethodsFix("method_68018", "method_68019"));

		//net/minecraft/client/render/chunk/ChunkRendererRegionBuilder (fabric-block-view-api-v2)
		//OptiFine reduced build() to a call to its own createRegion() and moved the loop - and the four loop
		//counters plus the Chunk[][] array Fabric's createDataMap captures with CAPTURE_FAILHARD - into it.
		//The vanilla body has exactly the local layout Fabric was compiled against, and OptiFine's createRegion
		//stays available for OptiFine's own callers.
		registerFix("class_6850", new RestoreVanillaMethodsFix(true, "method_39969"));

		//and the same method has to hand OptiFine's region its section position: the restored vanilla body calls the
		//vanilla constructor, which leaves that field null (see RegionSectionPosFix).
		registerFix("class_6850", new RegionSectionPosFix("class_853", "class_4076", "method_18677", "method_39969"));

		//net/minecraft/client/render/model/ModelLoader$BakerImpl (fabric-model-loading-api-v1)
		//Same pattern: OptiFine's bake(id, settings) only forwards to its own bake(id, settings, textureGetter),
		//which is where Fabric's @ModifyVariable (INVOKE_ASSIGN of getOrLoadModel) and its @Redirect of
		//UnbakedModel.bake live. Without the vanilla body in the method the mixin targets, its transformation
		//fails and every single model fails to bake (56042 warnings in one run). The forwarding call passes
		//this.field_40572, which is what the vanilla body uses itself, so behaviour is unchanged.
		registerFix("class_1088$class_7778", new RestoreVanillaMethodsFix(true, "method_45873"));

		//The Fabric API releases of 1.21.5 and older inject into helpers OptiFine's recompile dropped on those
		//releases: the model baker's deserialisation helper (method_65737 on 1.21.4, method_61072 on 1.21.1) and
		//three InGameHud layers (fabric-model-loading-api-v1 and fabric-rendering-v1). Same recipe as above - the
		//vanilla method is added back next to OptiFine's code. Those ids do not exist on the releases where OptiFine
		//kept the methods, and the fixer then does nothing.
		registerFix("class_1088", new RestoreVanillaMethodsFix("method_65737", "method_61072"));

		//Restoring the three InGameHud layers is not enough on its own: OptiFine's recompile also turned the method
		//references the constructor registers them with into lambdas of its own (lambda$new$0/1/2), and
		//fabric-rendering-v1's InGameHudMixin matches the *bootstrap handle* through its custom LayerInjectionPoint,
		//so with the lambdas in place none of the three injection points exists and Mixin fails the whole class.
		//This runs first and gives those lambdas the names the game uses, which is why the fixer below then finds
		//the methods already there and adds nothing (its vanilla bodies would lose OptiFine's own additions).
		registerFix("class_329", new LambdaMethodRefFix());

		//Where OptiFine's build kept no lambda for them either, the vanilla methods are added back next to its code.
		registerFix("class_329", new RestoreVanillaMethodsFix("method_55806", "method_55807", "method_55808"));

		//net/minecraft/client/world/ClientLevel (porting_lib's porting_lib_client_events, ClientLevelMixin)
		//The recompile turned the method the game registers for its own colour resolvers into a lambda of
		//OptiFine's: the patched class declares lambda$new$3(Object2ObjectArrayMap) where the game declares
		//method_23778(Object2ObjectArrayMap), same descriptor, and the BootstrapMethods entry that used to point at
		//method_23778 now points at the lambda. Porting Lib's @Inject asks for method_23778 by name, so it resolves
		//nothing ("could not find any targets matching ... class_638;method_23778(...)") and Mixin fails the whole
		//class. Renaming the lambda is the exact repair this fixer was written for (see LambdaMethodRefFix, whose
		//class_329 entry is the same shape) - and because the lambda *is* the method the game registered, the
		//bootstrap handle matches again the moment it carries the game's name.
		registerFix("class_638", new LambdaMethodRefFix());

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

		//net/minecraft/client/resources/model/ModelManager$1 (fabric-renderer-api-v1 ModelManager1Mixin)
		//The anonymous SpriteGetter keeps the fields javac synthesised for the two captured SpriteLoader
		//preparations, and both have the same type, so only their position identifies them. Fabric API shadows
		//field_61871 and field_64469, and a shadow it cannot locate fails the whole mixin.
		registerFix("class_1092$1", new SyntheticFieldFix());

		//net/minecraft/client/renderer/block/BlockRenderDispatcher$1 - javac's synthetic switch map for the
		//RenderShape switch in method_3353, the method the class_776 entry below restores. OptiFine's recompiled
		//class declares that map as $SwitchMap$net$minecraft$world$level$block$RenderShape, while the body restored
		//into method_3353 reads it under the game's own name field_4172, so the field has to carry both names:
		//without this registration the game's name is not on the class at all and the first block entity a mod
		//renders dies on the render thread with
		//  java.lang.NoSuchFieldError: Class net.minecraft.class_776$1 does not have member field 'int[] field_4172'
		//  at net.minecraft.class_776.method_3353(class_776.java:97)
		//  at twilightforest.client.renderer.block.CandelabraRenderer.render(CandelabraRenderer.java:38)
		//and takes the client down with it. Measured in a world, standing where the candelabra is, with the six jars
		//the line ships - see the report. The rename half of the same fix is what puts the game's name on the
		//declaration, which is the part mods shadow; the extra field is what keeps OptiFine's own reads working.
		registerFix("class_776$1", new SyntheticFieldFix());

		//net/minecraft/client/render/block/LiquidBlockRenderer (fabric-rendering-fluids-v1)
		//The recompile dropped the Biome colour call this mixin wraps. OptiFine's own fluid rendering stays
		//untouched: an inert call site is put back in front of the method so the mixin finds its point.
		registerFix("class_775", new InjectionCallPointFix("class_1163", "method_4961",
				"(Lnet/minecraft/class_1920;Lnet/minecraft/class_2338;)I", "method_3347"));

		//net/minecraft/client/render/ScreenEffectRenderer (fabric-renderer-api-v1 ScreenEffectRendererMixin)
		//The third real launch: MixinExtras' sugar reports
		//  "Failed to validate sugar @Local class_2338.class_2339 ... at instruction InjectionNode[Insn [ARETURN]]"
		//Vanilla keeps a MutableBlockPos in scope at that return; OptiFine's recompiled body does not, so the
		//callback cannot be built and the class fails. The vanilla body has exactly the local layout the mixin was
		//written against (the same repair as method_39969 for fabric-block-view-api-v2 in the 1.20.6 port).
		registerFix("class_4603", new RestoreVanillaMethodsFix(true, "method_24225"));

		//net/minecraft/client/render/item/BlockModelWrapper (fabric-renderer-api-v1 BlockModelWrapperMixin)
		//Its @Inject(at = RETURN) needs locals that OptiFine's recompiled update() no longer has, so every single item
		//model fails to bake and all item textures disappear. Same repair as class_4603 above.
		registerFix("class_10430", new RestoreVanillaMethodsFix(true, "method_65584"));

		//net/minecraft/client/render/block/entity/... the moving-block path: with contains_renderer declared (OptiFine is
		//the renderer) Fabric's RendererManager stays empty, and this hook calls Renderer.get() and throws.
		//OptiFine does not patch class_11681, so it is taken over on our own (see registerExtraClass). The descriptor is
		//left out on purpose: it differs between releases (the method takes a Camera in 1.21.8 and a Vec3d in 1.21.11),
		//and a fixer that hardcodes one silently stops firing on the other. These two classes only exist from 1.21.6 on,
		//where the take-over is simply skipped.
		registerExtraClass("class_11681", new StubInjectionTargetFix("method_72998", null, "optifabric$movingBlocks"));

		//Renaming it is only half of the story: the game calls it from the neighbouring class_11684.method_73002, and
		//that call would land on the copy Mixin injected into - which is exactly what the multiplayer crash showed
		//(class_11684.method_73002 -> class_11681.method_72998 -> handler$zmb000$...beforeRenderMovingBlocks). So the
		//caller is taken over as well and its call moved onto the renamed method (see CallSiteRedirectFix).
		registerExtraClass("class_11684", new CallSiteRedirectFix("class_11681", "method_72998", null, "optifabric$movingBlocks",
				"the injected copy must stay uncalled, and the vanilla body still has to render moving blocks"));

		//fabric-rendering-v1's BEFORE_BLOCK_OUTLINE hook reads a world render context that OptiFine's pass
		//structure never fills in, and dies with a NullPointerException (see StubInjectionTargetFix). No descriptor
		//here either: 1.21.8's method_62210 takes a Camera where 1.21.11's takes a Vec3d, and this has to fire on both.
		registerFix("class_761", new StubInjectionTargetFix("method_62210", null, "optifabric$blockOutline"));
		//net/minecraft/block/entity/BlockEntity
		//Upstream skips OptiFine's BlockEntity, and skipping it leaves five references dangling: OptiFine adds
		//hasCustomOutlineRendering (from its Forge compatibility interface) and the nbtTag/nbtTagUpdateMs fields,
		//and both its own RandomTileEntity and the recompiled class_757 call them - a NoSuchMethodError waiting
		//for the first block entity render. The class is applied here; the scanners check what that costs.
		//net/minecraft/client/renderer/GameRenderer
		//OptiFine's build adds two locals of its own in the middle of render() (guiFarPlane and guiOffsetZ, both
		//floats), which pushes the game's Matrix4f, Matrix4fStack and GuiGraphics one or two slots further up.
		//Mixin hands a method's locals to an @Inject handler strictly by slot order, so any handler that captures
		//this method's locals the way the game declares them - Architectury's MixinGameRenderer is the one that
		//reported it - fails the whole class with
		//  InjectionError: LVT in class_757::method_3192 has incompatible changes at opcode 601
		// and the game dies on its first frame. Moving OptiFine's own locals past the end of the local range puts
		// the game's locals back into the order those handlers were compiled against (see LocalSlotLayoutFix).
		//The descriptor is left out on purpose, exactly as in the two entries above: it differs between releases,
		//and the fixer then works off the descriptor OptiFine's own class carries.
		registerFix("class_757", new LocalSlotLayoutFix(null, "method_3192"));

		//net/minecraft/client/render/LevelRenderer again, the same reason on a different method (Porting Lib's
		//porting_lib_base LevelRendererMixin, @Inject at the DebugRenderer.render call in method_22710 = renderLevel).
		//Its handler captures that call's PoseStack through MixinExtras with @Local(index = 24), and OptiFine's
		//recompile moved the locals around it, so the sugar cannot build the callback:
		//  SugarApplicationException: Failed to validate sugar @Local(index = 24) class_4587 on method
		//  port_lib$renderEntityOutline ... in target method net/minecraft/class_761::method_22710(...)
		//  Caused by: SugarApplicationException: Unable to find matching local!
		// and the injection then reports "expected 1 invocation(s) but 0 succeeded", which fails the class. The
		// game's own local layout is what that index was written against, so OptiFine's extra locals are moved past
		// the end of the local range exactly as for class_757 above. No descriptor either, for the same reason.
		registerFix("class_761", new LocalSlotLayoutFix(null, "method_22710"));

		//net/minecraft/client/render/LevelRenderer once more, and the same method again: Porting Lib's
		//porting_lib_blocks LevelRendererMixin (client.LevelRendererMixin, shipped nested inside the Twilight
		//Forest jar) wraps the block-entity Iterator in method_22710 with an implicit @ModifyVariable:
		//  @ModifyVariable(method = "renderLevel", at = @At("STORE"),
		//     slice = @Slice(from = @At(INVOKE, target = "…CompiledSection.getRenderableBlockEntities()Ljava/util/List;"),
		//                    to   = @At(INVOKE, target = "…OutlineBufferSource.endOutlineBatch()V")))
		//  private static Iterator port_lib$wrapBlockEntityIterator(Iterator iterator)
		//The discriminator counts every local whose descriptor is java/util/Iterator from slot 1 onwards, and on
		//the patched class it finds two or three of them - "Found 2 candidate variables but exactly 1 is
		//required" / "Found 3 …" at the twelve ASTOREs inside that slice. Every one of those injection points is
		//then dropped, which fails the whole class with "expected 1 invocation(s) but 0 succeeded" during
		//OptiFine's own Reflector bootstrap and kills the client before the title screen. The game's own method
		//has no Iterator-typed table entry in that region at all: the candidates are entries Locals generates
		//from the code when the LocalVariableTable has nothing in range for those slots, which is why they are
		//named var26/var29 after their slots and why retyping the shipped table cannot reach them (see
		//ImplicitDiscriminatorMaskFix). It runs after LocalSlotLayoutFix because it shadows slots by appending
		//to the very table that fixer reorders.
		registerFix("class_761", new ImplicitDiscriminatorMaskFix("method_22710", "Ljava/util/Iterator;",
				"net/minecraft/class_846$class_849.method_3642()Ljava/util/List;",
				"net/minecraft/class_4618.method_23285()V"));

		//net/minecraft/client/gui/render/GuiRenderer$Draw (fabric-rendering-v1's DrawAccessor, coerced to by
		//GuiRendererMixin). This one is not a local-layout problem and not a missing method: it is the hierarchy
		//the @Coerce check reads. GuiRendererMixin's handler fixNonQuadIndexing declares its 6th parameter as
		//DrawAccessor and marks it @Coerce, so Mixin's Injector#checkCoerce asks whether the type it actually has
		//to pass - the Draw record class_11228$class_11230, from the receiver of the wrapped
		//RenderPass.setIndexBuffer call - can be coerced to DrawAccessor. canCoerce is a supertype test,
		//  to.hasSuperClass(from, Traversal.ALL, true),
		//and the answer is yes only once the accessor mixin has put DrawAccessor into that record's hierarchy.
		//The game's own copy has it: fabric-rendering-v1.mixins.json lists DrawAccessor before GuiRendererMixin,
		//so Mixin's accessor pass gets there first. The copy OptiFabric serves has no such interface, so the
		//injection is rejected before it runs and the client dies on the first frame:
		//  Mixin apply for mod fabric-rendering-v1 failed ... -> net.minecraft.class_11228:
		//  Cannot @Coerce argument type net.minecraft.class_11228$class_11230 at index 4 to
		//  net.fabricmc.fabric.mixin.client.rendering.DrawAccessor
		//Measured on 1.21.9, 1.21.10 and 1.21.11 (all three use this same intermediary number and the same
		//DrawAccessor). AddInterfaceFix declares the interface on the class and leaves the accessor methods to
		//Mixin's own ACCESSOR pass - implementing them here as well would fail the class with
		//"cannot overwrite method ... because @Overwrite is required by the parent configuration" (see that file).
		registerFix("class_11228$class_11230", new AddInterfaceFix("net/minecraft/class_11228$class_11230"));

		//net/minecraft/client/Camera, for two unrelated mods that inject at the same place: shouldersurfing's
		//CameraMixin and Porting Lib's porting_lib_client_events CameraMixin, both a @ModifyArg on the
		//org.joml.Quaternionf.rotationYXZ call inside setRotation. In the game that call is in the two-argument
		//method_19325(FF)V, which is the overload both refmaps name for "setRotation". OptiFine recompiled Camera
		//from a release whose setRotation has a third, roll argument: its method_19325(FF)V is a four-instruction
		//forwarder to its own public setRotation(FFF)V, where the whole body - and so the rotationYXZ call - now
		//lives. Neither mod's injection point exists in the method it asks for, and both fail the whole class with
		//"expected 1 invocation(s) but 0 succeeded. Scanned 0 target(s)": this is the class that kills a 1.21.1
		//launch with ShoulderSurfing, and it is also the class the Twilight Forest run dies on once class_761 is
		//repaired, so it is not a Twilight Forest problem at all. OptiFine's three-argument method has no other
		//caller left once the forwarder is replaced, and the vanilla body computes exactly what the forwarder
		//computed for a roll of zero, so putting it back cannot change behaviour.
		registerFix("class_4184", new RestoreVanillaMethodsFix(true, "method_19325"));

		//net/minecraft/client/particle/ParticleEngine (Porting Lib's porting_lib_base ParticleEngineMixin, @Inject on
		//addCustomRenderTypes into method_18125(Lnet/minecraft/class_3999;)Ljava/util/Queue;). In the game that is a
		//synthetic lambda the class registers for its own render-type queue; OptiFine's recompile emitted it under
		//javac's lambda name and pointed the bootstrap handle at that, so the name the refmap asks for is gone and
		//the mixin fails the class with "could not find any targets matching ... in net/minecraft/class_702". This is
		//the same rename-back shape as class_329, class_761 and class_638, so it gets the same fixer - registered
		//before the two class_702 entries below because it has to occupy the name first.
		registerFix("class_702", new LambdaMethodRefFix());

		//The second half of what that same mixin needs, and an independent failure: its other handler
		//  @WrapOperation(method = "getLightColor(…)", at = @At(INVOKE, target = "BlockState.getLightEmission()I"))
		//  private static int port_lib$customLight(BlockState, Operation<Integer>, BlockAndTintGetter, BlockState, BlockPos)
		//asks for the getLightEmission call inside class_761.method_23793, and OptiFine's recompiled body does not
		//have it: it reduced the method to a four-instruction forwarder to its own getPackedLightmapCoords, which
		//asks the block state for getLightValue (an OptiFine addition) instead. The game's own method does call
		//method_26213, so the call is re-created in front of OptiFine's body with its own vanilla opcode, exactly as
		//for class_775 above: OptiFine's rendering stays in use, the wrap applies to a value nothing reads, and the
		//class transforms instead of dying with "expected 1 invocation(s) but 0 succeeded. Scanned 0 target(s)".
		registerFix("class_761", new InjectionCallPointFix("class_2680", "method_26213", "()I", "method_23793"));

		//net/minecraft/client/renderer/block/ModelBlockRenderer, the third class in a row where Porting Lib asks for
		//the getLightEmission call inside a method OptiFine's javac rewrote. porting_lib_blocks' ModelBlockRendererMixin
		//is a MixinExtras @ModifyExpressionValue on that call inside method_3374 (tesselateBlock):
		//  @ModifyExpressionValue(method = "tesselateBlock",
		//     at = @At(INVOKE, target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
		//In the game that call is right at the top of the method, at instruction 17, and is the first thing OptiFine's
		//recompile dropped: its version resolves the light through its own LightCacheOF/RenderEnv path and never asks
		//the block state. The mixin therefore scans "0 target(s)" and fails the class with require = 1, which surfaces
		//as "Mixin transformation of net.minecraft.class_778 failed" while class_776 is being constructed and kills the
		//client during "Initializing game", a few seconds after the class_776 repair above lets the run get there.
		//Same repair, same reasons as class_761 above: the call is re-created in front of OptiFine's body, its receiver
		//loaded from the method's own state parameter, and its result discarded, so OptiFine's body and rendering stay
		//exactly as they are and the handler simply becomes inert instead of failing the class.
		registerFix("class_778", new InjectionCallPointFix("class_2680", "method_26213", "()I", "method_3374"));

		//net/minecraft/client/renderer/entity/ItemFrameRenderer (Porting Lib's porting_lib_items
		//client.ItemFrameRendererMixin, @WrapOperation on ItemStack.is(Item) inside method_33434 =
		//getFrameModelResourceLoc). This is the first link in this chain that is not "the call was dropped" but
		//"the call was inlined": the game asks
		//  itemStack.method_31574(Items.field_8204)          // ItemStack.is(Items.FILLED_MAP)
		//and OptiFine's recompile compiled that to
		//  itemStack.method_7909() instanceof class_1806   // getItem() instanceof MapItem
		//(FilledMapItem and MapItem both derive from MapItem, so it is the same question asked of the same
		//object in a different way, and OptiFine's own javac chose the instanceof). The mixin therefore scans
		//"0 target(s)" and fails the class, which aborts the initial resource load inside class_5619's <clinit>:
		//  Caught error loading resourcepacks, removing all selected resourcepacks
		//The client then still reaches the title screen, but with no resource packs at all - measured, see the
		//report - so this link is user visible even though it is not fatal.
		//The repair is the class_761/class_778 one with one addition: the call's argument is a static field, not a
		//parameter of method_33434, so the fixer learned to read that field. Because the re-created call sits in
		//front of OptiFine's instanceof and is the same boolean question, the wrap's value is the one the branch
		//would have computed anyway.
		registerFix("class_915", InjectionCallPointFix.withArgumentField("class_1799", "method_31574",
				"(Lnet/minecraft/class_1792;)Z", "class_1802", "field_8204", "Lnet/minecraft/class_1792;",
				"method_33434"));

		//The String constructor of net/minecraft/client/renderer/ShaderProgram, the fourth and last link of this
		//chain, and a variant of the factory-call collision this fixer was written for rather than a new shape. The
		//game's <init>(class_5912;Ljava/lang/String;Lnet/minecraft/class_293;)V does not build its identifier
		//itself: it calls the (class_5912;class_2960;class_293) constructor through this(...), and that one holds
		//the method_60656 call. OptiFine's recompile inlined the delegated constructor into the String overload, so
		//its String constructor now contains a method_60654 call of its own - the one below at line 158 - and the
		//call in the inlined body is not the call Fabric API's ShaderProgramMixin wraps. Its @WrapOperation on
		//method_60656 inside that constructor then finds no target, and because it is a MixinExtras late injection
		//the failure surfaces one step away from the injection:
		//  Caught error loading resourcepacks, removing all selected resourcepacks
		//  java.lang.NullPointerException: Cannot invoke "String.indexOf(int)" because "stringIn" is null
		//  at class_2960.method_12838 <- class_2960.method_60654 <- FabricShaderProgram.rewriteAsId
		//  at class_5944.wrapOperation$bag000$fabric-rendering-v1$modifyId <- class_5944.<init>
		//Fabric's handler does run on OptiFine's Identifier.of call, receives the null namespace its own
		//rewriteAsId does not expect and throws; the exception escapes the initial resource load and the client
		//drops every resource pack. Porting Lib's render types are what reaches it first (its own core-shader
		//registration), which is why the Twilight Forest run is where it shows up. Same repair, same reason as
		//method_34579 above: point the call at the factory the game's own method uses.
		registerFix("class_5944", new VanillaFactoryCallFix("<init>"));

		//net/minecraft/client/renderer/block/BlockRenderDispatcher, and the shape this line calls a forwarder: the
		//game's method_3353 (renderSingleBlock) holds the whole body and calls class_4696.method_23683 for the
		//block's render type; OptiFine recompiled the class from a release whose renderSingleBlock takes a
		//ModelData and a RenderType, so its method_3353 is a ten-instruction forwarder to its own seven-parameter
		//renderSingleBlock, and the method_23683 call moved in there with the rest of the body. Porting Lib's
		//porting_lib_base client.BlockRenderDispatcherMixin is a MixinExtras @WrapOperation on that call inside
		//the method the refmap names, so it scans "0 target(s)" and fails the class:
		//  Mixin transformation of net.minecraft.class_776 failed
		//  ... port_lib$customRenderType ... expected 1 invocation(s) but 0 succeeded
		//which kills the client while class_776 is being constructed (Reflector's FieldLocatorTypes), before the
		//title screen. OptiFine's own renderSingleBlock has no caller left once the forwarder is replaced - the
		//same situation as class_4184.method_19325 above - and the vanilla body calls only members this class
		//still has (method_3349, method_3367, method_3166 and the same six fields), so restoring the body is
		//exactly the forwarder's behaviour for the arguments it passed. Proved on an edited patch cache before it
		//was registered: with method_3353 restored the class transforms, the run gets several seconds further and
		//the next failure is class_778's, not this one.
		registerFix("class_776", new RestoreVanillaMethodsFix(true, "method_3353"));

		//net/minecraft/client/render/LevelRenderer once more, and the last shape of this family: the call itself.
		//OptiFine's recompile of method_22710 does not make the particle call the game makes - it makes the same
		//call to a method of its own, render(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;FLnet/minecraft/class_4604;)V,
		//with the receiver and the three arguments byte for byte the same and the camera frustum added as a fourth.
		//(The game's method_22710 has two calls to class_702.method_3049(...)V, OptiFine's has three of these, one
		//per shader branch; both sets are mutually exclusive branches, so one call runs per frame either way.)
		//carryon 2.2.6.13's LevelRendererMixin is an @Inject whose refmap target is a call to
		//class_702.method_3049(Lclass_765;Lclass_4184;F)V inside that method, so it scans 0 targets and, with
		//carryon.fabric.mixins.json's defaultRequire = 1, it fails the whole class:
		//  InjectionError: Critical injection failure: Callback method onRenderLevel(...)V in
		//  carryon.fabric.mixins.json:LevelRendererMixin from mod carryon failed injection check, (0/1) succeeded.
		//  Scanned 0 target(s). Using refmap carryon.refmap.json
		//-> "Mixin transformation of net.minecraft.class_761 failed" during OptiFine's own Reflector bootstrap,
		//before the title screen. InjectionCallPointFix is the wrong instrument here: what that point wraps is a
		//*draw*, so a re-created call would run the particle pass a second time, and that fixer puts its call in
		//front of the method body, which would move the mixin's handler to before the frame is drawn at all. The
		//game's call and OptiFine's differ only in the callee, and OptiFine's class_702.method_3049 is still on the
		//class as its own six-instruction forwarder to render(..., null) - so the call is aimed back where the game
		//aimed it and executes exactly once. The frustum is the one argument that has to go, and OptiFine's render
		//uses it for one thing only (per-particle frustum culling); null is what OptiFine's own forwarder passes.
		//Proved on an edited patch cache before it was registered: with method_22710 carrying this redirect the
		//class transforms and the run reaches the title screen with every counter clean - see the report.
		registerFix("class_761", new RestoreVanillaCallFix("class_702", "render",
				"(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;FLnet/minecraft/class_4604;)V", "method_3049",
				"(Lnet/minecraft/class_765;Lnet/minecraft/class_4184;F)V", "method_22710"));

		//sodium itself, and the five rows that only stage sodium with it. OptiFine did not drop either call: it
		//compiled the same arguments to methods of its own, with a return type of its own, so what is missing is a
		//call instruction and not a method. Both are @Redirects whose refmap target is the game's call:
		//  Redirector redirectSampleColor(Lnet/minecraft/class_243;Lnet/minecraft/class_6491$class_4859;)Lnet/minecraft/class_243;
		//    in sodium-common.mixins.json:features.render.world.sky.ClientLevelMixin from mod sodium ... Scanned 0 target(s)
		//  Redirector redirectGetFancyWeather()Z
		//    in sodium-common.mixins.json:features.options.weather.LevelRendererMixin from mod sodium ... Scanned 0 target(s)
		//Both fail the whole class through their configs' defaultRequire = 1, which is why sodium cannot start on
		//this stack once the loader's `breaks` gate is out of the way. The two sites need different repairs and the
		//difference is in their arguments, not in their return types:
		//  - class_638.method_23777 builds its second argument (a class_6491$class_4859 resolver) with an
		//    invokedynamic a few instructions earlier, so it is neither a parameter nor a field and the call cannot
		//    be rebuilt - but OptiFine's replacement takes the very same arguments, so RestoreSiblingCallFix
		//    duplicates them (DUP2) and makes the game's call from the copies, discarding its class_243 result.
		//  - class_310.method_1517 takes no arguments at all, so InjectionCallPointFix's ordinary repair - re-create
		//    the call at the top of the method and discard the result - is exact and costs nothing.
		//Neither changes what OptiFine's own code reads: every value OptiFine computed is still computed.
		registerFix("class_638", new RestoreSiblingCallFix("class_6491", "sampleM",
				"(Lnet/minecraft/class_243;Lnet/minecraft/class_6491$class_4859;)Lnet/optifine/Vec3M;", "method_24895",
				"(Lnet/minecraft/class_243;Lnet/minecraft/class_6491$class_4859;)Lnet/minecraft/class_243;", "method_23777"));
		registerFix("class_761", new InjectionCallPointFix("class_310", "method_1517", "()Z", "method_22714"));

		//The RestoreVanillaMethodsFix batch: OptiFine's recompiler removed or renamed a method a mixin names as its
		//own target, so Mixin reports "could not find any targets matching <owner>.<name><desc>" and fails the whole
		//class through the mixin config's defaultRequire = 1. Every line names the method its own log named, and the
		//byte evidence (ClassCompare: the game's client-intermediary.jar against the served class) is that the game's
		//method is gone and OptiFine's own lambda took its place:
		//  class_757.method_18144(Lclass_1297;)Z          removed, OptiFine's lambda$pick$57(Lclass_1297;)Z added
		//      cut-through: @Inject(isPickable) could not find any targets matching
		//      Lnet/minecraft/class_757;method_18144(Lnet/minecraft/class_1297;)Z
		//  class_836.method_3580(Ljava/util/HashMap;)V    removed, OptiFine's lambda$static$0(Ljava/util/HashMap;)V added
		//      deeperdarker (ShatteredHeadRenderMixin, @Inject addModel) and supplementaries (SkullBlockRendererMixin,
		//      @Inject supp$addExtraTextures) name the same method, so one registration covers both rows
		//  class_1043.method_22793()V                     removed, OptiFine's lambda$new$0()V added
		//      modernfix: safety.DynamicTextureMixin, @Inject checkNullPixels
		//  class_442.method_55814(Lclass_4185)V           removed, OptiFine's lambda$init$1..5(Lclass_4185)V added
		//      no-chat-reports: client.MixinTitleScreen, @Inject onRealmsButtonClicked
		//  class_1921's six candidates                    all six removed, replaced by OptiFine's lambda$static$N
		//      immediatelyfast: core.MixinRenderLayer, @ModifyArg changeTranslucency, whose method list is exactly
		//      method_34834, method_34833, method_36437, method_36436, method_37348 and method_37347. All six exist in
		//      the game's own class_1921 and all six contain the method_24049 call its @At names, so restoring all six
		//      reproduces what the mod does on plain Fabric - where Mixin simply finds six targets - instead of
		//      guessing one of them. Both immediatelyfast rows are this one jar.
		//The restored copies are what OptiFine's own code no longer calls, so they are injection targets and nothing
		//else: the mixin applies and the client starts, and the handler sits in a method OptiFine's body does not
		//reach. That is the trade-off this fixer has always made and it is stated in the report per row.
		registerFix("class_757", new RestoreVanillaMethodsFix("method_18144"));
		registerFix("class_836", new RestoreVanillaMethodsFix("method_3580"));
		registerFix("class_1043", new RestoreVanillaMethodsFix("method_22793"));
		registerFix("class_442", new RestoreVanillaMethodsFix("method_55814"));
		registerFix("class_1921", new RestoreVanillaMethodsFix("method_34834", "method_34833", "method_36437",
				"method_36436", "method_37348", "method_37347"));
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
