/*
 * New in this release of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import kynarain.cn.optifabric.mod.OptifineVersion.JarType;

/**
 * Where OptiFine may be installed, and which of those places wins.
 *
 * <p>OptiFine is not always "a jar in this instance's mods folder". A user who plays a modpack instance from a
 * launcher gets three further shapes, and all three are worth finding instead of telling that user their
 * OptiFine does not exist:
 *
 * <ol>
 *   <li>{@link Source#GAME_MODS} - {@code <gameDir>/mods/*.jar}, the shape this mod has always read;</li>
 *   <li>{@link Source#SHARED_MODS} - the {@code mods/} folder of the launcher root, which a launcher that
 *       isolates each instance ({@code <root>/versions/<name>/} as the game directory) does <em>not</em> put
 *       jars from. It is where a user who installed a shared OptiFine jar, before creating instances, still has
 *       it;</li>
 *   <li>{@link Source#LAUNCHER_VERSION} - an OptiFine installed as a launcher <em>version</em>:
 *       {@code <root>/versions/<SomeName>/<SomeName>.json} whose {@code libraries} name
 *       {@code optifine:OptiFine:<v>}, with the jar at
 *       {@code <root>/libraries/optifine/OptiFine/<v>/OptiFine-<v>.jar}. A launcher that only offers
 *       "OptiFine <em>or</em> Fabric" produces exactly this, and it is one of the cases this release exists
 *       for.</li>
 * </ol>
 *
 * <p><b>The ambiguity rule.</b> Several places may hold a valid OptiFine at the same time, so the choice is
 * written down here once instead of being inferred at each call site:
 *
 * <ol>
 *   <li>only jars whose {@code net.optifine.Config.MC_VERSION} equals the release that is running are
 *       candidates. A jar for another release is a different problem with its own message (see
 *       {@link OptifineVersion#parseJarType}), not a candidate, and it is never upgraded into one;</li>
 *   <li>{@link Source#GAME_MODS} wins. It is the copy the user can see next to this mod and the copy the
 *       local-file install box writes, so a version folder or a shared folder is not a reason to ignore it.
 *       Two such jars are still the error they always were and not a choice: one instance runs one OptiFine;</li>
 *   <li>between {@link Source#LAUNCHER_VERSION} and {@link Source#SHARED_MODS} the <em>newer</em> build wins,
 *       ordered by {@link OptifineSupport#order} - the explicit ordering OptiFine's own numbering needs
 *       ({@code HD_U_J10} after {@code HD_U_J9}, which text sorting gets backwards);</li>
 *   <li>two candidates with the <em>same</em> build are not the same choice twice: if they are literally the
 *       same file it is one candidate, and otherwise the search refuses with {@link JarType#DUPLICATED} and
 *       names both. Guessing between two builds of the same number is not something a path can justify, and
 *       silently loading one of them would hide the ambiguity from the user who has to fix it.</li>
 * </ol>
 *
 * <p>A version folder this class cannot read is reported and skipped; one broken folder must not make the
 * other places invisible. Nothing here crawls {@code libraries/}: a launcher-installed OptiFine is found only
 * through the version json that declares it, so an unrelated OptiFine jar lying in {@code libraries/} is not
 * evidence that this instance is supposed to run it.
 */
final class OptifineSearch {
	/** {@code optifine:OptiFine:<version>} - the library coordinates a launcher-installed OptiFine uses. */
	private static final Pattern OPTIFINE_LIBRARY = Pattern.compile("^optifine:OptiFine:(.+)$");

	/** Where an OptiFine jar was found. The order of the constants is the preference order. */
	enum Source {
		/** {@code <gameDir>/mods/*.jar} - the instance's own mods folder. */
		GAME_MODS("mods folder of this instance"),
		/** The launcher root's {@code mods/} folder, which an isolated instance does not use. */
		SHARED_MODS("shared mods folder of the launcher"),
		/** {@code <root>/versions/<name>/}: OptiFine installed as a launcher version. */
		LAUNCHER_VERSION("launcher-installed OptiFine version");

		private final String description;

		Source(String description) {
			this.description = description;
		}

		/** The phrase the prompt shows for this source. */
		String description() {
			return this.description;
		}
	}

