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
import java.util.Locale;

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
 *
 * <p>{@link #photonGate()} is a different kind of notice on the same dialog: it is about a <em>shader pack</em>
 * in {@code shaderpacks/}, not about the OptiFine jar, so it has its own acknowledgement file and its own rule.
 * It exists because a measurement, not a guess: see the message text for what was measured where.
 */
public final class OptifinePrompt {
	/** Where "an older preview was accepted" is remembered; one line, holding the build that was accepted. */
	public static final String ACK_FILE = "optifabric-mismatch-ack.txt";

	/** Where "the Photon notice was accepted" is remembered; one line, holding pack name and Minecraft version. */
	public static final String PHOTON_ACK_FILE = "optifabric-photon-ack.txt";

	/**
	 * The first Minecraft version whose OptiFine builds cannot render Photon's translucent geometry, which is what
	 * makes the pack misrender there. Below this the pack still logs OptiFine's "Invalid program name" lines - the
	 * same names - but those versions were reported working, so they are not worth interrupting anyone for.
	 */
	private static final String PHOTON_FIRST_AFFECTED_MC = "1.21.6";

	/** What the notice is about. */
	public enum Mode {
		/** No OptiFine at all: the finder reports it on every launch. */
		MISSING,
		/** An older preview is installed: reported once per build. */
		MISMATCH,
		/**
		 * The installed build is the newest one this release knows, and that build carries a caveat worth showing
		 * ({@link OptifineSupport#NOTE_SHADERS_CRASH}: its shader path crashes at startup in OptiFine's own code).
		 * Reported once per build, because there is nothing for the user to update to - the advice is to play without
		 * shaders on that Minecraft version.
		 */
		SHADERS_CRASH
	}

	private OptifinePrompt() {
	}

	/** The notice this instance deserves, or null when it needs none. */
	public static Mode modeFor(JarType jarType, OptifineSupport.Build expected, String installedBuild) {
		if (jarType == JarType.MISSING) return Mode.MISSING;
		if (jarType != JarType.OPTIFINE_MOD && jarType != JarType.OPTIFINE_INSTALLER) return null;
		if (expected == null || installedBuild == null || installedBuild.isEmpty()) return null;

		//The newest build for this Minecraft version is the one there is nothing to update to, so a caveat on it is
		//the only thing left to say. Anything older falls through to the mismatch notice below, which already tells
		//the user to update - once that is done, this becomes the notice they see.
		if (OptifineSupport.NOTE_SHADERS_CRASH.equals(expected.note)
				&& OptifineSupport.order(installedBuild, expected) == OptifineSupport.Order.SAME) {
			return Mode.SHADERS_CRASH;
		}

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

		if (mode == null || !shouldPrompt(mode, OptifineVersion.version)) {
			photonGate();

			return;
		}

		String modsPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods").getAbsolutePath();

		//The newest build for this version is what is installed, and it carries OptiFine's own shader defect. There is
		//nothing to update to, so the only advice is to leave shaders off on this Minecraft version.
		if (mode == Mode.SHADERS_CRASH) {
			System.out.println("[OptiFabric] OptiFine " + OptifineVersion.version + " is the newest build for Minecraft "
					+ expected.mc + ", and its shader path throws during startup; this notice is shown once per build"
					+ " (remembered in config/" + ACK_FILE + ")");
			System.out.println("[OptiFabric] Play Minecraft " + expected.mc + " without a shader pack, or move to a"
					+ " Minecraft version whose newest OptiFine build is a final release ("
					+ OptifineSupport.OFFICIAL_DOWNLOAD_PAGE + ')');
			OptifabricError.setError("The newest OptiFine build for this Minecraft version cannot load a shader pack:"
					+ " selecting one crashes during startup, inside OptiFine's own shader code, before the title"
					+ " screen is reached (%s).\n\nThis mod cannot work around that - its repairs are for what"
					+ " OptiFine's recompile changed, not for OptiFine's own shader path.\n\nPlay Minecraft %s without"
					+ " shaders, or use a Minecraft version whose newest OptiFine build is a final release.",
					expected.file, expected.mc);
			acknowledge(OptifineVersion.version);

			return;
		}

		if (mode != Mode.MISMATCH) return;

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

	/**
	 * The Photon notice: a shader pack in {@code shaderpacks/} whose translucent programs only Iris provides, on a
	 * Minecraft version whose OptiFine does not provide them either. Measured on 1.21.10 with OptiFine
	 * J7_pre11 and {@code photon_v1.2a.zip}: OptiFine logs 30 {@code Invalid program name} lines, skips those
	 * programs, and the deferred pass and the TAA history then read render targets that nothing wrote - the sky and
	 * the water flicker many times a second, and turning TAA on makes the whole frame wrong. Nothing here can fix
	 * that, so the notice only says what can be done instead.
	 */
	private static void photonGate() {
		String gameVersion = FabricLoader.getInstance().getRawGameVersion();

		if (!isPhotonAffectedMc(gameVersion)) return;

		String pack = findPhotonPack();

		if (pack == null) return;

		String key = pack + " @ " + gameVersion;

		if (isAcknowledged(PHOTON_ACK_FILE, key)) return;

		System.out.println("[OptiFabric] Found " + pack + " in shaderpacks/, and Minecraft " + gameVersion
				+ "'s OptiFine build cannot render it; this notice is shown once per pack and version (remembered"
				+ " in config/" + PHOTON_ACK_FILE + ")");
		System.out.println("[OptiFabric] Use a shader pack that supports OptiFine (" + DOCS_PACK_ADVICE + "), or run"
				+ " Photon under Iris, which cannot be combined with this mod");
		OptifabricError.setError("A Photon shader pack is installed (%s), and OptiFine cannot render it correctly on"
				+ " Minecraft %s.\n\nPhoton draws translucent geometry through program names that only Iris provides"
				+ " (for example gbuffers_all_translucent); OptiFine has no such program, so it skips them and the"
				+ " deferred lighting and TAA history read render targets that nothing wrote. Measured on 1.21.10:"
				+ " the sky and the water flicker several times a second, and enabling TAA makes the whole image"
				+ " wrong.\n\nThis is a shader-pack/loader difference, not something this mod can repair. Use a pack"
				+ " that supports OptiFine (%s), or run Photon under Iris - which needs Sodium, and cannot be combined"
				+ " with this mod.", pack, gameVersion, DOCS_PACK_ADVICE);
		acknowledge(PHOTON_ACK_FILE, key);
	}

	/** The advice the READMEs give, in one phrase, so the dialog and the docs cannot drift apart. */
	private static final String DOCS_PACK_ADVICE = "Complementary and similar mainstream packs are tested";

	/** The first {@code photon*.zip} in {@code shaderpacks/}, or null when there is none. */
	private static String findPhotonPack() {
		File dir = new File(FabricLoader.getInstance().getGameDirectory(), "shaderpacks");
		File[] files = dir.listFiles();

		if (files == null) return null;

		for (File file : files) {
			String name = file.getName().toLowerCase(Locale.ROOT);

			if (file.isFile() && name.endsWith(".zip") && name.startsWith("photon")) return file.getName();
		}

		return null;
	}

	/**
	 * Whether this Minecraft version is at or past {@link #PHOTON_FIRST_AFFECTED_MC}, decided by walking
	 * {@link OptifineSupport#all()} - the ten releases in release order - rather than by comparing version strings.
	 * A version this line does not know is never reported, which is the quiet answer.
	 */
	private static boolean isPhotonAffectedMc(String gameVersion) {
		if (gameVersion == null) return false;

		boolean atOrPast = false;

		for (OptifineSupport.Build build : OptifineSupport.all()) {
			if (build.mc.equals(PHOTON_FIRST_AFFECTED_MC)) atOrPast = true;

			if (atOrPast && build.mc.equals(gameVersion)) return true;
		}

		return false;
	}

	/** Remembers that this installed build's notice has been shown, so the next launch is quiet. */
	public static void acknowledge(String installedBuild) {
		acknowledge(ACK_FILE, installedBuild);
	}

	/** Remembers a notice under its own file, so two kinds of notice cannot overwrite each other. */
	public static void acknowledge(String fileName, String key) {
		try {
			Path path = ackFile(fileName);

			Files.createDirectories(path.getParent());
			Files.write(path, ((key == null ? "" : key) + System.lineSeparator())
					.getBytes(StandardCharsets.UTF_8));
		} catch (Throwable t) {
			// A notice that cannot be remembered is a notice that shows again; never fatal.
			System.out.println("[OptiFabric] Could not write " + fileName + ": " + t);
		}
	}

	private static boolean isAcknowledged(String installedBuild) {
		return isAcknowledged(ACK_FILE, installedBuild);
	}

	private static boolean isAcknowledged(String fileName, String key) {
		try {
			Path path = ackFile(fileName);

			if (!Files.isRegularFile(path)) return false;

			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

			return !lines.isEmpty() && lines.get(0).trim().equals(key == null ? "" : key.trim());
		} catch (IOException e) {
			// silent by design: unreadable means "not acknowledged", which shows the notice again - the safe direction
			return false;
		}
	}

	private static Path ackFile(String fileName) {
		return FabricLoader.getInstance().getConfigDir().resolve(fileName);
	}
}
