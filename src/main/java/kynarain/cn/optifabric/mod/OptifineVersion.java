/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 / Fabric Loader 0.19.x.
 *
 * Changes from upstream:
 *   - the current Minecraft version comes from Fabric Loader instead of the launcher's version.json resource;
 *   - Apache Commons IO/FilenameUtils usage replaced with plain JDK code.
 */
package kynarain.cn.optifabric.mod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipError;
import java.util.zip.ZipException;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import net.fabricmc.loader.api.FabricLoader;

import kynarain.cn.optifabric.util.ASMUtils;
import kynarain.cn.optifabric.util.ZipUtils;

/**
 * Locates the OptiFine jar this instance is supposed to load and reads its declared versions.
 *
 * <p>The places searched, and which of them wins when several hold a valid OptiFine, are
 * {@link OptifineSearch}'s subject; this class only reads a jar and remembers what it said. The three shapes
 * are: this instance's {@code mods/} folder, the launcher root's shared {@code mods/} folder, and OptiFine
 * installed as a launcher <em>version</em> ({@code versions/<name>/} plus its
 * {@code libraries/optifine/**} jar).
 */
public class OptifineVersion {
	public static String version;
	public static String minecraftVersion;
	public static JarType jarType;
	/** The jar {@link #findOptifineJar()} chose, for the prompt's "we found it here" line; null before that. */
	public static File optifineJar;
	/** Where that jar was found, as a phrase; null when nothing was found or before the search ran. */
	public static String optifineSource;
	/** The absolute paths that were searched, whether or not anything was there. */
	public static List<String> searchedLocations = List.of();

	public static File findOptifineJar() throws IOException {
		Path gameDir = FabricLoader.getInstance().getGameDirectory().toPath();
		Path launcherRoot = OptifineSearch.launcherRoot(gameDir);
		File modsDir = new File(gameDir.toFile(), "mods");

		optifineJar = null;
		optifineSource = null;
		searchedLocations = OptifineSearch.searchedLocations(gameDir, launcherRoot);

		List<OptifineSearch.Candidate> candidates;

		try {
			candidates = OptifineSearch.find(gameDir, FabricLoader.getInstance().getRawGameVersion());
		} catch (OptifineSearch.OptifineChoiceException e) {
			jarType = e.type;
			OptifabricError.setError("%s", e.getMessage());
			throw new FileAlreadyExistsException(e.getMessage());
		} catch (java.io.UncheckedIOException e) {
			jarType = JarType.CORRUPT_ZIP;
			OptifabricError.setError("%s", e.getMessage());
			throw new IOException(e.getMessage(), e.getCause());
		}

		if (!candidates.isEmpty()) {
			OptifineSearch.Candidate chosen = candidates.get(0);
			optifineJar = chosen.jar;
			optifineSource = chosen.description();

			JarType type = reportJarType(chosen.jar);
			jarType = type;

			if (type.isError()) {
				throw new RuntimeException("An error occurred when trying to find the optifine jar: " + type.name());
			}

			System.out.println("[OptiFabric] Found OptiFine " + version + " in the " + optifineSource
					+ ": " + chosen.path());
			if (candidates.size() > 1) {
				System.out.println("[OptiFabric]   " + (candidates.size() - 1)
						+ " other usable OptiFine jar(s) are installed as well; the newest build wins (see OptifineSearch)");
				for (OptifineSearch.Candidate other : candidates.subList(1, candidates.size())) {
					System.out.println("[OptiFabric]     also: " + other.path() + "  (" + other.description() + ")");
				}
			}

			return optifineJar;
		}

		jarType = JarType.MISSING;
		OptifabricError.setError("OptiFabric could not find the OptiFine jar for Minecraft %s. Looked in:\n%s\n\n"
				+ "Download OptiFine for Minecraft %s and put the jar in the mods folder next to this mod, or paste its"
				+ " path into the Install from file box on the screen that follows.",
				FabricLoader.getInstance().getRawGameVersion(), String.join("\n", searchedLocations),
				FabricLoader.getInstance().getRawGameVersion());
		throw new FileNotFoundException("Could not find optifine jar");
	}

	/**
	 * The build name inside one jar, as {@code net.optifine.Config.VERSION} declares it, or null when the jar
	 * cannot be read. Only used to compare two installed places by build; it reads the jar rather than the
	 * fields below, so a caller can ask about a jar other than the chosen one.
	 */
	static String readBuildName(File file) {
		try {
			ClassNode classNode = readConfig(file);

			if (classNode == null) return null;

			for (FieldNode fieldNode : classNode.fields) {
				if ("VERSION".equals(fieldNode.name)) return (String) fieldNode.value;
			}
		} catch (IOException e) {
			// Unreadable: not orderable, which the caller treats as "cannot be compared".
		}

		return null;
	}

	/** Reads {@code net/optifine/Config.class} out of a jar, without loading any class from it. */
	private static ClassNode readConfig(File file) throws IOException {
		try (JarFile jarFile = new JarFile(file)) {
			JarEntry jarEntry = jarFile.getJarEntry("net/optifine/Config.class"); //F1 (1.14.2) - G9 location

			if (jarEntry == null) {
				jarEntry = jarFile.getJarEntry("notch/net/optifine/Config.class"); //H1 (1.17.1) location
			}

			if (jarEntry == null) return null;

			return ASMUtils.readClass(jarFile, jarEntry);
		}
	}