	/** One usable OptiFine jar, with where it came from. */
	static final class Candidate {
		final File jar;
		final Source source;
		/** The launcher version's folder name when {@link #source} is {@link Source#LAUNCHER_VERSION}. */
		final String versionName;

		Candidate(File jar, Source source, String versionName) {
			this.jar = jar;
			this.source = source;
			this.versionName = versionName;
		}

		/** What the prompt writes for this candidate: the place, and the launcher version's name when known. */
		String description() {
			if (this.source == Source.LAUNCHER_VERSION && this.versionName != null) {
				return this.source.description() + " \"" + this.versionName + "\"";
			}

			return this.source.description();
		}

		String path() {
			return this.jar.getAbsolutePath();
		}
	}

	private OptifineSearch() {
	}

	/**
	 * Every place this class knows, in preference order, as absolute paths - whether or not anything is there.
	 * Used for the "we looked here" line, so a user can check the folders themselves.
	 */
	static List<String> searchedLocations(Path gameDir, Path launcherRoot) {
		List<String> places = new ArrayList<>();

		places.add(gameDir.resolve("mods").toAbsolutePath().toString());
		if (launcherRoot != null) {
			if (!launcherRoot.equals(gameDir.toAbsolutePath().normalize())) {
				places.add(launcherRoot.resolve("mods").toAbsolutePath().toString());
			}
			places.add(launcherRoot.resolve("versions").toAbsolutePath() + "\\<name>\\<name>.json (a launcher-installed OptiFine)");
			places.add(launcherRoot.resolve("libraries").resolve("optifine").resolve("OptiFine").toAbsolutePath().toString());
		}

		return places;
	}

	/**
	 * The launcher root of the running game, or null when the game directory is not inside one.
	 *
	 * <p>The layout is read, not guessed: a Minecraft launcher keeps {@code versions/} <em>and</em> either
	 * {@code libraries/} or {@code assets/} directly under its root, and the walk stops at the first directory
	 * that has them. An instance game directory of {@code <root>/versions/<name>/} therefore yields
	 * {@code <root>}, and a game directory that is already the root yields itself.
	 */
	static Path launcherRoot(Path gameDir) {
		Path current = gameDir == null ? null : gameDir.toAbsolutePath().normalize();

		for (int up = 0; current != null && up < 4; up++) {
			if (Files.isDirectory(current.resolve("versions")) &&
					(Files.isDirectory(current.resolve("libraries")) || Files.isDirectory(current.resolve("assets")))) {
				return current;
			}

			current = current.getParent();
		}

		return null;
	}

	/**
	 * Every usable OptiFine for one Minecraft release, in preference order.
	 *
	 * @param gameDir the running instance's game directory
	 * @param minecraftVersion {@code FabricLoader.getRawGameVersion()}
	 * @throws OptifineChoiceException when a jar for this release exists but cannot be chosen: several of them in
	 *         the same place, or two places offering the same build. The callers turn this into the error dialog
	 *         that names the files; it deliberately is not "no OptiFine found".
	 */
	static List<Candidate> find(Path gameDir, String minecraftVersion) {
		Path root = launcherRoot(gameDir);
		List<Candidate> candidates = new ArrayList<>();

		// 1. the instance's own mods folder, which stays the only place a duplicate is an error
		List<File> own = optifineJarsInFolder(gameDir.resolve("mods"), minecraftVersion);
		if (own.size() > 1) {
			throw new OptifineChoiceException(JarType.DUPLICATED,
					"Please ensure you only have 1 copy of OptiFine in the mods folder!\nFound: "
							+ own.get(0).getAbsolutePath() + "\n       " + own.get(1).getAbsolutePath(),
					own);
		}
		if (own.size() == 1) {
			candidates.add(new Candidate(own.get(0), Source.GAME_MODS, null));

			return candidates;
		}

		// 2. a shared mods folder: only when the instance is isolated, or that would be the same folder again
		if (root != null && !root.equals(gameDir.toAbsolutePath().normalize())) {
			List<File> shared = optifineJarsInFolder(root.resolve("mods"), minecraftVersion);
			if (shared.size() > 1) {
				throw new OptifineChoiceException(JarType.DUPLICATED,
						"Please ensure you only have 1 copy of OptiFine in the launcher's shared mods folder!\nFound: "
								+ shared.get(0).getAbsolutePath() + "\n       " + shared.get(1).getAbsolutePath(),
						shared);
			}
			if (shared.size() == 1) candidates.add(new Candidate(shared.get(0), Source.SHARED_MODS, null));
		}

		// 3. launcher-installed versions
		if (root != null) candidates.addAll(launcherVersions(root, minecraftVersion));

		// 4. the choice: the newer build first, and an exact tie between two different files is a refusal
		List<Candidate> ordered = new ArrayList<>(candidates);
		ordered.sort(Comparator.comparingInt((Candidate candidate) -> buildRank(candidate, minecraftVersion)).reversed());

		if (ordered.size() > 1) {
			Candidate first = ordered.get(0);
			Candidate second = ordered.get(1);

			if (first.jar.getAbsolutePath().equals(second.jar.getAbsolutePath())) {
				ordered.remove(1);
			} else if (buildRank(first, minecraftVersion) == buildRank(second, minecraftVersion)) {
				throw new OptifineChoiceException(JarType.DUPLICATED,
						"Two OptiFine jars for this Minecraft release carry the same build, so neither can be preferred:\nFound: "
								+ first.path() + "  (" + first.description() + ")\n       "
								+ second.path() + "  (" + second.description() + ")\n"
								+ "Keep one of them (or remove the OptiFine launcher version) and start the game again.",
						List.of(first.jar, second.jar));
			}
		}

		return ordered;
	}

