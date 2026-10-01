/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 26.1.2 / Fabric Loader 0.19.x.
 *
 * 26.x is unobfuscated, so this file names the game by its official names and no mappings sit in between.
 * The 1.21.x line, which has its own branch, carries the same two mixins written against yarn names; this is
 * the 26.x counterpart. What changed:
 *   net.minecraft.client.gui.screen.TitleScreen / Screen / ConfirmScreen -> ...client.gui.screens.*
 *   net.minecraft.client.gui.DrawContext      -> net.minecraft.client.gui.GuiGraphicsExtractor
 *   net.minecraft.text.Text                   -> net.minecraft.network.chat.Component
 *   net.minecraft.util.Formatting             -> net.minecraft.ChatFormatting
 *   net.minecraft.util.math.MathHelper        -> net.minecraft.util.Mth
 *   Screen.render(DrawContext, int, int, float) -> Screen.extractRenderState(GuiGraphicsExtractor, int, int, float)
 *   Screen#textRenderer / #client             -> Screen#font / #minecraft
 *   TitleScreen#doBackgroundFade / #backgroundFadeStart -> TitleScreen#fading / #fadeInStart
 *   Text.literal(x).formatted(F)              -> Component.literal(x).withStyle(F)
 *   Graphics#drawTextWithShadow(font, s, x, y, colour) -> GuiGraphicsExtractor#text(font, s, x, y, colour)
 *   Util.getOperatingSystem().open(x)         -> Util.getPlatform().openUri(String) / .openFile(File)
 *   Util.getMeasuringTimeMs()                 -> Util.getMillis()
 *   MinecraftClient#keyboard                  -> Minecraft#keyboardHandler
 *
 * Changes carried over from the 1.20.6 port: MinecraftClient#openScreen was removed there, setScreen is
 * used instead; the Fabric screen API integration (compat.fabricscreenapi.Events) and the Text/DrawContext
 * compatibility shims upstream needed are gone; the dead "render(MatrixStack...)" target was dropped.
 *
 * 26.2 renamed Minecraft#setScreen to #setScreenAndShow (26.1.2 has both, 26.2 only the new name), so this
 * file calls #setScreenAndShow - which is the one name that compiles on both releases of the 26.x line.
 *
 * The download prompt (MissingOptifineScreen) is the 26.x counterpart of the one the 1.21.x line carries, and
 * it is ported the same way: the jar states it owns - nothing in mods/ at all, and an OptiFine older than the
 * newest build this release knows - are handed to it before the error gate below, because an older build of
 * the *same* Minecraft release loads without any error at all. Everything else in this file is unchanged.
 *
 * NOT yet verified in game: 26.1 replaced "render into a draw context" with "extract a render state" plus a
 * separate renderer, so the version label is now added from extractRenderState instead of render. That is
 * the faithful reading of the new API, but it only holds up once it has been seen on screen.
 */

package kynarain.cn.optifabric.mixin;

import java.io.File;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import net.fabricmc.loader.api.FabricLoader;

import kynarain.cn.optifabric.mod.MissingOptifineScreen;
import kynarain.cn.optifabric.mod.OptifabricError;
import kynarain.cn.optifabric.mod.OptifineSupport;
import kynarain.cn.optifabric.mod.OptifineVersion;

/**
 * Shows why OptiFine could not be loaded instead of silently starting without it, and prints the
 * OptiFine version in the bottom left corner once it is running.
 */
