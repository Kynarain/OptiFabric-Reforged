/*
 * New in the 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The support table of this line: the newest OptiFine build this OptiFabric release knows about for each
 * Minecraft release. That is the build the prompt recommends and the one its download button fetches.
 *
 * <p>This line builds exactly one jar, for Minecraft {@code 1.20.6}, so the table has exactly one row - and
 * that row is the one the prompt, the download and the version comparison all read. The file name is the one
 * OptiFine's own download listing uses, and the build it names is the newest build OptiFine shipped for
 * 1.20.6: {@code HD_U_I9_pre1}, {@code HD_U_J1_pre17} and {@code HD_U_J1_pre18} are the three builds that
 * release ever got, all three of them previews, so the newest preview is the recommendation and there is no
 * final build to prefer over it. Were OptiFine to ship a final build for 1.20.6, that build would replace
 * {@link #BUILDS} here - the rule is "the newest final build of the release, or the newest preview only when
 * the release has no final build at all".
 *
 * <p>The build a jar is compared against is not the file name but OptiFine's own version string -
 * {@code net.optifine.Config.VERSION}, which {@link OptifineVersion} reads. That string is the file name
 * without {@code .jar} and without the {@code preview_} prefix, so the comparison is exact rather than a
 * guess: the installed 1.20.6 jar in {@code mods/} carries
 * {@code OptiFine_1.20.6_HD_U_J1_pre18}.
 *
 * <p>A row names the <em>newest</em> build of its release, and a row is only about that release. An installed
 * build that is the same as the row, or newer than it (OptiFine shipped it after this OptiFabric release),
 * is acceptable and must not be reported as wrong; {@link #order} is the explicit ordering that question is
 * answered with, and it is deliberately not a string comparison.
 *
 * <h2>Where OptiFine comes from</h2>
 * From OptiFine's own site, downloaded by the user: {@link #OFFICIAL_DOWNLOAD_PAGE} is the only URL this mod
 * names, and it names it as text ({@link OptifineSupport.Build#file} is the file to look for there). This mod
 * ships <b>no</b> third-party OptiFine URL, <b>fetches nothing</b> and has no downloader any more - the
 * platform's review requires that a mod must not download files while the game runs, and the URL template the
 * old download field was pre-filled with went with it.
 *
 * <h2>The two path fields</h2>
 * {@link Build#type} and {@link Build#patch} are stored as OptiFine's own release notes write the build -
 * {@code HD_U_J1} plus a {@code pre18} patch for a preview, an empty patch for a final build. They are what
 * the table's {@link Build#file} is spelled from, and they keep a row readable next to those release notes.
 */
public final class OptifineSupport {
	/** OptiFine's own download listing: the page the prompt tells the user to download from, by hand. */
	public static final String OFFICIAL_DOWNLOAD_PAGE = "https://optifine.net/downloads";

	/** Shown for a release whose OptiFine build is known to break shader packs. */
	public static final String NOTE_SHADERS_CRASH = "shaders-crash";

	private static final List<Build> BUILDS = List.of(
			// One row per Minecraft release: the build the prompt recommends and the download button fetches.
			// 1.20.6 is the only release this jar is built for, and OptiFine shipped three previews for it and
			// no final build, so the newest preview is the row:
			//
			//   preview_OptiFine_1.20.6_HD_U_I9_pre1.jar   06.06.2024
			//   preview_OptiFine_1.20.6_HD_U_J1_pre17.jar  25.09.2024
			//   preview_OptiFine_1.20.6_HD_U_J1_pre18.jar  27.09.2024   <- newest, and this row
			//
			// The older two are still recognised: order() simply compares them as older, which is what makes
			// I9_pre1 and J1_pre17 prompt while J1_pre18 does not. A build newer than this row (a preview 19, or
			// the final J1, were one to appear) is accepted and never mentioned.
			new Build("1.20.6", "HD_U_J1", "pre18", "preview_OptiFine_1.20.6_HD_U_J1_pre18.jar", null));

	private OptifineSupport() {
	}

	/**
	 * Whether a build name names a preview ({@code ..._preN}) rather than a final build. Only a preview is worth
	 * mentioning when a newer build exists: a final build the user already has is a proper release, so an
	 * OptiFine that shipped a newer final afterwards is not a reason to interrupt them.
	 */
	public static boolean isPreview(String buildName) {
		return buildName != null && buildName.contains("_pre");
	}

	/** One supported Minecraft release and the OptiFine build it needs. */
	public static final class Build {
		/** The Minecraft release this entry is for, as {@code FabricLoader.getRawGameVersion()} spells it. */
		public final String mc;
		/** OptiFine type as the notes write it: {@code HD_U_J1}. */
		public final String type;
		/** Prerelease suffix ({@code pre18}) or empty for a final build. */
		public final String patch;
		/** The official file name of the newest build: the download target, and the name it is saved under. */
		public final String file;
		/** A short machine tag for a caveat worth showing, or null; the screen localises it. */
		public final String note;

		Build(String mc, String type, String patch, String file, String note) {
			this.mc = mc;
			this.type = type;
			this.patch = patch;
			this.file = file;
			this.note = note;
		}

		/**
		 * The build as OptiFine reports it in {@code net.optifine.Config.VERSION}, which is what an installed
		 * jar is compared against: the file name without {@code .jar} and without the {@code preview_} prefix.
		 */
		public String buildName() {
			String name = file.endsWith(".jar") ? file.substring(0, file.length() - 4) : file;

			return name.startsWith("preview_") ? name.substring("preview_".length()) : name;
		}