	/**
	 * The build ranking of one candidate for the running release, used to prefer the newer of two places. A
	 * build this line cannot order ranks 0 rather than counting as newest or oldest: {@link OptifineSupport#order}
	 * has already said such a name cannot be placed, and this class must not pretend to compare it.
	 */
	private static int buildRank(Candidate candidate, String minecraftVersion) {
		OptifineSupport.Build expected = OptifineSupport.forMc(minecraftVersion);
		String build = OptifineVersion.readBuildName(candidate.jar);

		if (build == null || expected == null) return 0;

		switch (OptifineSupport.order(build, expected)) {
		case OLDER:
			return -1;
		case NEWER:
			return 1;
		default:
			return 0;
		}
	}

	/**
	 * The OptiFine jars of one {@code mods} folder that declare the running release, plus the first jar that
	 * declared itself OptiFine but could not be used.
	 *
	 * <p>Classification reads every jar in the folder, so an unrelated jar must not leave an error behind for
	 * the game to show later: {@link OptifineVersion#parseJarType} only describes what it saw, and the error is
	 * written here, once, for the file the message is actually about.
	 */
	private static List<File> optifineJarsInFolder(Path modsDir, String minecraftVersion) throws OptifineChoiceException {
		File[] entries = modsDir.toFile().listFiles();

		if (entries == null) return List.of();

		List<File> found = new ArrayList<>();
		OptifineVersion.Parsed unrelatedError = null;
		File unrelatedErrorFile = null;

		for (File file : entries) {
			if (file.isDirectory() || file.isHidden() || !OptifineVersion.hasJarExtension(file.getName())) continue;
			if (file.getName().startsWith(".")) continue;

			OptifineVersion.Parsed parsed;

			try {
				parsed = OptifineVersion.parseJarType(file);
			} catch (IOException e) {
				throw new UncheckedIOException("Unable to read " + file, e);
			}

			if (parsed.type == JarType.OPTIFINE_MOD || parsed.type == JarType.OPTIFINE_INSTALLER) {
				if (minecraftVersion != null && minecraftVersion.equals(parsed.minecraftVersion)) {
					found.add(file);
				} else if (unrelatedErrorFile == null) {
					// OptiFine, but not for this release. Report it the way this mod always has: the user has to
					// know the jar they installed is for another Minecraft version.
					unrelatedError = parsed;
					unrelatedErrorFile = file;
				}

				continue;
			}

			// A jar that declares OptiFine but cannot be read or does not declare a build. A jar that is simply
			// not OptiFine is SOMETHING_ELSE and is not reported at all.
			if (parsed.type.isError() && parsed.type != JarType.SOMETHING_ELSE && unrelatedErrorFile == null) {
				unrelatedError = parsed;
				unrelatedErrorFile = file;
			}
		}

		if (found.isEmpty() && unrelatedErrorFile != null) {
			throw new OptifineChoiceException(unrelatedError.type,
					OptifineVersion.describeJarType(unrelatedErrorFile, unrelatedError), List.of(unrelatedErrorFile));
		}

		found.sort(Comparator.comparing(File::getName));

		return found;
	}

