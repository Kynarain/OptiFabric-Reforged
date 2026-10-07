/*
 * New in this 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart, and it exists only on the alternative 1.20.6 build (mod id optifabric_reforged): that is the
 * build C2ME can load next to, and on it c2me-threading-worldgen is the one module that cannot work.
 */
package kynarain.cn.optifabric.mod;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Turns C2ME's threaded world generation off, in C2ME's own config file, so that 1.20.6 + C2ME + OptiFine
 * reaches a world instead of failing on world load.
 *
 * <p>The failure this exists for is not fixable from this side: OptiFine recompiles
 * {@code net/minecraft/class_3898} (ThreadedAnvilChunkStorage) and its own bytes for {@code method_17224}
 * disagree with themselves - the lambda's descriptor, the values its registration site pushes and the slots
 * its body reads cannot be made to agree while also presenting the game's signature. C2ME's
 * {@code c2me-threading-worldgen} module injects into that member, so entering a world dies with
 * {@code InvalidInjectionException ... could not find any targets matching '...method_17224(...)'} (measured;
 * the whole collision surface is in {@code c2me-modules/REPORT.md}, the byte-level reason in
 * {@code collision-1206/REPORT.md}). With {@code [threadedWorldGen] enabled = false} the same instance
 * reaches a world with 0 errors and C2ME's other 19 modules stay active.
 *
 * <p>What this class does is deliberately small: only when the mod {@code c2me} is present, only the one key
 * {@code [threadedWorldGen] enabled} in {@code config/c2me.toml}, keeping every other byte of the file, with a
 * one-time backup, and never over an explicit {@code true} the user wrote - that one is reported and left
 * alone, because it crashes on world load and that is the user's call.
 *
 * <p><b>Why the write alone is not enough.</b> Fabric Loader prepares the mixin configurations of all mods in
 * {@code FabricMixinBootstrap.init} and only then invokes the {@code preLaunch} entrypoints (that order is
 * visible in {@code Knot.init} of Fabric Loader 0.19.5). C2ME reads {@code config/c2me.toml} inside its mixin
 * plugin's {@code onLoad}, which is part of that earlier step - the log order shows it plainly:
 *
 * <pre>
 * [main/INFO]: Initializing com.ishland.c2me.threading.worldgen.mixin
 * [main/INFO]: Config threadedWorldGen.enabled changed from true to true
 * [OptiFabric] Preparing OptiFine OptiFine_1.20.6_HD_U_J1_pre18 ...
 * </pre>
 *
 * So a {@code preLaunch} write is always one launch too late for the launch it runs in. This class therefore
 * writes the file and, when C2ME has already resolved that module to on for this launch, <b>stops this launch
 * and asks the user to start the game again by hand</b>. It does not restart the game: starting the replacement
 * process was a process launch (on Windows through {@code CreateProcessW}), and the platforms this build is
 * submitted to require that a mod must not download files or start processes while the game runs. Three guards
 * keep the behaviour sane: C2ME's resolved value for this launch must not already be false, the file must be
 * re-read from disk and really say {@code false} after the write, and a marker file records that this shim
 * already stopped one launch (a second stop for the same unresolved state never happens).
 */
public final class C2meCompat {
	/** Set this to {@code true} to leave C2ME's config completely alone: {@code -Doptifabric.noC2meCompat=true}. */
	public static final String OPT_OUT_PROPERTY = "optifabric.noC2meCompat";

	private static final String C2ME_MOD_ID = "c2me";
	private static final String CONFIG_PATH = "config/c2me.toml";
	private static final String BACKUP_PATH = "config/c2me.toml.optifabric-backup";
	private static final String RELAUNCH_MARKER_PATH = "config/c2me.toml.optifabric-relaunched";
	private static final String SECTION = "threadedWorldGen";
	private static final String KEY = "enabled";

	/**
	 * C2ME's module entry point. Its {@code enabled} field is what {@code ModuleMixinPlugin.onLoad} reads and
	 * what gates every mixin of the module, so reading it back tells this shim whether the launch that is
	 * already running has the module on - which is what decides whether a restart is worth doing.
	 */
	private static final String C2ME_WORLDGEN_ENTRYPOINT = "com.ishland.c2me.threading.worldgen.ModuleEntryPoint";
	private static final String C2ME_WORLDGEN_ENABLED_FIELD = "enabled";

	private static final Pattern SECTION_LINE = Pattern.compile("^[ \\t]*\\[([^\\[\\]]+)\\][ \\t]*$");
	private static final Pattern KEY_LINE = Pattern.compile("^([ \\t]*)" + KEY + "([ \\t]*=)([ \\t]*)(.*)$");
	private static final Pattern DEFAULT_COMMENT = Pattern.compile("\\(Default:[ \\t]*(true|false)[ \\t]*\\)");

