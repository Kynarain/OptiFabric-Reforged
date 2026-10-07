/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 *
 * Changes from upstream:
 *   - MinecraftClient#openScreen was removed in 1.20.6, setScreen is used instead;
 *   - the Fabric screen API integration (compat.fabricscreenapi.Events) and the Text/DrawContext
 *     compatibility shims upstream needed are gone; the 1.20.6 Yarn API is used directly;
 *   - the dead "render(MatrixStack...)" target was dropped, 1.20.6 renders screens into a DrawContext;
 *   - the two buttons that used to open a folder or a page copy a path or a link to the game's own clipboard
 *     instead: opening one goes through the operating system, which is a shell execute, and this release had
 *     to remove every process launch for the platform's review.
 *
 * This is the 2.1.0 presentation: a problem with the OptiFine jar sets an OptifabricError message and throws a
 * non-fatal failure, and this mixin shows that message on the title screen (with the trace for an internal
 * error) and nothing else. There is no OptiFine screen class, no text box, no install button and no link
 * button - the message itself is the whole UI, exactly as it was in 2.1.0.
 */

package kynarain.cn.optifabric.mixin;

import java.io.File;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

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
	private boolean doBackgroundFade;
	@Shadow
	private long backgroundFadeStart;

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
		// A jar state that is valid in itself (OPTIFINE_MOD, OPTIFINE_INSTALLER, SOMETHING_ELSE) with an error
		// set means OptiFabric could not bring that OptiFine in at all. Asserting "no error to show" for those
		// states threw out of the title screen, which is exactly what must not happen to a user whose OptiFine
		// is newer than this OptiFabric release.
		// Every button here is local: it copies a URL or a path to the game's own clipboard (or the stack trace,
		// as before) and never starts a process.
		String modsPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();
		String logsPath = new File(FabricLoader.getInstance().getGameDirectory(), "logs").getAbsolutePath();
		String readme = "https://github.com/Kynarain/OptiFabric/blob/mc1.21.11/README.md";
		String issues = "https://github.com/Kynarain/OptiFabric/issues";

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
			action = help -> client.keyboard.setClipboard(help ? readme : modsPath);
			break;

		case INTERNAL_ERROR: //Something wrong with OptiFabric itself
		default: {
			String stack = OptifabricError.getErrorLog();
			actionButtonText = stack != null ? "Copy stack-trace" : "Copy logs folder path";
			helpButtonText = "Copy issues link";
			action = help -> client.keyboard.setClipboard(help ? issues : stack != null ? stack : logsPath);
			break;
		}
		}

		client.setScreen(new ConfirmScreen(action, Text.literal("There was an error loading OptiFabric!").formatted(Formatting.RED),
				Text.literal(OptifabricError.getError()), Text.literal(helpButtonText).formatted(Formatting.GREEN), Text.literal(actionButtonText)));
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (OptifabricError.hasError()) return;

		float fadeTime = doBackgroundFade ? (Util.getMeasuringTimeMs() - backgroundFadeStart) / 1000F : 1F;
		float fadeColor = MathHelper.clamp(fadeTime - 1F, 0F, 1F);

		int alpha = MathHelper.ceil(fadeColor * 255F) << 24;
		if ((alpha & 0xFC000000) != 0) {
			context.drawTextWithShadow(textRenderer, OptifineVersion.version, 2, height - 20, 0xFFFFFF | alpha);
		}
	}
}