@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
	@Shadow
	private boolean fading;
	@Shadow
	private long fadeInStart;

	protected MixinTitleScreen() {
		super(null);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		// The download prompt owns the jar states it exists for - nothing in mods/ at all, and an OptiFine
		// that is older than the newest build this release knows. It is decided from the jar itself, not from
		// OptifabricError: an older build of the *same* Minecraft release loads without any error at all
		// (OptifineVersion only complains about a jar for another release), and the user still has to be told
		// that a newer one exists. So this has to run before the error gate below.
		OptifineSupport.Build expected = OptifineSupport.forMc(FabricLoader.getInstance().getRawGameVersion());
		MissingOptifineScreen.Mode prompt = MissingOptifineScreen.modeFor(OptifineVersion.jarType, expected, OptifineVersion.version);

		if (prompt != null) {
			if (MissingOptifineScreen.shouldPrompt(prompt, OptifineVersion.version)) {
				System.out.println((prompt == MissingOptifineScreen.Mode.MISSING
						? "[OptiFabric] OptiFine is not installed - showing the download screen"
						: "[OptiFabric] The installed OptiFine build is not the one this Minecraft version expects - showing the download screen")
						+ " (installed " + (OptifineVersion.version == null ? "nothing" : OptifineVersion.version)
						+ ", recommended " + expected.file + ")");
				minecraft.setScreenAndShow(new MissingOptifineScreen(prompt, expected, OptifineVersion.version));

				// Mode B (an older build) is a one-time recommendation: remember the build as soon as the prompt is
				// shown, so a later launch with the same jar does not nag again. Mode A must appear on every launch
				// and never consults the acknowledgement file, so it is deliberately not written here.
				if (prompt == MissingOptifineScreen.Mode.MISMATCH) {
					MissingOptifineScreen.acknowledge(OptifineVersion.version);
				}
			}

			return;
		}

		// Everything below is the error dialog, and it is only for a real error.
		if (!OptifabricError.hasError()) return;

		String actionButtonText, helpButtonText;
		BooleanConsumer action;
		// A jar state that is valid in itself (OPTIFINE_MOD, OPTIFINE_INSTALLER, SOMETHING_ELSE) with an error
		// set means OptiFabric could not bring that OptiFine in at all. A build newer than this release knows
		// about is one such state: it is left alone above while it loads, and when it does not patch, the
		// failure dialog below is what the user needs. Asserting "no error to show" for those states threw out
		// of the title screen - which is exactly what must not happen to a user whose OptiFine is newer than
		// this OptiFabric release. (The 1.21.x line carries the same change; it is part of this port.)
		switch (OptifineVersion.jarType) {
		case MISSING: //Errors relating to the OptiFine jar, link the mods folder
		case CORRUPT_ZIP:
		case INCOMPATIBLE:
		case DUPLICATED:
			actionButtonText = "Open mods folder";
			helpButtonText = "Open help";
			action = help -> {
				if (help) {
					Util.getPlatform().openUri("https://github.com/Kynarain/OptiFabric/blob/mc1.21.11/README.md");
				} else {
					Util.getPlatform().openFile(new File(FabricLoader.getInstance().getGameDirectory(), "mods"));
				}
			};
			break;

		case INTERNAL_ERROR: //Something wrong with OptiFabric itself
		default: {
			String stack = OptifabricError.getErrorLog();
			actionButtonText = stack != null ? "Copy stack-trace" : "Open logs folder";
			helpButtonText = "Open issues";
			action = help -> {
				if (help) {
					Util.getPlatform().openUri("https://github.com/Kynarain/OptiFabric/issues");
				} else if (stack != null) {
					minecraft.keyboardHandler.setClipboard(stack);
				} else {
					Util.getPlatform().openFile(new File(FabricLoader.getInstance().getGameDirectory(), "logs"));
				}
			};
			break;
		}
		}

		minecraft.setScreenAndShow(new ConfirmScreen(action, Component.literal("There was an error loading OptiFabric!").withStyle(ChatFormatting.RED),
				Component.literal(OptifabricError.getError()), Component.literal(helpButtonText).withStyle(ChatFormatting.GREEN), Component.literal(actionButtonText)));
	}

	@Inject(method = "extractRenderState", at = @At("RETURN"))
	private void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (OptifabricError.hasError()) return;

		float fadeTime = fading ? (Util.getMillis() - fadeInStart) / 1000F : 1F;
		float fadeColor = fading ? Mth.clamp(fadeTime - 1F, 0F, 1F) : 1F;

		int alpha = Mth.ceil(fadeColor * 255F) << 24;
		if ((alpha & 0xFC000000) != 0) {
			context.text(font, OptifineVersion.version, 2, height - 20, 0xFFFFFF | alpha);
		}
	}
}