	private static boolean attempted;

	private C2meCompat() {
	}

	/**
	 * Called from the preLaunch entrypoint before anything else. Does nothing at all unless C2ME is installed;
	 * when it is, it either reports why it is not acting, or makes sure {@code [threadedWorldGen] enabled}
	 * reads {@code false}.
	 */
	public static synchronized void ensureC2meCanEnterWorlds() {
		if (attempted) return;
		attempted = true;

		FabricLoader loader = FabricLoader.getInstance();

		// Only ever touch anything when C2ME is present: without it this build must behave exactly like the
		// published 1.20.6 one.
		if (!loader.getModContainer(C2ME_MOD_ID).isPresent()) return;

		if (Boolean.getBoolean(OPT_OUT_PROPERTY)) {
			System.out.println("[OptiFabric] C2ME is installed, but the C2ME compatibility shim is switched off by -D"
					+ OPT_OUT_PROPERTY + "=true, so " + CONFIG_PATH + " is left exactly as it is");
			System.out.println("[OptiFabric]   with the shim off, C2ME's c2me-threading-worldgen stays on and"
					+ " 1.20.6 + C2ME + OptiFine fails on world load (InvalidInjectionException ..."
					+ " class_3898.method_17224); drop that property to let the shim turn it off");
			return;
		}

		try {
			apply(loader.getGameDir());
		} catch (Throwable t) {
			System.out.println("[OptiFabric] C2ME compatibility shim failed, " + CONFIG_PATH
					+ " was left as it is: " + t);
		}
	}

