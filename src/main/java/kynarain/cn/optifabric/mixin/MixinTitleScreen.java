/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 / Fabric Loader 0.19.x.
 *
 * Changes from upstream:
 *   - MinecraftClient#openScreen was removed in 1.20.6, setScreen is used instead;
 *   - the Fabric screen API integration (compat.fabricscreenapi.Events) and the Text/DrawContext
 *     compatibility shims upstream needed are gone; the 1.20.6 Yarn API is used directly;
 *   - the dead "render(MatrixStack...)" target was dropped, 1.20.6 renders screens into a DrawContext;
 *   - the download prompt runs before the error gate: a jar that is simply older than the newest build
 *     this release knows loads without any error at all, and "nothing in mods/" is an error state, so
 *     the prompt has to be decided first. When it does not take the screen over, the error gate below
 *     still runs, which is what keeps the "OptiFine could not be found" dialog reachable after the
 *     prompt has been dismissed for this session.
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
	private boolean doBackgroundFade;
	@Shadow
	private long backgroundFadeStart;

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

		if (prompt != null && MissingOptifineScreen.shouldPrompt(prompt, OptifineVersion.version)) {
			System.out.println((prompt == MissingOptifineScreen.Mode.MISSING
					? "[OptiFabric] OptiFine is not installed - showing the download screen"
					: "[OptiFabric] The installed OptiFine build is not the one this Minecraft version expects - showing the download screen")
					+ " (installed " + (OptifineVersion.version == null ? "nothing" : OptifineVersion.version)
					+ ", recommended " + expected.file + ")");
			client.setScreen(new MissingOptifineScreen(prompt, expected, OptifineVersion.version));

			// Mode B (an older build) is a one-time recommendation: remember the build as soon as the prompt is
			// shown, so a later launch with the same jar does not nag again. Mode A must appear on every launch
			// and never consults the acknowledgement file, so it is deliberately not written here.
			if (prompt == MissingOptifineScreen.Mode.MISMATCH) {
				MissingOptifineScreen.acknowledge(OptifineVersion.version);
			}

			return;
		}

		// Everything below is the error dialog, and it is only for a real error.
		if (!OptifabricError.hasError()) return;

		String actionButtonText, helpButtonText;
		BooleanConsumer action;
		// Every button here is local: it copies a URL or a path to the game's own clipboard (or the stack trace,
		// as before) and never starts a process. Opening a folder or a page through the operating system is a
		// shell execute - the same shape as the process launch this release had to remove for the platform's
		// review - so the labels say "copy" and what used to be opened is now copied.
		String modsPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();
		String logsPath = new File(FabricLoader.getInstance().getGameDirectory(), "logs").getAbsolutePath();
		String readme = "https://github.com/Kynarain/OptiFabric/blob/main/README.md";
		String issues = "https://github.com/Kynarain/OptiFabric/issues";

		switch (OptifineVersion.jarType) {
		case SOMETHING_ELSE: //Valid jar states, we shouldn't be here
		case OPTIFINE_INSTALLER:
		case OPTIFINE_MOD:
			throw new IllegalStateException("No error to show!");

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
		float fadeColor = doBackgroundFade ? MathHelper.clamp(fadeTime - 1F, 0F, 1F) : 1F;

		int alpha = MathHelper.ceil(fadeColor * 255F) << 24;
		if ((alpha & 0xFC000000) != 0) {
			context.drawTextWithShadow(textRenderer, OptifineVersion.version, 2, height - 20, 0xFFFFFF | alpha);
		}
	}
}
