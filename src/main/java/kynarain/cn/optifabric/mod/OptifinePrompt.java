/*
 * New in this release of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;

import kynarain.cn.optifabric.mod.OptifineVersion.JarType;

/**
 * The whole user-facing story of a missing or outdated OptiFine: a few lines drawn on the title screen that
 * say to download OptiFine from its official page and put the jar in the mods folder, and one short sentence
 * saying that this mod does not download it at runtime.
 *
 * <p>Nothing interactive: there is no download button, no path box, no clipboard button and no second screen.
 * The user leaves it the way they leave the title screen - the ordinary menu, or closing the window. That keeps
 * the store build's prompt at the 2.1.0 level (a plain message) while still naming the folder, and it keeps the
 * platform story visible in one sentence rather than a paragraph.
 *
 * <p>The prompt rules are unchanged: no OptiFine at all is shown on <b>every</b> launch, an installed build
 * that is older than the newest one this release knows and that is a <b>preview</b> is shown <b>once per
 * build</b> (the build is written to {@link #ACK_FILE} the first time), and SAME/NEWER/final never prompts.
 */
public final class OptifinePrompt {
	/** Where "an older preview was accepted" is remembered; one line, holding the build that was accepted. */
	public static final String ACK_FILE = "optifabric-mismatch-ack.txt";

	/** What the prompt is about. */
	public enum Mode {
		/** No OptiFine at all: shown on every launch. */
		MISSING,
		/** An older preview is installed: shown once per build. */
		MISMATCH
	}

	private OptifinePrompt() {
	}

	/**
	 * The prompt this instance deserves, or null when it needs none.
	 *
	 * <p>The comparison is {@link OptifineSupport#order}, not a string compare or a prefix guess: a final build
	 * the user already has is never nagged about, even when a newer final exists.
	 */
	public static Mode modeFor(JarType jarType, OptifineSupport.Build expected, String installedBuild) {
		if (jarType == JarType.MISSING) return Mode.MISSING;
		if (expected == null || installedBuild == null || installedBuild.isEmpty()) return null;

		return OptifineSupport.order(installedBuild, expected) == OptifineSupport.Order.OLDER && OptifineSupport.isPreview(installedBuild)
				? Mode.MISMATCH : null;
	}

	/** Whether the prompt should be shown now. Missing is every launch; an older preview is once per build. */
	public static boolean shouldPrompt(Mode mode, String installedBuild) {
		if (mode == Mode.MISSING) return true;

		return !isAcknowledged(installedBuild);
	}

	/** Remembers that this installed build's prompt has been shown, so the next launch is quiet. */
	public static void acknowledge(String installedBuild) {
		try {
			Path path = ackFile();

			Files.createDirectories(path.getParent());
			Files.write(path, ((installedBuild == null ? "" : installedBuild) + System.lineSeparator())
					.getBytes(StandardCharsets.UTF_8));
		} catch (Throwable t) {
			// A prompt that cannot be remembered is a prompt that shows again; never fatal.
			System.out.println("[OptiFabric] Could not write " + ACK_FILE + ": " + t);
		}
	}

	private static boolean isAcknowledged(String installedBuild) {
		try {
			Path path = ackFile();

			if (!Files.isRegularFile(path)) return false;

			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

			return !lines.isEmpty() && lines.get(0).trim().equals(installedBuild == null ? "" : installedBuild.trim());
		} catch (IOException e) {
			return false;
		}
	}

	private static Path ackFile() {
		return FabricLoader.getInstance().getConfigDir().resolve(ACK_FILE);
	}

	/** The mods folder this instance actually loads from, as an absolute path. */
	public static String modsFolder() {
		return new java.io.File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();
	}

	/** The heading: what is wrong, in one line. */
	public static String heading(Mode mode, OptifineSupport.Build expected) {
		if (mode == Mode.MISSING) {
			return t("OptiFine is not installed", "OptiFine 未安装");
		}

		return t("The installed OptiFine build is not the one this Minecraft version expects (recommended: "
				+ (expected == null ? "?" : expected.file) + ")",
				"已安装的 OptiFine 不是这个 Minecraft 版本期望的构建(建议:" + (expected == null ? "?" : expected.file) + ")");
	}

	/**
	 * The one instruction that matters, naming the folder. The file name to look for stays in it because that is
	 * what the user has to recognise on OptiFine's own page.
	 */
	public static String instruction(OptifineSupport.Build expected) {
		String file = expected == null ? "OptiFine" : expected.file;

		return t("Download " + file + " yourself from " + OptifineSupport.OFFICIAL_DOWNLOAD_PAGE
				+ " and put the jar in the mods folder next to this mod (" + modsFolder() + ").",
				"请自己到官网 " + OptifineSupport.OFFICIAL_DOWNLOAD_PAGE + " 下载 " + file
						+ ",把 jar 放进这个 mod 旁边的 mods 文件夹(" + modsFolder() + ")。");
	}

	/** The compliance sentence, one line: why the mod will not do that step for the user. */
	public static String why() {
		return t("OptiFabric does not download OptiFine at runtime: the platform requires that a mod must not fetch files while the game runs.",
				"OptiFabric 不会在运行时下载 OptiFine:平台要求模组不得在游戏运行时下载文件。");
	}

	/** How to leave the prompt: the title screen's own buttons, or closing the window. */
	public static String howToLeave() {
		return t("Use the title screen behind this message, or close the game.", "使用本提示后面的标题界面按钮,或关闭游戏。");
	}

	/** Chinese when the game's own language is any {@code zh_*} variant, English otherwise. */
	private static boolean isChinese() {
		try {
			// Named by its official name here: the 26.x line is unobfuscated, so this file differs from the
			// 1.21.x / 1.20.6 copies by exactly this one call.
			net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();

			if (client == null || client.options == null) return false;

			// 26.2 calls it Options.languageCode (the 1.21.x line has Options.language).
			String language = client.options.languageCode;

			return language != null && language.toLowerCase(java.util.Locale.ROOT).startsWith("zh");
		} catch (Throwable t) {
			return false;
		}
	}

	private static String t(String english, String chinese) {
		return isChinese() ? chinese : english;
	}
}