	public static boolean hasJarExtension(String name) {
		int dot = name.lastIndexOf('.');

		return dot >= 0 && name.regionMatches(true, dot + 1, "jar", 0, 3) && dot == name.length() - 4;
	}

	/** What one jar turned out to be, with no side effect on this class and no error written anywhere. */
	static final class Parsed {
		final JarType type;
		final String version;
		final String minecraftVersion;

		Parsed(JarType type, String version, String minecraftVersion) {
			this.type = type;
			this.version = version;
			this.minecraftVersion = minecraftVersion;
		}
	}

	/**
	 * Classifies one jar with no side effects, so {@link OptifineSearch} can look at every jar in a folder
	 * without leaving an error behind for a jar it does not use. {@link #reportJarType} is the reporting half.
	 */
	static Parsed parseJarType(File file) throws IOException {
		ClassNode classNode;

		try (JarFile jarFile = new JarFile(file)) {
			JarEntry jarEntry = jarFile.getJarEntry("net/optifine/Config.class"); //F1 (1.14.2) - G9 location

			if (jarEntry == null) {
				jarEntry = jarFile.getJarEntry("notch/net/optifine/Config.class"); //H1 (1.17.1) location
			}

			if (jarEntry == null) return new Parsed(JarType.SOMETHING_ELSE, null, null);

			classNode = ASMUtils.readClass(jarFile, jarEntry);
		} catch (ZipException | ZipError e) {
			return new Parsed(JarType.CORRUPT_ZIP, null, null);
		}

		String foundVersion = null;
		String foundMcVersion = null;

		for (FieldNode fieldNode : classNode.fields) {
			if ("VERSION".equals(fieldNode.name)) {
				foundVersion = (String) fieldNode.value;
			}
			if ("MC_VERSION".equals(fieldNode.name)) {
				foundMcVersion = (String) fieldNode.value;
			}
		}

		if (foundVersion == null || foundVersion.isEmpty() || foundMcVersion == null || foundMcVersion.isEmpty()) {
			return new Parsed(JarType.INCOMPATIBLE, foundVersion, foundMcVersion);
		}

		if (!FabricLoader.getInstance().getRawGameVersion().equals(foundMcVersion)) {
			return new Parsed(JarType.INCOMPATIBLE, foundVersion, foundMcVersion);
		}

		boolean[] isInstaller = new boolean[1];
		ZipUtils.iterateContents(file, (zip, zipEntry) -> {
			if (zipEntry.getName().startsWith("patch/")) {
				isInstaller[0] = true;
				return false;
			} else {
				return true;
			}
		});

		return new Parsed(isInstaller[0] ? JarType.OPTIFINE_INSTALLER : JarType.OPTIFINE_MOD, foundVersion, foundMcVersion);
	}

	/**
	 * Classifies one jar <em>and</em> reports it: the fields the rest of the mod reads are set, and a jar that
	 * cannot be used leaves the message its state deserves. This is the entry point for the jar that was
	 * actually chosen, so nothing here is ever written for a jar that is not this instance's OptiFine.
	 */
	static JarType reportJarType(File file) throws IOException {
		Parsed parsed = parseJarType(file);
		jarType = parsed.type;
		if (parsed.version != null) version = parsed.version;
		if (parsed.minecraftVersion != null) minecraftVersion = parsed.minecraftVersion;

		String message = describeJarType(file, parsed);

		if (message != null) OptifabricError.setError("%s", message);

		return parsed.type;
	}

	/**
	 * The sentence one classification deserves, or null for a jar that is fine: an OptiFine mod jar, an
	 * installer, or a jar that has nothing to do with OptiFine at all.
	 */
	static String describeJarType(File file, Parsed parsed) {
		switch (parsed.type) {
		case OPTIFINE_MOD:
		case OPTIFINE_INSTALLER:
		case SOMETHING_ELSE:
			return null;

		case CORRUPT_ZIP:
			return "The jar at " + file + " is corrupt";

		default:
			if (parsed.version == null || parsed.version.isEmpty() || parsed.minecraftVersion == null || parsed.minecraftVersion.isEmpty()) {
				return "Unable to find OptiFine version from OptiFine jar at " + file;
			}

			return String.format("This version of OptiFine from %s is not compatible with the current minecraft version"
					+ "\n\nOptifine requires %s you are running %s", file, parsed.minecraftVersion,
					FabricLoader.getInstance().getRawGameVersion());
		}
	}

	public enum JarType {
		MISSING(true),
		OPTIFINE_MOD(false),
		OPTIFINE_INSTALLER(false),
		INCOMPATIBLE(true),
		CORRUPT_ZIP(true),
		DUPLICATED(true),
		INTERNAL_ERROR(true),
		SOMETHING_ELSE(false);

		private final boolean error;

		JarType(boolean error) {
			this.error = error;
		}

		public boolean isError() {
			return error;
		}
	}
}
