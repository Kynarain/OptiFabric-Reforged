/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart. The 1.21.x line, which has its own branch, carries the same three files written against yarn
 * names; this is the 26.x counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The support table of this line: the newest OptiFine build this OptiFabric release knows about for each
 * Minecraft release it covers. That is the build the prompt recommends and the one its download button fetches.
 *
 * <p>Nothing here is invented. {@code https://optifine.net/downloads} is the only source, read on 2026-10-02:
 * <ul>
 *   <li><b>26.2</b> has a <em>preview only</em> ("Preview versions": {@code OptiFine HD U K2 pre1}, 22.09.2026)
 *       and an <em>empty</em> main table, so the newest final build for 26.2 does not exist and the newest
 *       preview is what this row names;</li>
 *   <li><b>26.1.2</b> is in the same state ({@code OptiFine HD U K1 pre2}, 22.06.2026, and {@code pre1} before
 *       it, with an empty main table);</li>
 *   <li>every other 26.x release the site lists ({@code 26.1}, {@code 26.1.1}, {@code 26.1.3}, {@code 26.2.1},
 *       {@code 26.3}) has no build at all, which is why this table has exactly two rows.</li>
 * </ul>
 * The rule the rows follow is the one in the project README: <b>the newest final build for that Minecraft
 * release if one exists, otherwise the newest preview</b> - so a preview is only ever named by a row whose
 * release has no final build at all, and a preview is never named where a final exists.
 *
 * <p>The <em>order</em> the prompt decides with is a separate question from that rule, and it is asked on every
 * launch: an installed build is only ever reported when it is <b>older</b> than its row <em>and</em> is itself a
 * preview (see {@link #isPreview}). A final build is a proper release, so an installed final - of any age, even
 * when a newer final exists - is left alone entirely.
 *
 * <p>The build a jar is compared against is not the file name but OptiFine's own version string -
 * {@code net.optifine.Config.VERSION}, which {@link OptifineVersion} reads. That string is the file name
 * without {@code .jar} and without the {@code preview_} prefix, so the comparison is exact rather than a
 * guess: the two jars of this line carry {@code OptiFine_26.2_HD_U_K2_pre1} and
 * {@code OptiFine_26.1.2_HD_U_K1_pre2}.
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
 * {@link Build#type} and {@link Build#patch} are stored the way the release notes write the build: a
 * {@code HD_U_K2} type plus a {@code pre1} patch for a preview, or the type alone with an empty patch for a
 * final build. {@link Build#pathType()} and {@link Build#pathPatch()} split them the way a site that spells a
 * final build with its letter split off would ({@code HD_U} + {@code K2}); an already-split preview type is
 * left alone.
 */
public final class OptifineSupport {
	/** The official source the download field is pre-filled with; {@code {file}} is the table's file name. */
	public static final String OFFICIAL_TEMPLATE = "https://optifine.net/adloadx?f={file}";

	/** OptiFine's own download listing, for the "get it yourself" path when a download fails. */
	public static final String OFFICIAL_DOWNLOAD_PAGE = "https://optifine.net/downloads";

	/** Shown for a release whose OptiFine build cannot load shader packs at all. */
	public static final String NOTE_NO_SHADERS = "no-shaders";

	private static final List<Build> BUILDS = List.of(
			// One row per Minecraft release this line covers: the build the prompt recommends and the download
			// button fetches. Both releases have a preview and no final build on optifine.net, so both rows name
			// their newest preview - and the day OptiFine publishes a final for either of them, that row's file
			// changes to the final and this table is the one place to change. Every other build of the same
			// release is still recognised: order() simply compares it as older, same or newer.
			//
			// The prompt only ever names a *preview* that is older than this row: a final build the user already
			// has is a proper release, so a newer final - or a final at all - is not worth interrupting them for.
			new Build("26.2", "HD_U_K2", "pre1", "preview_OptiFine_26.2_HD_U_K2_pre1.jar", NOTE_NO_SHADERS),
			new Build("26.1.2", "HD_U_K1", "pre2", "preview_OptiFine_26.1.2_HD_U_K1_pre2.jar", null));

	/**
	 * The caveat on 26.2 is not a guess: this line measured it (see {@code docs/PORT_26.x.md} and the README's
	 * Compatibility table) - the 26.2 preview cancels the shaderpack load inside OptiFine's own
	 * {@code Shaders.loadShaderPack}, so picking a shaderpack in OptiFine's settings silently does nothing,
	 * and forcing that load back on (what 2.1.0 shipped) drew a world of particles with see-through blocks.
	 * Read exactly that far: the game itself starts, renders and plays normally on 26.2 - what is missing is
	 * shaders. 26.1.2 has no such caveat, because shaders work there.
	 */

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
		/** OptiFine type as the site writes it: {@code HD_U_K1}, {@code HD_U_K2}. */
		public final String type;
		/** Prerelease suffix ({@code pre1}, {@code pre2}) or empty for a final build. */
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

	/** Both entries, in release order. */
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
	 * numbering does not sort as text: {@code HD_U_K10} would be newer than {@code HD_U_K9}, yet
	 * {@code "K10".compareTo("K9") < 0}.
	 * <ol>
	 *   <li>the build letter first: {@code J1} … {@code J9} are one type, and a letter after {@code K}
	 *       ({@code L1}, were OptiFine to ship one for this release) is newer than anything in the table;</li>
	 *   <li>then the number after that letter, as a number: {@code HD_U_K1} &lt; {@code K2} &lt; … &lt;
	 *       {@code K9} &lt; {@code K10};</li>
	 *   <li>then, inside one type, the patch: a preview ({@code preN}) is older than the final build
	 *       ({@code HD_U_K2_pre1} &lt; {@code HD_U_K2}), and two previews compare by N as numbers
	 *       ({@code pre1} &lt; {@code pre2} &lt; {@code pre16}).</li>
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

	/** The build at the end of a name: {@code OptiFine_26.2_HD_U_K2_pre1} → build K2, preview 1. */
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
