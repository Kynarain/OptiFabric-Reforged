/*
 * New in the 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipFile;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import net.fabricmc.loader.api.FabricLoader;

import kynarain.cn.optifabric.util.ASMUtils;

/**
 * Installs an OptiFine jar the <em>user</em> already downloaded, by copying that local file into {@code mods/}.
 *
 * <p>This is the whole answer to "OptiFabric must not download anything at runtime": the mod names OptiFine's
 * official download page, the user fetches the jar with their own browser, and this class does nothing but
 * <b>local file I/O</b> - open the file, check it is really OptiFine, copy it where the loader looks for it.
 * There is no HTTP client, no URL fetch and no downloader anywhere in this class, and a typed URL is
 * deliberately <em>not</em> fetched: {@link #looksLikeUrl} catches it and the caller tells the user to
 * download the file themselves first.
 *
 * <p>Every installation is validated before anything is written: the file must exist, be readable, be a zip
 * carrying OptiFine's {@code Config.class}, and - when its {@code MC_VERSION} can be read - be a build for the
 * Minecraft release that is running. Nothing is ever overwritten silently: an existing jar of the same name is
 * reported and left alone, and a second OptiFine in {@code mods/} is refused rather than added, because
 * OptiFine cannot be loaded twice.
 */
public final class OptifineLocalInstall {
	/** What one installation attempt did. The caller turns this into a bilingual message. */
	public enum Status {
		/** The jar was copied into {@code mods/}. */
		INSTALLED,
		/** A usable OptiFine jar with that name was already there; nothing was copied. */
		ALREADY_PRESENT,
		/** Nothing was typed into the box. */
		EMPTY,
		/** The text is a URL; it is never fetched. */
		LOOKS_LIKE_URL,
		/** No such file. */
		NOT_FOUND,
		/** There, but not a readable regular file. */
		NOT_A_FILE,
		/** Readable, but not an OptiFine jar. */
		NOT_OPTIFINE,
		/** An OptiFine jar for a different Minecraft release. */
		WRONG_MINECRAFT,
		/** Another OptiFine jar is already in {@code mods/}. */
		OTHER_OPTIFINE_PRESENT,
		/** A file of that name is there and is not a usable OptiFine jar. */
		TARGET_EXISTS,
		/** The copy itself failed. */
		FAILED
	}

	/** One attempt's outcome: the status plus whatever its message needs to name. */
	public static final class Result {
		public final Status status;
		/** The path, file name, version or exception text the message names. */
		public final String detail;
		/** The OptiFine build this Minecraft release expects (the table's file name). */
		public final String expected;

		Result(Status status, String detail, String expected) {
			this.status = status;
			this.detail = detail == null ? "" : detail;
			this.expected = expected == null ? "" : expected;
		}
	}

	private OptifineLocalInstall() {
	}

	/**
	 * Whether the typed text is a URL rather than a path. Such text is never fetched - it is reported back so
	 * the user is told to download the file themselves first.
	 */
	public static boolean looksLikeUrl(String text) {
		if (text == null) return false;

		String value = text.trim().toLowerCase(Locale.ROOT);

		return value.startsWith("http://") || value.startsWith("https://") || value.startsWith("ftp://")
				|| value.startsWith("www.") || value.contains("://");
	}

	/** The mods folder of this instance. */
	private static File modsDir() {
		return new File(FabricLoader.getInstance().getGameDirectory(), "mods");
	}

	/** Installs the jar at the typed path, or says why it will not. Never throws. */
	public static Result install(String typed, OptifineSupport.Build expected) {
		String expectedFileName = expected == null ? "" : expected.file;
		String text = typed == null ? "" : typed.trim();

		// A path pasted out of Explorer's "Copy as path" arrives wrapped in double quotes.
		if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
			text = text.substring(1, text.length() - 1).trim();
		}

		if (text.isEmpty()) return new Result(Status.EMPTY, "", expectedFileName);

		if (looksLikeUrl(text)) {
			System.out.println("[OptiFabric] refusing to fetch " + text + ": this build downloads nothing,"
					+ " install from a local file instead");

			return new Result(Status.LOOKS_LIKE_URL, text, expectedFileName);
		}

		Path source;
		try {
			source = Paths.get(text);
		} catch (InvalidPathException e) {
			return new Result(Status.NOT_FOUND, text, expectedFileName);
		}

		if (!Files.exists(source)) return new Result(Status.NOT_FOUND, text, expectedFileName);

		if (!Files.isRegularFile(source) || !Files.isReadable(source)) {
			return new Result(Status.NOT_A_FILE, text, expectedFileName);
		}

