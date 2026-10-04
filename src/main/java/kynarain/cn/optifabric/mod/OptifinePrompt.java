/*
 * New in this release of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;

import kynarain.cn.optifabric.mod.OptifineVersion.JarType;

/**
 * The rule that decides whether an installed OptiFine is worth a notice, and the acknowledgement file that keeps
 * the "an older preview is installed" notice to once per build.
 *
 * <p>There is no screen here, and no message shape of its own: the notice is an {@link OptifabricError} message,
 * which is 2.1.0's presentation - {@code MixinTitleScreen} turns it into the one dialog this mod has. A missing
 * OptiFine already sets that error inside {@link OptifineVersion#findOptifineJar()}, so its dialog appears on
 * every launch exactly as it did in 2.1.0; {@link #gate()} adds the second case in front of that same dialog.
 *
 * <p>The comparison is {@link OptifineSupport#order}, not a string compare and not a prefix guess. An installed
 * build that is the newest this release knows, a newer one, or any final build is never reported; only an older
 * <em>preview</em> is, because a final build the user already has is a proper release. "Once" is the build name
 * written to {@link #ACK_FILE} under {@code config/}: the next launch with the same jar is quiet, and a launch
 * with a different older preview is not.
 */
public final class OptifinePrompt {
	/** Where "an older preview was accepted" is remembered; one line, holding the build that was accepted. */
	public static final String ACK_FILE = "optifabric-mismatch-ack.txt";

	/** What the notice is about. */
	public enum Mode {
		/** No OptiFine at all: the finder reports it on every launch. */
		MISSING,
		/** An older preview is installed: reported once per build. */
		MISMATCH
	}

	private OptifinePrompt() {
	}

	/** The notice this instance deserves, or null when it needs none. */
	public static Mode modeFor(JarType jarType, OptifineSupport.Build expected, String installedBuild) {
		if (jarType == JarType.MISSING) return Mode.MISSING;
		if (jarType != JarType.OPTIFINE_MOD && jarType != JarType.OPTIFINE_INSTALLER) return null;
		if (expected == null || installedBuild == null || installedBuild.isEmpty()) return null;

		return OptifineSupport.order(installedBuild, expected) == OptifineSupport.Order.OLDER
				&& OptifineSupport.isPreview(installedBuild) ? Mode.MISMATCH : null;
	}

	/** Whether the notice should be shown now. Missing is every launch; an older preview is once per build. */
	public static boolean shouldPrompt(Mode mode, String installedBuild) {
		if (mode == null) return false;
		if (mode == Mode.MISSING) return true;

		return !isAcknowledged(installedBuild);
	}

	/**
	 * The invisible gate in front of the title screen's dialog, called from the title-screen path before the
	 * dialog decides whether it has anything to show. It adds a message only for an older preview, and only when
	 * that build has not been acknowledged yet. Nothing else is touched: a real failure has already set its own
	 * message, and SAME / NEWER / a final build leave this method without a word.
	 */
	public static void gate() {
		if (OptifabricError.hasError()) return;

		OptifineSupport.Build expected = OptifineSupport.forMc(FabricLoader.getInstance().getRawGameVersion());
		Mode mode = modeFor(OptifineVersion.jarType, expected, OptifineVersion.version);

		if (mode != Mode.MISMATCH || !shouldPrompt(mode, OptifineVersion.version)) return;

		String modsPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();
		System.out.println("[OptiFabric] OptiFine " + OptifineVersion.version + " is older than " + expected.file
				+ ", the newest build for Minecraft " + expected.mc + "; this notice is shown once per build"
				+ " (remembered in config/" + ACK_FILE + ")");
		System.out.println("[OptiFabric] Download " + expected.file + " from " + OptifineSupport.OFFICIAL_DOWNLOAD_PAGE
				+ " and put the jar in " + modsPath + ", next to this mod");
		OptifabricError.setError("The installed OptiFine build is older than the newest build for this Minecraft"
				+ " version:\n%s\n\nThe newest build for Minecraft %s is %s. Download it and place it in that"
				+ " folder next to this mod.", OptifineVersion.version, expected.mc, expected.file);
		acknowledge(OptifineVersion.version);
	}

	/** Remembers that this installed build's notice has been shown, so the next launch is quiet. */
	public static void acknowledge(String installedBuild) {
		try {
			Path path = ackFile();

			Files.createDirectories(path.getParent());
			Files.write(path, ((installedBuild == null ? "" : installedBuild) + System.lineSeparator())
					.getBytes(StandardCharsets.UTF_8));
		} catch (Throwable t) {
			// A notice that cannot be remembered is a notice that shows again; never fatal.
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
}