	/**
	 * The whole shim: read C2ME's config, decide, write, report, and restart once when the launch that is
	 * already running cannot be saved by the write. Free of loader API so the file logic can be exercised on
	 * its own.
	 */
	static void apply(Path gameDir) throws IOException {
		Path config = gameDir.resolve(CONFIG_PATH);
		Path backup = gameDir.resolve(BACKUP_PATH);
		Path marker = gameDir.resolve(RELAUNCH_MARKER_PATH);

		boolean existed = Files.isRegularFile(config);
		String text = existed ? read(config) : null;
		Inspection before = inspect(text);

		System.out.println("[OptiFabric] C2ME is installed: making sure c2me-threading-worldgen is off, because"
				+ " 1.20.6 + C2ME + OptiFine cannot apply that module's mixins to OptiFine's class_3898"
				+ " (method_17224)");

		if (before.explicitFalse) {
			System.out.println("[OptiFabric]   " + CONFIG_PATH + " already says [" + SECTION + "] " + KEY
					+ " = false, nothing to do (file left untouched)");
			deleteQuietly(marker);
			return;
		}

		if (before.explicitTrue) {
			System.out.println("[OptiFabric]   " + CONFIG_PATH + " says [" + SECTION + "] " + KEY
					+ " = true, which is an explicit choice - leaving it alone");
			System.out.println("[OptiFabric]   WARNING: with that module on, 1.20.6 + C2ME + OptiFine CRASHES on"
					+ " world load (InvalidInjectionException: no targets matching"
					+ " Lnet/minecraft/class_3898;method_17224(...) in net/minecraft/class_3898)");
			System.out.println("[OptiFabric]   set it to false (or delete the key) to let the shim turn it off;"
					+ " -D" + OPT_OUT_PROPERTY + "=true switches this shim off entirely");
			return;
		}

		// Back the file up once, before the first time this shim ever changes it.
		boolean backedUp = false;
		if (existed && !Files.exists(backup)) {
			Files.createDirectories(backup.getParent());
			Files.copy(config, backup, StandardCopyOption.COPY_ATTRIBUTES);
			backedUp = true;
		}

		Files.createDirectories(config.getParent());
		Files.write(config, before.rewrite(text).getBytes(StandardCharsets.UTF_8));

		// Never act on a write that was not verified: re-read what C2ME will read next launch.
		Inspection after = inspect(read(config));

		if (existed) {
			System.out.println("[OptiFabric]   " + CONFIG_PATH + " [" + SECTION + "] " + KEY + ": "
					+ before.describeValue() + " -> false");
		} else {
			System.out.println("[OptiFabric]   " + CONFIG_PATH + " did not exist, created it with [" + SECTION
					+ "] " + KEY + " = false");
		}

		if (backedUp) {
			System.out.println("[OptiFabric]   original file backed up once to " + BACKUP_PATH
					+ " (that backup is never overwritten; delete it if you want nothing from this shim)");
		}

		System.out.println("[OptiFabric]   every other byte of the file is untouched; only that one key changed");
		System.out.println("[OptiFabric]   what is given up: C2ME's threaded world generation (parallel and async"
				+ " chunk-generation scheduling); its other 19 modules stay active");
		System.out.println("[OptiFabric]   opt out with -D" + OPT_OUT_PROPERTY + "=true (C2ME's module then stays"
				+ " on and world load fails)");

		if (!after.explicitFalse) {
			System.out.println("[OptiFabric]   ERROR: the write did not take effect ([" + SECTION + "] " + KEY
					+ " does not read false), not restarting; fix the file or its permissions yourself");
			return;
		}

		// Can the launch that is already running be saved? C2ME resolved its config before this entrypoint
		// ran, so only a restart can change what this launch does.
		Boolean resolved = readC2meResolvedEnabled();
		if (resolved == null) resolved = before.resolvedDefault;

		if (Boolean.FALSE.equals(resolved)) {
			System.out.println("[OptiFabric]   C2ME had already resolved that module to off for this launch"
					+ " (its default is computed, not fixed: enabled defaults to globalExecutorParallelism >= 3,"
					+ " which depends on CPU count and -Xmx), so no restart is needed - this fix only makes the"
					+ " outcome deterministic instead of luck");
			deleteQuietly(marker);
			return;
		}

		if (Files.exists(marker)) {
			System.out.println("[OptiFabric]   this game was already restarted once by this shim ("
					+ RELAUNCH_MARKER_PATH + " exists) and C2ME still has the module on: not restarting again."
					+ " Start the game once more by hand, or delete " + CONFIG_PATH + " and let C2ME recreate it");
			return;
		}

		System.out.println("[OptiFabric]   C2ME read its config before Fabric's preLaunch entrypoints run"
				+ (resolved == null ? " (and this shim cannot read back what it resolved)" : "")
				+ ", so this launch still has c2me-threading-worldgen on: the fixed value can only take effect"
				+ " on a restart.");
		System.out.println("[OptiFabric]   OptiFabric wrote " + CONFIG_PATH + " ([" + SECTION + "] " + KEY
				+ " = false). That value is read by C2ME before this mod runs, so it takes effect on the next"
				+ " start: please start the game again by hand.");
		System.out.println("[OptiFabric]   OptiFabric 已写入 " + CONFIG_PATH + "([" + SECTION + "] " + KEY
				+ " = false)。C2ME 在本模组之前就已读取该值,所以它要到下次启动才生效:请手动重新启动游戏。");
		System.out.println("[OptiFabric]   this launch is stopped instead, so the configuration that cannot work"
				+ " is never used; OptiFabric does not start any process (no restart, no process launch) and"
				+ " fetches nothing from the network. With [" + SECTION + "] " + KEY + " = false on the next"
				+ " start, 1.20.6 + C2ME + OptiFine enters worlds with 0 errors.");
		Files.write(marker, ("C2meCompat wrote " + CONFIG_PATH + " and stopped the launch once at "
				+ java.time.Instant.now() + " so C2ME would read [" + SECTION + "] " + KEY + " = false\r\n")
				.getBytes(StandardCharsets.UTF_8));

		// Nothing is relaunched: starting the game again is the user's own action, and this exit is the whole
		// of what this shim does about it (exit code 0, no process started, no network touched).
		System.exit(0);
	}

	/** What one c2me.toml says about the one key this shim cares about. */
	static final class Inspection {
		boolean fileExists;
		int sectionLine = -1;
		boolean keyExists;
		/** the value token exactly as written, e.g. {@code "default"}, {@code false}, {@code "true"} */
		String rawValue = "";
		/** {@code (Default: true|false)} from the comment C2ME writes above the key, when it is there */
		Boolean resolvedDefault;
		boolean explicitTrue;
		boolean explicitFalse;

		/** Line-level state needed to rewrite the value in place, or to insert the key. */
		List<String> lines;
		int keyLine = -1;
		int valueTokenStart = -1;
		int valueTokenEnd = -1;
		/** Defaults to this platform's line separator: a file this shim has to create from nothing. */
		String eol = System.lineSeparator();

		boolean sectionExists() {
			return sectionLine >= 0;
		}

		String describeValue() {
			return rawValue.isEmpty() ? "(no key)" : rawValue;
		}

