/*
 * New in the 1.21.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
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
 * <p>Nothing here is invented. The file names are the ones the release notes name
 * ({@code release/notes/mc<MC>.md}, the line that reads "需求 OptiFine `...`"), and
 * {@code release/version.ps1 -CheckSupport} fails when a README table or a note disagrees with this list.
 *
 * <p>The build a jar is compared against is not the file name but OptiFine's own version string -
 * {@code net.optifine.Config.VERSION}, which {@link OptifineVersion} reads. That string is the file name
 * without {@code .jar} and without the {@code preview_} prefix, so the comparison is exact rather than a
 * guess: the ten jars of this line carry, in order,
 * {@code OptiFine_1.21_HD_U_J1_pre9} … {@code OptiFine_1.21.11_HD_U_J9}.
 *
 * <p>A row names the <em>newest</em> build of its release, and a row is only about that release. An installed
 * build that is the same as the row, or newer than it (OptiFine shipped it after this OptiFabric release),
 * is acceptable and must not be reported as wrong; {@link #order} is the explicit ordering that question is
 * answered with, and it is deliberately not a string comparison.
 *
 * <h2>Where the download comes from</h2>
 * Only from the official site: {@link #OFFICIAL_TEMPLATE} for the jar and
 * {@link #OFFICIAL_DOWNLOAD_PAGE} for the page a user browses by hand. This mod ships <b>no</b> third-party
 * OptiFine URL and never redirects a download to one - a user may paste a URL of their own into the field
 * (and {@link OptifineSupport.Build#expand} will fill in the placeholders for them), but that choice is
 * theirs alone.
 *
 * <h2>The two path fields</h2>
 * {@link Build#type} and {@link Build#patch} are stored as the release notes write the build - {@code HD_U_J1}
 * plus an empty patch for a final build, {@code HD_U_J6} plus {@code pre3} for a preview. They double as the
 * two path components that a user's own URL template can ask for through {@code {type}} and {@code {patch}}:
 * some sites spell those with the final build's letter split off, which is exactly what
 * {@link Build#pathType()} and {@link Build#pathPatch()} produce ({@code HD_U} + {@code J1}), while an
 * already-split preview is left alone ({@code HD_U_J6} + {@code pre3}).
 */
public final class OptifineSupport {
	/** The official source the download field is pre-filled with; {@code {file}} is the table's file name. */
	public static final String OFFICIAL_TEMPLATE = "https://optifine.net/adloadx?f={file}";

	/** OptiFine's own download listing, for the "get it yourself" path when a download fails. */
	public static final String OFFICIAL_DOWNLOAD_PAGE = "https://optifine.net/downloads";

	/** Shown for a release whose OptiFine build is known to break shader packs. */
	public static final String NOTE_SHADERS_CRASH = "shaders-crash";

	private static final List<Build> BUILDS = List.of(
			// One row per Minecraft release: the build the prompt recommends and the download button fetches.
			// The rule is "the newest *final* build for that release, or the newest preview only when the
			// release has no final build yet" - which is why 1.21.4 is J3 and not the newer preview J4_pre2,
			// and why 1.21.6/1.21.7/1.21.8/1.21.9/1.21.10 use previews at all. Every other build of the same
			// release is still recognised: order() simply compares it as older, same or newer.
			// The prompt only ever names a *preview* that is older than this row: a final build the user already
			// has is a proper release, so a newer final is not worth interrupting them for.
			new Build("1.21", "HD_U_J1", "pre9", "preview_OptiFine_1.21_HD_U_J1_pre9.jar", null),
			new Build("1.21.1", "HD_U_J1", "", "OptiFine_1.21.1_HD_U_J1.jar", null),
			new Build("1.21.3", "HD_U_J2", "", "OptiFine_1.21.3_HD_U_J2.jar", null),
			new Build("1.21.4", "HD_U_J3", "", "OptiFine_1.21.4_HD_U_J3.jar", null),
			// OptiFine's only 1.21.6 / 1.21.7 builds are previews and cancel the shaderpack load inside
			// OptiFine's own code (ShadersTex.initDynamicTextureNS dereferences a null multiTex), so the
			// release notes state them as "not recommended" for shaders. Both still play without shaders.
			new Build("1.21.6", "HD_U_J6", "pre3", "preview_OptiFine_1.21.6_HD_U_J6_pre3.jar", NOTE_SHADERS_CRASH),
			new Build("1.21.7", "HD_U_J6", "pre7", "preview_OptiFine_1.21.7_HD_U_J6_pre7.jar", NOTE_SHADERS_CRASH),
			new Build("1.21.8", "HD_U_J6", "pre16", "preview_OptiFine_1.21.8_HD_U_J6_pre16.jar", null),
			new Build("1.21.9", "HD_U_J7", "pre2", "preview_OptiFine_1.21.9_HD_U_J7_pre2.jar", null),
			new Build("1.21.10", "HD_U_J7", "pre11", "preview_OptiFine_1.21.10_HD_U_J7_pre11.jar", null),
			new Build("1.21.11", "HD_U_J9", "", "OptiFine_1.21.11_HD_U_J9.jar", null));

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
		/** OptiFine type as the notes write it: {@code HD_U_J1} … {@code HD_U_J9}. */
		public final String type;
		/** Prerelease suffix ({@code pre9}, {@code pre16}, …) or empty for a final build. */
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

		/** {@code {type}} of a user-supplied URL template - see the class comment. */
		public String pathType() {
			if (!patch.isEmpty()) return type;

			int split = type.lastIndexOf('_');

			return split < 0 ? type : type.substring(0, split);
		}

		/** {@code {patch}} of a user-supplied URL template - see the class comment. */
		public String pathPatch() {
			if (!patch.isEmpty()) return patch;

			int split = type.lastIndexOf('_');

			return split < 0 ? type : type.substring(split + 1);
		}

		/** {@code https://optifine.net/adloadx?f=<file>}: the official page carrying the download token. */
		public String officialUrl() {
			return expand(OFFICIAL_TEMPLATE);
		}

		/**
		 * Replaces {@code {mc}}, {@code {file}}, {@code {type}} and {@code {patch}} in a URL the user typed.
		 * A URL without a placeholder is returned as it stands, which is what "treat it as a direct URL" needs.
		 * No template of this mod's own goes through here: the field starts as {@link #officialUrl()}.
		 */
		public String expand(String template) {
			return template.replace("{mc}", mc)
					.replace("{file}", file)
					.replace("{type}", pathType())
					.replace("{patch}", pathPatch());
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

	/** All ten entries, in release order. */
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
	 *       ({@code HD_U_J1_pre9} &lt; {@code HD_U_J1}), and two previews compare by N as numbers
	 *       ({@code pre3} &lt; {@code pre16}).</li>
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

	/** The build at the end of a name: {@code OptiFine_1.21_HD_U_J6_pre16} → build J6, preview 16. */
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
