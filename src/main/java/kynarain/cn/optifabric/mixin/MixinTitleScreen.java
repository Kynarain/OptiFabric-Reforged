/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 26.1.2 / Fabric Loader 0.19.x.
 *
 * 26.x is unobfuscated, so this file names the game by its official names and no mappings sit in between. What
 * changed from the yarn-named 1.21.x counterpart:
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
 *   MinecraftClient#getInstance               -> Minecraft#getInstance
 *
 * The missing/wrong-OptiFine notice is no longer a screen of its own: it is four lines drawn on the title screen
 * (see OptifinePrompt), so there is no path box, no install button and no second screen to leave.
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
import kynarain.cn.optifabric.mod.OptifineSupport;
import kynarain.cn.optifabric.mod.OptifineVersion;

/**
 * Draws the missing/wrong-OptiFine notice on the title screen and prints the OptiFine version in the bottom
 * left corner once it is running.
 *
 * <p>The notice has no screen object on purpose. A screen would have to offer a way to leave it, and every such
 * affordance is either an extra widget this release does not need or a button that copies something; four lines
 * over the title screen leaves the user exactly where they were, with the title screen's own buttons and the
 * window's close button. The wording, the folder it names and the "we do not download it" sentence come from
 * {@link OptifinePrompt}.
 */
@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
	@Shadow
	private boolean fading;
	@Shadow
	private long fadeInStart;

	/** The prompt to draw, or null. The mixin owns it because there is no screen object any more. */
	private OptifinePrompt.Mode prompt;
	/** The build this Minecraft version expects, for the prompt's text. */
	private OptifineSupport.Build expected;

	protected MixinTitleScreen() {
		super(null);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		this.expected = OptifineSupport.forMc(FabricLoader.getInstance().getRawGameVersion());
		OptifinePrompt.Mode wanted = OptifinePrompt.modeFor(OptifineVersion.jarType, this.expected, OptifineVersion.version);
		boolean show = wanted != null && OptifinePrompt.shouldPrompt(wanted, OptifineVersion.version);

		if (show) {
			// The same words go to the log: a log is the one place a run's exact wording can be checked after the
			// fact, and it is what a support request carries.
			System.out.println("[OptiFabric] OptiFine prompt: " + wanted + " (installed "
					+ (OptifineVersion.version == null ? "nothing" : OptifineVersion.version)
					+ ", recommended " + (this.expected == null ? "?" : this.expected.file) + ")");
			System.out.println("[OptiFabric] prompt " + wanted + ": " + OptifinePrompt.heading(wanted, this.expected));
			System.out.println("[OptiFabric] prompt " + wanted + ": " + OptifinePrompt.instruction(this.expected));
			System.out.println("[OptiFabric] prompt " + wanted + ": " + OptifinePrompt.why());
			System.out.println("[OptiFabric] prompt " + wanted + ": " + OptifinePrompt.howToLeave());
			System.out.println("[OptiFabric] prompt " + wanted + ":   official download page "
					+ OptifineSupport.OFFICIAL_DOWNLOAD_PAGE);
			System.out.println("[OptiFabric] prompt " + wanted + ":   OptiFabric does not download OptiFine; get "
					+ (this.expected == null ? "OptiFine" : this.expected.file) + " from that page yourself and put it in "
					+ OptifinePrompt.modsFolder());

			if (wanted == OptifinePrompt.Mode.MISMATCH) {
				OptifinePrompt.acknowledge(OptifineVersion.version);
			}
		}

		this.prompt = show ? wanted : null;

		if (show) return;

		// Everything below is the error dialog, and it is only for a real error.
		if (!OptifabricError.hasError()) return;

		String actionButtonText, helpButtonText;
		BooleanConsumer action;
		// Every button here is local: it copies a URL or a path to the game's own clipboard (or the stack trace, as
		// before) and never starts a process. Opening a folder or a page through the operating system is a shell
		// execute - the same shape as the process launch this release had to remove for the platform's review - so
		// the labels say "copy" and what used to be opened is now copied.
		String modsPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();
		String logsPath = new File(FabricLoader.getInstance().getGameDirectory(), "logs").getAbsolutePath();
		String readme = "https://github.com/Kynarain/OptiFabric/blob/26.x/README.md";
		String issues = "https://github.com/Kynarain/OptiFabric/issues";

		switch (OptifineVersion.jarType) {
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
		if (this.prompt != null) {
			// Four short lines drawn straight onto the title screen: no widgets of their own, nothing to click here,
			// and the title screen's own buttons stay usable underneath - which is how the user leaves.
			int y = 8;

			for (String line : new String[] { OptifinePrompt.heading(this.prompt, this.expected),
					OptifinePrompt.instruction(this.expected), OptifinePrompt.why(), OptifinePrompt.howToLeave() }) {
				context.text(font, line, 6, y, 0xFFFFFF);
				y += 12;
			}

			return;
		}

		if (OptifabricError.hasError()) return;

		float fadeTime = fading ? (Util.getMillis() - fadeInStart) / 1000F : 1F;
		float fadeColor = Mth.clamp(fadeTime - 1F, 0F, 1F);

		int alpha = Mth.ceil(fadeColor * 255F) << 24;
		if ((alpha & 0xFC000000) != 0) {
			context.text(font, OptifineVersion.version, 2, height - 20, 0xFFFFFF | alpha);
		}
	}
}