		String rewrite(String text) {
			String cr = eol.equals("\r\n") ? "\r" : "";
			List<String> out = new ArrayList<>(lines);

			if (keyExists) {
				String line = out.get(keyLine);
				out.set(keyLine, line.substring(0, valueTokenStart) + "false" + line.substring(valueTokenEnd));
			} else if (sectionExists()) {
				// The section is there but the key is not: add it as the first key of that section.
				out.add(sectionLine + 1, "\t" + KEY + " = false" + cr);
			} else if (fileExists) {
				// No such section at all: append one, after a single blank line.
				while (!out.isEmpty() && out.get(out.size() - 1).trim().isEmpty()) out.remove(out.size() - 1);
				out.add(cr);
				out.add("[" + SECTION + "]" + cr);
				out.add("\t" + KEY + " = false" + cr);
				out.add(cr);
			} else {
				return "version = 3" + eol + eol + "[" + SECTION + "]" + eol + "\t" + KEY + " = false" + eol;
			}

			StringBuilder joined = new StringBuilder();
			for (int i = 0; i < out.size(); i++) {
				joined.append(out.get(i));
				if (i + 1 < out.size()) joined.append('\n');
			}
			return joined.toString();
		}
	}

	/**
	 * Reads one c2me.toml. Pure text handling: the section headers in C2ME's file are sometimes tab-indented
	 * and {@code enabled} also exists in several other sections, so the section has to be tracked by name.
	 */
	static Inspection inspect(String text) {
		Inspection inspection = new Inspection();
		inspection.fileExists = text != null;
		inspection.lines = new ArrayList<>();

		if (text == null) return inspection;

		if (text.contains("\r\n")) inspection.eol = "\r\n";
		String[] split = text.split("\n", -1);
		for (String line : split) inspection.lines.add(line);

		String section = null;
		for (int i = 0; i < inspection.lines.size(); i++) {
			String line = inspection.lines.get(i);
			String body = line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;

			Matcher header = SECTION_LINE.matcher(body);
			if (header.matches()) {
				section = header.group(1).trim();
				if (section.equals(SECTION)) inspection.sectionLine = i;
				continue;
			}

			if (!SECTION.equals(section)) continue;

			Matcher key = KEY_LINE.matcher(body);
			if (key.matches()) {
				inspection.keyExists = true;
				inspection.keyLine = i;
				String rest = key.group(4);
				String token = valueToken(rest);
				inspection.rawValue = token;
				inspection.valueTokenStart = key.start(4) + rest.indexOf(token);
				inspection.valueTokenEnd = inspection.valueTokenStart + token.length();
				inspection.explicitTrue = unquote(token).equalsIgnoreCase("true");
				inspection.explicitFalse = unquote(token).equalsIgnoreCase("false");
				continue;
			}

			// The comment directly above the key carries the default C2ME actually resolved on this machine.
			if (!inspection.keyExists) {
				Matcher comment = DEFAULT_COMMENT.matcher(body);
				if (comment.find()) inspection.resolvedDefault = Boolean.valueOf(comment.group(1));
			}
		}

		if (!inspection.sectionExists()) inspection.keyExists = false;
		return inspection;
	}

	/** The value token of a TOML line, with a quoted string kept whole and a trailing comment dropped. */
	private static String valueToken(String rest) {
		String value = rest.trim();
		if (value.isEmpty()) return value;

		char first = value.charAt(0);
		if (first == '"' || first == '\'') {
			int end = value.indexOf(first, 1);
			if (end > 0) return value.substring(0, end + 1);
		}

		int comment = value.indexOf('#');
		if (comment >= 0) value = value.substring(0, comment).trim();
		return value;
	}

	private static String unquote(String token) {
		if (token.length() >= 2) {
			char first = token.charAt(0);
			char last = token.charAt(token.length() - 1);
			if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
				return token.substring(1, token.length() - 1);
			}
		}
		return token;
	}

	/**
	 * What C2ME resolved for the launch that is already running: its module entry point's own {@code enabled}
	 * field, read back if that class is on the classpath. Returns {@code null} when it cannot be read (another
	 * C2ME version, a different shape), which the caller treats as "assume the module may be on".
	 */
	private static Boolean readC2meResolvedEnabled() {
		try {
			Class<?> entryPoint = Class.forName(C2ME_WORLDGEN_ENTRYPOINT, false, C2meCompat.class.getClassLoader());
			Field field = entryPoint.getField(C2ME_WORLDGEN_ENABLED_FIELD);
			return field.getBoolean(null);
		} catch (Throwable t) {
			// silent by design: an unreadable field means the setting could not be resolved, which the caller treats as "leave the config alone"
			return null;
		}
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static void deleteQuietly(Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			// A stale marker is only ever a reason not to restart automatically; never fatal.
		}
	}
}
