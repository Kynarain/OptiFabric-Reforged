/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 26.1.2 / Fabric Loader 0.19.x.
 *
 * 26.x is unobfuscated, so this file names the game by its official names and no mappings sit in between. What
 * changed from the yarn-named 1.21.x / 1.20.6 counterpart:
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
 *   Screen#setScreen                          -> Minecraft#setScreenAndShow (26.2 only has the new name)
 *
 * This is the 2.1.0 presentation: a problem with the OptiFine jar sets an OptifabricError message and throws a
 * non-fatal failure, and this mixin shows that message on the title screen (with the trace for an internal
 * error) and nothing else. There is no OptiFine screen class, no text box, no install button and no link
 * button - the message itself is the whole UI, exactly as it was in 2.1.0. The two buttons that used to open a
 * folder or a page copy a path or a link to the game's own clipboard instead, because opening one goes through
 * the operating system, which is a shell execute.
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

import kynarain.cn.optifabric.mod.OptifabricError;
import kynarain.cn.optifabric.mod.OptifinePrompt;
import kynarain.cn.optifabric.mod.OptifineVersion;

/**
 * Shows why OptiFine could not be loaded instead of silently starting without it, and prints the OptiFine
 * version in the bottom left corner once it is running.
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
		// 2.1.0's rule, restored in front of its own dialog: a missing jar already set the error in the finder,
		// and an installed-but-older preview reaches the same dialog through this gate, once per build (the build
		// is remembered in config/optifabric-mismatch-ack.txt). Nothing here is a screen of its own, and
		// SAME / NEWER / any final build never get past it.
		OptifinePrompt.gate();

		if (!OptifabricError.hasError()) return;

		String actionButtonText, helpButtonText;
		BooleanConsumer action;
		String modsPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();
		String logsPath = new File(FabricLoader.getInstance().getGameDirectory(), "logs").getAbsolutePath();
		String readme = "https://github.com/Kynarain/OptiFabric-Reforged/blob/26.x/README.md";
		String issues = "https://github.com/Kynarain/OptiFabric-Reforged/issues";

		//A jar whose class bytes cannot be parsed at all leaves jarType null, and OptifabricRuntime has already
		//turned that into an error with a stack behind it. Switching on null threw a NullPointerException out of
		//the title screen - in the one path that exists to explain a failure to the user.
		OptifineVersion.JarType jarType = OptifineVersion.jarType;

		switch (jarType != null ? jarType : OptifineVersion.JarType.INTERNAL_ERROR) {
		case MISSING: //Errors relating to the OptiFine jar, link the mods folder
		case CORRUPT_ZIP:
		case INCOMPATIBLE:
		case DUPLICATED:
			actionButtonText = "Copy mods folder path";
			helpButtonText = "Copy help link";
			action = help -> minecraft.keyboardHandler.setClipboard(help ? readme : modsPath);
			break;

		case INTERNAL_ERROR: //Something wrong with OptiFabric itself
		default: {
			String stack = OptifabricError.getErrorLog();
			actionButtonText = stack != null ? "Copy stack-trace" : "Copy logs folder path";
			helpButtonText = "Copy issues link";
			action = help -> minecraft.keyboardHandler.setClipboard(help ? issues : stack != null ? stack : logsPath);
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
		float fadeColor = Mth.clamp(fadeTime - 1F, 0F, 1F);

		int alpha = Mth.ceil(fadeColor * 255F) << 24;
		if ((alpha & 0xFC000000) != 0) {
			context.text(font, OptifineVersion.version, 2, height - 20, 0xFFFFFF | alpha);
		}
	}
}