		@Override
		public String toString() {
			return mc + " -> " + file;
		}
	}

	/** The newest build this line knows for one Minecraft release, or null when it does not support it. */
	public static Build forMc(String minecraftVersion) {
		if (minecraftVersion == null) return null;

		for (Build build : BUILDS) {
			if (build.mc.equals(minecraftVersion)) return build;
		}

		return null;
	}

	/** Every entry, in release order. */
	public static List<Build> all() {
		return new ArrayList<>(BUILDS);
	}

	// ------------------------------------------------------------------ build order

	/**
	 * Where an installed build sits relative to the newest build this line knows for the same Minecraft
	 * release - the one question the prompt asks.
	 */
	public enum Order {
		/** Older than the newest known build: the only case the mismatch prompt is for. */
		OLDER,
		/** The newest known build itself; there is nothing to say. */
		SAME,
		/** Newer than this OptiFabric release knows about; acceptable, so it is never reported. */
		NEWER,
		/** Not a build name this line can order; it is treated as acceptable and left alone. */
		UNKNOWN
	}

	/**
	 * Orders an installed build name against the newest build this line knows, for one Minecraft release.
	 *
	 * <p>The rule, in full. It is an explicit ordering and never a string comparison, because OptiFine's own
	 * numbering does not sort as text: {@code HD_U_J10} is newer than {@code HD_U_J9}, yet
	 * {@code "J10".compareTo("J9") < 0}.
	 * <ol>
	 *   <li>the build letter first: {@code J1} … {@code J9} are one type, and a letter after {@code J}
	 *       ({@code K1}, were OptiFine to ship one for this release) is newer than anything in the table;</li>
	 *   <li>then the number after that letter, as a number: {@code HD_U_J1} &lt; {@code J2} &lt; … &lt;
	 *       {@code J9} &lt; {@code J10};</li>
	 *   <li>then, inside one type, the patch: a preview ({@code preN}) is older than the final build
	 *       ({@code HD_U_J1_pre18} &lt; {@code HD_U_J1}), and two previews compare by N as numbers
	 *       ({@code pre3} &lt; {@code pre18}).</li>
	 * </ol>
	 *
	 * <p>The Minecraft version is deliberately not part of this comparison: a caller may only use it for two
	 * builds of the <em>same</em> release. That is exactly what {@link OptifineVersion.JarType#OPTIFINE_MOD}
	 * and {@link OptifineVersion.JarType#OPTIFINE_INSTALLER} mean - the jar declares this release as its
	 * {@code MC_VERSION} - while a jar for another release is not this release's build at all and keeps its
	 * own prompt.
	 *
	 * <p>A name this line cannot read (a renamed or hand-edited {@code Config.VERSION}) comes back as
	 * {@link Order#UNKNOWN}: it cannot be shown to be older, so nothing is said about it and nothing is
	 * downloaded, installed or moved. That is printed once, so "why is there no prompt" has an answer.
	 *
	 * @param installedBuild {@code Config.VERSION} of the jar in {@code mods/}, may be null
	 * @param newest the table row for the running Minecraft release, may be null
	 */
	public static Order order(String installedBuild, Build newest) {
		Stage installed = parse(installedBuild);
		Stage known = newest == null ? null : parse(newest.buildName());

		if (installed == null || known == null) {
			System.out.println("[OptiFabric] Cannot order OptiFine build " + installedBuild + " against "
					+ (newest == null ? "(no table entry)" : newest.buildName()) + " - leaving that jar alone");

			return Order.UNKNOWN;
		}

		int compared = installed.compareTo(known);

		return compared < 0 ? Order.OLDER : compared == 0 ? Order.SAME : Order.NEWER;
	}

	/** The build at the end of a name: {@code OptiFine_1.20.6_HD_U_J1_pre18} → build J1, preview 18. */
	private static final Pattern BUILD_TAIL = Pattern.compile("([A-Z]+)(\\d+)(?:_pre(\\d+))?$");

	/** The build a name ends in, or null when it does not end in one this line can read. */
	private static Stage parse(String buildName) {
		if (buildName == null || buildName.isEmpty()) return null;

		Matcher matcher = BUILD_TAIL.matcher(buildName);

		if (!matcher.find()) return null;

		try {
			return new Stage(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3));
		} catch (NumberFormatException e) {
			// A build number too long for an int is not an OptiFine build this line can place.
			return null;
		}
	}

	/** One parsed build name, comparable by letter, number and patch - see {@link #order}. */
	private static final class Stage implements Comparable<Stage> {
		private final String letter;
		private final int number;
		/** The preview number, or {@link Integer#MAX_VALUE} for a final build: a final beat its previews. */
		private final int patch;

		Stage(String letter, int number, String preview) {
			this.letter = letter;
			this.number = number;
			this.patch = preview == null ? Integer.MAX_VALUE : Integer.parseInt(preview);
		}

		@Override
		public int compareTo(Stage other) {
			int byLetter = letter.compareTo(other.letter);
			if (byLetter != 0) return byLetter;

			int byNumber = Integer.compare(number, other.number);
			if (byNumber != 0) return byNumber;

			return Integer.compare(patch, other.patch);
		}

		@Override
		public String toString() {
			return letter + number + (patch == Integer.MAX_VALUE ? "" : "_pre" + patch);
		}
	}
}