	/**
	 * Every OptiFine installed as a launcher version under {@code <root>/versions/}. Only a version whose own
	 * json declares {@code optifine:OptiFine:<v>} is read, and the jar is looked up at exactly the path that
	 * name spells.
	 */
	private static List<Candidate> launcherVersions(Path root, String minecraftVersion) {
		Path versions = root.resolve("versions");
		List<Candidate> found = new ArrayList<>();

		if (!Files.isDirectory(versions)) return found;

		List<Path> folders;
		try (Stream<Path> stream = Files.list(versions)) {
			folders = stream.filter(Files::isDirectory).sorted().toList();
		} catch (IOException e) {
			System.out.println("[OptiFabric] Cannot read " + versions + ": " + e);

			return found;
		}

		for (Path folder : folders) {
			try {
				Candidate candidate = launcherVersion(root, folder, minecraftVersion);
				if (candidate != null) found.add(candidate);
			} catch (Throwable t) {
				// One unreadable version folder must not hide the others.
				System.out.println("[OptiFabric] Skipping launcher version " + folder.getFileName() + ": " + t);
			}
		}

		return found;
	}

	/** The candidate one {@code versions/<name>/} folder describes, or null when it is not this release's OptiFine. */
	private static Candidate launcherVersion(Path root, Path folder, String minecraftVersion) throws IOException {
		Path json = folder.resolve(folder.getFileName() + ".json");
		if (!Files.isRegularFile(json)) return null;

		String text = new String(Files.readAllBytes(json), StandardCharsets.UTF_8);
		String library = declaredOptifineLibrary(text);

		if (library == null) return null;

		Path folderOfLibrary = root.resolve("libraries").resolve("optifine").resolve("OptiFine").resolve(library);
		Path jar = folderOfLibrary.resolve("OptiFine-" + library + ".jar");

		if (!Files.isRegularFile(jar)) jar = onlyJar(folderOfLibrary);
		if (jar == null) {
			System.out.println("[OptiFabric] Launcher version " + folder.getFileName() + " declares optifine:OptiFine:"
					+ library + ", but no jar is at " + folderOfLibrary);

			return null;
		}

		OptifineVersion.Parsed parsed;

		try {
			parsed = OptifineVersion.parseJarType(jar.toFile());
		} catch (IOException e) {
			throw new IOException("Unable to read " + jar, e);
		}

		if (parsed.type != JarType.OPTIFINE_MOD && parsed.type != JarType.OPTIFINE_INSTALLER) {
			System.out.println("[OptiFabric] Launcher version " + folder.getFileName() + " names OptiFine "
					+ library + ", but " + jar + " is " + parsed.type + " - not using it");

			return null;
		}

		if (minecraftVersion == null || !minecraftVersion.equals(parsed.minecraftVersion)) {
			// A launcher version for another release. Its own MC_VERSION is what decides, never the folder name.
			return null;
		}

		return new Candidate(jar.toFile(), Source.LAUNCHER_VERSION, folder.getFileName().toString());
	}

	/** The one jar directly inside a folder, or null when there is not exactly one. */
	private static Path onlyJar(Path folder) {
		if (!Files.isDirectory(folder)) return null;

		try (Stream<Path> stream = Files.list(folder)) {
			List<Path> jars = stream.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".jar"))
					.toList();

			return jars.size() == 1 ? jars.get(0) : null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * {@code optifine:OptiFine:<version>} as the version json's {@code libraries} name it. The json is read as
	 * text on purpose: a malformed version json must not be able to fail this mod's start-up, and nothing about
	 * this answer needs a parser.
	 */
	private static String declaredOptifineLibrary(String jsonText) {
		Matcher matcher = OPTIFINE_LIBRARY.matcher("");

		for (String token : jsonText.split("\"")) {
			matcher.reset(token.trim());
			if (matcher.matches()) return matcher.group(1);
		}

		return null;
	}

	/** A jar for this release exists but cannot be chosen; the caller reports it in the error dialog. */
	static final class OptifineChoiceException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		final JarType type;
		final List<File> files;

		OptifineChoiceException(JarType type, String message, List<File> files) {
			super(message);
			this.type = type;
			this.files = List.copyOf(files);
		}
	}
}