		String fileName = source.getFileName() == null ? text : source.getFileName().toString();

		if (!OptifineJarCheck.isOptifineArchive(source.toFile())) {
			return new Result(Status.NOT_OPTIFINE, fileName, expectedFileName);
		}

		Build build = readVersions(source.toFile());
		String runningMc = FabricLoader.getInstance().getRawGameVersion();

		if (build != null && build.minecraftVersion != null && runningMc != null
				&& !runningMc.equals(build.minecraftVersion)) {
			System.out.println("[OptiFabric] " + source + " is OptiFine " + build.version
					+ " for Minecraft " + build.minecraftVersion + ", but this instance runs " + runningMc
					+ " - refusing it");

			return new Result(Status.WRONG_MINECRAFT, fileName + " (" + build.minecraftVersion + ")",
					runningMc);
		}

		File mods = modsDir();
		File target = new File(mods, fileName);

		List<String> others = otherOptifineJars(mods, target, source.toFile());

		if (!others.isEmpty()) {
			System.out.println("[OptiFabric] " + mods + " already carries another OptiFine (" + others
					+ "), refusing to add " + fileName);

			return new Result(Status.OTHER_OPTIFINE_PRESENT, String.join(", ", others), expectedFileName);
		}

		if (target.isFile()) {
			// Never over a file that is already there: either it is a good OptiFine (nothing to do) or it is
			// something else and the user has to decide what to do with it.
			if (OptifineJarCheck.isOptifineArchive(target)) {
				System.out.println("[OptiFabric] " + target + " is already a usable OptiFine jar, nothing to copy");

				return new Result(Status.ALREADY_PRESENT, fileName, expectedFileName);
			}

			return new Result(Status.TARGET_EXISTS, fileName, expectedFileName);
		}

		Path temporary = null;

		try {
			if (!mods.isDirectory() && !mods.mkdirs()) {
				return new Result(Status.FAILED, "could not create " + mods, expectedFileName);
			}

			// Copied beside the target first and moved into place, so a failure halfway can never leave a
			// truncated jar in mods/ that the loader would then read.
			temporary = Files.createTempFile(mods.toPath(), fileName + ".", ".part");
			Files.copy(source, temporary, StandardCopyOption.REPLACE_EXISTING);
			Files.move(temporary, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			deleteQuietly(temporary);

			System.out.println("[OptiFabric] could not install " + source + ": " + e);

			return new Result(Status.FAILED, String.valueOf(e.getMessage()), expectedFileName);
		}

		System.out.println("[OptiFabric] installed " + target + " (" + target.length() + " bytes) from "
				+ source + (build == null || build.version == null ? "" : ", OptiFine " + build.version));

		return new Result(Status.INSTALLED, fileName, expectedFileName);
	}

	/** The other OptiFine jars already in {@code mods/}, ignoring the target itself and the source file. */
	private static List<String> otherOptifineJars(File mods, File target, File source) {
		List<String> others = new ArrayList<>();
		File[] files = mods.listFiles();

		if (files == null) return others;

		for (File file : files) {
			if (!file.isFile() || !file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) continue;
			if (file.equals(target) || file.equals(source)) continue;
			if (OptifineJarCheck.isOptifineArchive(file)) others.add(file.getName());
		}

		return others;
	}

	/** {@code Config.VERSION} and {@code Config.MC_VERSION} of an OptiFine jar, or null when unreadable. */
	private static Build readVersions(File file) {
		try (ZipFile zip = new ZipFile(file)) {
			ZipEntry entry = OptifineJarCheck.findConfig(zip);

			if (entry == null) return null;

			ClassNode node = ASMUtils.readClass(zip, entry);
			String version = null;
			String minecraftVersion = null;

			for (FieldNode field : node.fields) {
				if (!(field.value instanceof String)) continue;

				if ("VERSION".equals(field.name)) version = (String) field.value;
				if ("MC_VERSION".equals(field.name)) minecraftVersion = (String) field.value;
			}

			return new Build(version, minecraftVersion);
		} catch (ZipError | IOException e) {
			return null;
		}
	}

	private static void deleteQuietly(Path path) {
		if (path == null) return;

		try {
			Files.deleteIfExists(path);
		} catch (IOException ignored) {
			// The reason we are here is the copy itself; a leftover .part file is the lesser problem.
		}
	}

	/** The two strings an OptiFine jar declares about itself. Either may be null. */
	private static final class Build {
		final String version;
		final String minecraftVersion;

		Build(String version, String minecraftVersion) {
			this.version = version;
			this.minecraftVersion = minecraftVersion;
		}
	}
}
