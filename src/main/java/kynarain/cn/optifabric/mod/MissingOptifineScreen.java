/*
 * New in the 1.21.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import net.fabricmc.loader.api.FabricLoader;

/**
 * The screen shown when the running OptiFabric has no usable OptiFine to load, or an older one than this
 * release knows about: it says what is wrong, recommends the latest OptiFine build for this Minecraft release
 * and names the file to look for, and offers to download it from OptiFine's own site.
 *
 * <p>Two shapes, taken from the vanilla error-dialog layout of the reference screenshot (a heading, a couple
 * of explanation lines, a URL field, a status line and three wide stacked buttons):
 * <ul>
 *   <li>{@link Mode#MISSING} - no OptiFine jar in {@code mods/} at all. Shown on <b>every</b> launch until one
 *       is installed; {@code 继续返回主菜单} dismisses it and only for the rest of that session. The mismatch
 *       memory in {@code config/optifabric-mismatch-ack.txt} is never consulted for this mode - see
 *       {@link #shouldPrompt}.</li>
 *   <li>{@link Mode#MISMATCH} - a jar is there, but its build is <em>older</em> than the newest one this
 *       release knows (or the jar is for another Minecraft release, which cannot be ordered against this
 *       one). Lighter: the game keeps running with whatever OptiFine loaded, and {@code 仍要继续} writes
 *       {@code config/optifabric-mismatch-ack.txt} so the same build is not mentioned again.</li>
 * </ul>
 *
 * <p>When a download fails the screen says why and swaps the download button for
 * {@code 打开官网下载页} plus {@code 重新检查}: the user fetches the named jar from OptiFine's own download
 * page and drops it in the mods folder, and the re-check picks it up. Nothing is ever fetched from anywhere
 * the user did not ask for.
 *
 * <p>All downloads happen on a daemon worker thread; the worker only ever writes the volatile fields the
 * render thread reads, and every widget is touched on the render thread alone.
 *
 * <p>The layout is computed from this screen's own {@code width}/{@code height}, which are Minecraft's
 * <em>scaled</em> GUI units - at GUI scale 2 on an 854x480 window that is only 427x240. The heading, the line
 * that names the build this release wants, the status line, the URL field and the buttons <em>are</em> the
 * screen and are always drawn; only the supplementary explanation sentences may be given up, and only
 * longest-first, when the window is genuinely too short to hold them above the status line. No line is ever
 * placed above the top edge or allowed to overlap the next one, and every layout pass prints what it drew and
 * what it had to drop, so the screen can be checked from a log instead of by eye.
 *
 * <p>The colours below carry {@code 0xFF} alpha on purpose: from 1.21.9 on, {@code DrawContext.drawText} and
 * its {@code draw*TextWithShadow} wrappers return early when {@code ColorHelper.getAlpha(color)} is zero, so
 * an RGB-only colour (the {@code 0xFFFFFF} of older versions) draws nothing at all while the widgets still
 * show their own labels - which is exactly how this screen's whole text block once went missing.
 *
 * <p>Strings are picked from the game's own language ({@code options.language}) and both variants live here
 * inline, so nothing has to be shipped as a resource pack and the screen works before any resource reload.
 */
public class MissingOptifineScreen extends Screen {
	/** Which of the two problems this screen is about. */
	public enum Mode {
		MISSING,
		MISMATCH
	}

	/**
	 * Where Mode B's {@code 仍要继续} is remembered; one line, holding the build that was accepted. Only
	 * {@link #shouldPrompt} reads it, and only for {@link Mode#MISMATCH}: it has no say about Mode A.
	 */
	public static final String ACK_FILE = "optifabric-mismatch-ack.txt";

	// Every colour needs a non-zero alpha byte: 1.21.9+ silently drops a text draw whose alpha is zero.
	private static final int COLOR_TEXT = 0xFFFFFFFF;
	private static final int COLOR_DIM = 0xFFA0A0A0;
	private static final int COLOR_OK = 0xFF55FF55;
	private static final int COLOR_WARN = 0xFFFFFF55;
	private static final int COLOR_ERROR = 0xFFFF5555;

	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_STEP = 22;
	private static final int BOTTOM_MARGIN = 16;
	private static final int LINE_STEP = 13;
	/** The smallest gap a squeezed layout may leave between the heading, the text block and the status line. */
	private static final int MIN_GAP = 2;
	/** The heading never comes closer to the top edge than this. */
	private static final int TOP_MARGIN = 4;
	/** Between the URL field and the first button under it. */
	private static final int FIELD_GAP = 6;
	/** The text block keeps this much room at each side, so a wrapped line never touches the edge. */
	private static final int SIDE_MARGIN = 10;

	/** {@code 继续返回主菜单} dismisses Mode A for the rest of the session and nothing else. */
	private static boolean missingDismissed;

	private final Mode mode;
	private final OptifineSupport.Build build;
	/** The build {@link OptifineVersion} read out of the installed jar, or null when there is none. */
	private final String installedBuild;

	private TextFieldWidget urlField;
	private ButtonWidget actionButton;
	/**
	 * Draw-time layout, recomputed by {@link #init()}; {@link #render} only reads it. Every one of these is
	 * derived from this screen's own {@code height}, so a short window moves them up instead of losing them.
	 */
	private int titleY;
	private int statusY;
	private int captionY;
	private int fieldY;
	private int firstButtonY;
	private int lastButtonY;
	/** The text block as the current layout drew it, in reading order, each carrying its own y. */
	private List<Line> drawnLines = List.of();
	/** The explanation sentences the current layout had no room for. */
	private List<Line> droppedLines = List.of();
	/** The status line and the caption, wrapped like the block - they are long in English too. */
	private List<Line> drawnStatus = List.of();
	private List<Line> drawnCaption = List.of();
	/** The last layout logged, so the repeated layout pass at startup does not print twice. */
	private String lastLayout;

	private volatile String status;
	private volatile int statusColor = COLOR_DIM;
	private volatile boolean busy;
	/** True once a download failed; the buttons become the manual path. */
	private volatile boolean failed;
	/** Set by the worker once the state the buttons are built from has changed. */
	private volatile boolean relayoutRequested;
	/** True once a download finished and the game could not restart itself. */
	private volatile boolean closeGameOnly;

	public MissingOptifineScreen(Mode mode, OptifineSupport.Build build, String installedBuild) {
		super(Text.literal(titleFor(mode)));

		this.mode = mode;
		this.build = build;
		this.installedBuild = installedBuild;

		// What this screen decides is only ever visible on screen, so print it as it will read: a log is the
		// one place a run's exact wording can still be checked afterwards.
		System.out.println("[OptiFabric] prompt " + mode + ": " + titleFor(mode));

		for (Line line : buildLines()) {
			System.out.println("[OptiFabric] prompt " + mode + ":   " + line.text);
		}

		System.out.println("[OptiFabric] prompt " + mode + ":   " + idleStatus());

		// The URL field starts as this and the download button fetches whatever the field holds, so the line
		// says which build that button would actually fetch.
		System.out.println("[OptiFabric] prompt " + mode + ":   download " + this.build.officialUrl());
	}

	// ------------------------------------------------------------------ what to show, decided by the mixin

	/**
	 * The prompt a jar state deserves, or null when this screen is not the right answer.
	 *
	 * <p>The mismatch case is "the installed build is <em>older</em> than the newest one this release knows, and only when the installed build is a preview",
	 * which {@link OptifineSupport#order} decides explicitly. An equal build is the supported state, and a
	 * newer one came out after this OptiFabric release and is acceptable, so neither of them may prompt.
	 *
	 * @param jarType what {@link OptifineVersion} made of the jar in {@code mods/}
	 * @param expected the newest build this release knows about, or null when there is no table row
	 * @param installedBuild {@code Config.VERSION} of the installed jar
	 */
	public static Mode modeFor(OptifineVersion.JarType jarType, OptifineSupport.Build expected, String installedBuild) {
		if (jarType == null || expected == null) return null;

		switch (jarType) {
		case MISSING:
			return Mode.MISSING;

		case INCOMPATIBLE:
			// OptiFine's own MC_VERSION is not this release (the 1.21.3 jar in a 1.21.1 instance), or the
			// jar does not declare a version at all. Such a jar is not "an older build of this release", so
			// OptifineSupport.order cannot place it - and OptiFine cannot load it either, so the screen that
			// names the build this release wants is exactly what the user needs. The second has no build to
			// name, so it keeps the old error dialog.
			return installedBuild == null || installedBuild.isEmpty() ? null : Mode.MISMATCH;

		case OPTIFINE_MOD:
		case OPTIFINE_INSTALLER:
			// These two mean the jar declares this Minecraft release (OptifineVersion compared MC_VERSION),
			// so the installed build and the known one can be ordered: e.g. OptiFine_1.21.11_HD_U_J8 in the
			// 1.21.11 instance, where the table names J9. J9 installed is the supported state, and a build
			// newer than J9 (say J10) simply postdates this OptiFabric release.
			// ...and only a *preview* is worth mentioning. A final build the user already has is a proper
			// release, so a newer final shipping later is not a reason to interrupt them; preview-only
			// releases (1.21, 1.21.6 … 1.21.10) still point at their newest preview.
			return OptifineSupport.order(installedBuild, expected) == OptifineSupport.Order.OLDER
					&& OptifineSupport.isPreview(installedBuild) ? Mode.MISMATCH : null;

		default:
			return null;
		}
	}

	/**
	 * Whether the prompt for that state still has something to say this launch.
	 *
	 * <p>Mode A cannot be suppressed across sessions: {@code missingDismissed} is a static field of this class,
	 * so a new JVM starts with it false, and the acknowledgement file is only ever read on the Mode B branch
	 * below. {@code config/optifabric-mismatch-ack.txt} - even one naming an accepted build - therefore cannot
	 * keep Mode A off the screen, which is what "no OptiFine is shown on every launch" needs.
	 */
	public static boolean shouldPrompt(Mode mode, String installedBuild) {
		if (mode == Mode.MISSING) return !missingDismissed;

		return !isAcknowledged(installedBuild);
	}

	/** {@code 仍要继续}: remember this exact build, so the same older one is not mentioned again. */
	public static void acknowledge(String installedBuild) {
		Path file = ackFile();

		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, installedBuild == null ? "" : installedBuild, StandardCharsets.UTF_8);
			System.out.println("[OptiFabric] OptiFine build " + installedBuild + " accepted, remembered in " + file);
		} catch (IOException e) {
			System.out.println("[OptiFabric] Could not remember " + installedBuild + " in " + file + ": " + e);
		}
	}

	private static boolean isAcknowledged(String installedBuild) {
		Path file = ackFile();

		if (!Files.isRegularFile(file) || installedBuild == null) return false;

		try {
			return Files.readString(file, StandardCharsets.UTF_8).trim().equals(installedBuild);
		} catch (IOException e) {
			System.out.println("[OptiFabric] Could not read " + file + ": " + e);

			return false;
		}
	}

	/** Beside the {@code .optifine} cache, in the game directory's {@code config/} folder. */
	private static Path ackFile() {
		return FabricLoader.getInstance().getConfigDir().resolve(ACK_FILE);
	}

	// ------------------------------------------------------------------ layout

	/** One button of the stacked block at the bottom. */
	private static final class Row {
		final String label;
		final Runnable action;

		Row(String label, Runnable action) {
			this.label = label;
			this.action = action;
		}
	}

	/**
	 * One line of the text block, with the colour it is drawn in and whether the screen is allowed to give it
	 * up when the window is too short.
	 *
	 * <p>{@link #required} marks what the prompt actually exists for: the heading (drawn separately, from
	 * {@link #getTitle()}) and the lines naming the build this release wants. Those are placed whatever the
	 * height is. Everything else is an explanation and is a candidate for dropping - which is why the layout
	 * can never lose the whole block: at least one line in every list is required, so the drop loop always
	 * runs out of candidates before it runs out of lines.
	 */
	private static final class Line {
		final String text;
		final int color;
		final boolean required;
		/**
		 * Wrapped pieces of one source line share a group number and are given up together, so dropping a line
		 * can never leave half a sentence behind. Unwrapped lines keep group 0.
		 */
		final int group;
		/** Where the layout that produced this line put its top edge; only valid until the next layout. */
		int y;

		Line(String text, int color, boolean required) {
			this(text, color, required, 0);
		}

		Line(String text, int color, boolean required, int group) {
			this.text = text;
			this.color = color;
			this.required = required;
			this.group = group;
		}
	}

	/**
	 * The buttons for the current state. The download button is the middle one of the reference screen and is
	 * replaced - never joined - by the manual path once a download failed, so the screen can never look like it
	 * is still offering to fetch something by itself.
	 */
	private List<Row> rows() {
		List<Row> rows = new ArrayList<>();

		if (this.failed) {
			rows.add(new Row(t("Open the official download page", "打开官网下载页"), this::openOfficialPage));
			rows.add(new Row(t("Open mods folder", "打开 Mod 文件夹"), this::openModsFolder));
			rows.add(new Row(t("Check again", "重新检查"), this::recheck));
		} else {
			rows.add(new Row(t("Open mods folder", "打开 Mod 文件夹"), this::openModsFolder));

			if (this.closeGameOnly) {
				rows.add(new Row(t("Close game", "关闭游戏"), this::closeGame));
			} else {
				rows.add(new Row(downloadLabel(), this::startDownload));
			}
		}

		rows.add(new Row(dismissLabel(), this::dismiss));

		return rows;
	}

	@Override
	protected void init() {
		String source = this.urlField != null ? this.urlField.getText() : this.build.officialUrl();
		List<Row> rows = rows();
		int fieldWidth = Math.min(320, this.width - 40);
		int left = (this.width - fieldWidth) / 2;
		int fontHeight = this.textRenderer.fontHeight;

		// GUI scale 2 on an 854x480 window leaves only a few hundred GUI pixels of height, and GUI scale 3
		// leaves half of that again, so the layout is derived from the height that is really there. The
		// comfortable spacing is the reference layout; when it does not fit, the same layout is retried with
		// every gap squeezed to MIN_GAP, and only then may explanations be given up.
		List<Line> lines = wrapLines(buildLines());

		// The heading is the topmost thing on the screen and the only thing above the text block. It is
		// clamped to at least TOP_MARGIN, so no arithmetic here can push a line off the top edge.
		this.titleY = Math.max(TOP_MARGIN, Math.min(this.height / 12, 40));

		if (!layout(lines, rows, fontHeight, LINE_STEP, LINE_STEP - fontHeight, BOTTOM_MARGIN, false)
				&& !layout(lines, rows, fontHeight, fontHeight + MIN_GAP, MIN_GAP, MIN_GAP, false)) {
			// Not even the bare minimum fits (a GUI height under about 150 pixels). The required lines are
			// the screen, so they are placed anyway, as low as the heading allows, and the log says so.
			layout(lines, rows, fontHeight, fontHeight + MIN_GAP, MIN_GAP, MIN_GAP, true);
		}

		this.urlField = new TextFieldWidget(this.textRenderer, left, this.fieldY, fieldWidth, BUTTON_HEIGHT,
				Text.literal(t("OptiFine download source", "OptiFine 下载地址")));
		this.urlField.setMaxLength(2048);
		this.urlField.setText(source);
		this.addDrawableChild(this.urlField);

		this.actionButton = null;

		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			ButtonWidget button = ButtonWidget.builder(Text.literal(row.label), pressed -> row.action.run())
					.dimensions(left, this.firstButtonY + i * BUTTON_STEP, fieldWidth, BUTTON_HEIGHT)
					.build();

			// The download button (only ever the second row of the idle layout) is the one that goes dead
			// while a download is running.
			if (!this.failed && !this.closeGameOnly && i == 1) {
				button.active = !this.busy;
				this.actionButton = button;
			}

			this.addDrawableChild(button);
		}

		// A layout is only visible on screen; this is what makes it checkable from a log instead.
		logLayout();
	}

	/**
	 * Splits every line that is wider than the block into as many lines as it needs. The English wording is far
	 * wider than the Chinese for the same sentence, and a single centred line that overflows is clipped at both
	 * edges - which is exactly how the English layout looked broken. The pieces of one source line share a
	 * {@link Line#group}, so the drop logic still gives up or keeps a whole sentence, never half of one.
	 */
	private List<Line> wrapLines(List<Line> lines) {
		int maxWidth = maxTextWidth();
		List<Line> wrapped = new ArrayList<>();
		int group = 0;

		for (Line line : lines) {
			wrapped.addAll(wrapText(line.text, line.color, line.required, maxWidth, ++group));
		}

		return wrapped;
	}

	/**
	 * Wraps one string into as many lines as fit {@code maxWidth}. Every piece shares one {@code group}, so the
	 * drop logic gives up or keeps a whole sentence and never leaves half of one behind.
	 */
	private List<Line> wrapText(String text, int color, boolean required, int maxWidth, int group) {
		List<Line> wrapped = new ArrayList<>();

		if (text == null || text.isEmpty()) return wrapped;

		if (this.textRenderer.getWidth(text) <= maxWidth) {
			wrapped.add(new Line(text, color, required, group));

			return wrapped;
		}

		StringBuilder current = new StringBuilder();

		for (String word : text.split(" ")) {
			for (String piece : pieces(word, maxWidth)) {
				if (current.length() == 0) {
					current.append(piece);
				} else if (this.textRenderer.getWidth(current + " " + piece) <= maxWidth) {
					current.append(' ').append(piece);
				} else {
					wrapped.add(new Line(current.toString(), color, required, group));
					current.setLength(0);
					current.append(piece);
				}
			}
		}

		if (current.length() > 0) wrapped.add(new Line(current.toString(), color, required, group));

		return wrapped;
	}

	/** The widest a text line may be before it is wrapped. */
	private int maxTextWidth() {
		return Math.max(80, this.width - 2 * SIDE_MARGIN);
	}

	/** Cuts one word into pieces that each fit, so a token wider than the block cannot overflow either. */
	private List<String> pieces(String word, int maxWidth) {
		List<String> pieces = new ArrayList<>();
		StringBuilder piece = new StringBuilder();

		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);

			if (piece.length() > 0 && this.textRenderer.getWidth(piece.toString() + c) > maxWidth) {
				pieces.add(piece.toString());
				piece.setLength(0);
			}

			piece.append(c);
		}

		if (piece.length() > 0) pieces.add(piece.toString());

		return pieces;
	}

	/** The distance from the top of a block of {@code count} lines to the bottom of its last one. */
	private static int blockHeight(int count, int step, int fontHeight) {
		return count == 0 ? 0 : (count - 1) * step + fontHeight;
	}

	/**
	 * Places the bottom furniture - the buttons, the URL field above them, the caption above that and the
	 * status line above that - and then as much of the text block as fits between it and the heading.
	 *
	 * <p>The block is centred in the band between the heading and the status line, but never closer to
	 * the heading than {@code gap} and never above {@link #TOP_MARGIN}: {@code firstY} is computed from the
	 * real {@code height} and can therefore never be negative. When the block does not fit, explanation lines
	 * are given up <em>longest first</em>, so the shortest explanation survives longest and the required lines
	 * are not candidates at all.
	 *
	 * @param step distance between two lines of the block
	 * @param gap distance between two blocks (heading, block, status, caption, field, buttons)
	 * @param bottomMargin space left under the last button
	 * @param force when true the required lines are placed even if they do not fit; used as the last resort
	 * @return true when every required line was placed and the whole block fits without overlap
	 */
	private boolean layout(List<Line> lines, List<Row> rows, int fontHeight, int step, int gap, int bottomMargin,
			boolean force) {
		this.lastButtonY = this.height - bottomMargin - BUTTON_HEIGHT;
		this.firstButtonY = this.lastButtonY - (rows.size() - 1) * BUTTON_STEP;
		this.fieldY = this.firstButtonY - FIELD_GAP - BUTTON_HEIGHT;

		// The status line and the caption are wrapped as well: in English they are long enough to be clipped at
		// the edges, and they are not part of the text block the drop logic manages.
		int maxWidth = maxTextWidth();
		String statusText = this.status == null ? idleStatus() : this.status;
		int statusLineColor = this.status == null ? COLOR_DIM : this.statusColor;
		List<Line> statusLines = wrapText(statusText, statusLineColor, false, maxWidth, 0);
		List<Line> captionLines = wrapText(t("Download URL (official; you may replace it)",
				"下载地址(官方地址;也可以改成你自己的)"), COLOR_DIM, false, maxWidth, 0);

		this.captionY = this.fieldY - gap - blockHeight(captionLines.size(), fontHeight, fontHeight);
		this.statusY = this.captionY - gap - blockHeight(statusLines.size(), fontHeight, fontHeight);

		// The band the block may use: under the heading, over the status line.
		int top = this.titleY + fontHeight + gap;
		int bottom = this.statusY - gap;
		List<Line> kept = new ArrayList<>(lines);
		List<Line> dropped = new ArrayList<>();

		while (blockHeight(kept.size(), step, fontHeight) > bottom - top) {
			Line longest = null;

			for (Line line : kept) {
				if (line.required) continue;
				if (longest == null || line.text.length() > longest.text.length()) longest = line;
			}

			if (longest == null) break; // only the screen's own lines are left

			// Give up the whole wrapped sentence, never one piece of it.
			final Line chosen = longest;
			final int group = chosen.group;

			kept.removeIf(line -> !line.required && (line == chosen || (group != 0 && line.group == group)));
			dropped.add(chosen);
		}

		boolean fits = blockHeight(kept.size(), step, fontHeight) <= bottom - top;
		// Centre the block in the band between the heading and the status line. Pinning it to the bottom
		// left a large empty hole under the heading whenever the block was short (a couple of lines), which
		// is what made the screen look top-heavy and unbalanced.
		int slack = Math.max(0, (bottom - top) - blockHeight(kept.size(), step, fontHeight));
		int firstY = fits ? top + slack / 2 : top;

		for (int i = 0; i < kept.size(); i++) {
			kept.get(i).y = firstY + i * step;
		}

		this.drawnLines = kept;
		this.droppedLines = dropped;

		for (int i = 0; i < statusLines.size(); i++) {
			statusLines.get(i).y = this.statusY + i * fontHeight;
		}

		for (int i = 0; i < captionLines.size(); i++) {
			captionLines.get(i).y = this.captionY + i * fontHeight;
		}

		this.drawnStatus = statusLines;
		this.drawnCaption = captionLines;

		return fits || force;
	}

	/**
	 * Prints the layout once per layout pass - that is once per screen at startup, and again if the window is
	 * resized or the button block is rebuilt. The drawn rows come with their y, the dropped ones with the
	 * reason, so "the heading and the key line were drawn" is a statement a log can settle.
	 */
	private void logLayout() {
		int fontHeight = this.textRenderer.fontHeight;
		int blockBottom = this.titleY + fontHeight;

		for (Line line : this.drawnLines) {
			blockBottom = Math.max(blockBottom, line.y + fontHeight);
		}

		// Everything that has to hold for the screen to be readable: nothing off the top edge, the block
		// clear of the status line, and the status, caption, field and buttons in that order inside the
		// screen. Logged as one word so a run can be checked without looking at it.
		boolean inside = this.titleY >= TOP_MARGIN && this.statusY >= this.titleY + fontHeight
				&& this.captionY >= this.statusY + fontHeight && this.fieldY >= this.captionY + fontHeight
				&& this.firstButtonY >= this.fieldY + BUTTON_HEIGHT
				&& this.lastButtonY + BUTTON_HEIGHT <= this.height && blockBottom <= this.statusY;

		List<String> rows = new ArrayList<>();
		rows.add("layout gui=" + this.width + "x" + this.height + " titleY=" + this.titleY + " statusY=" + this.statusY
				+ " captionY=" + this.captionY + " urlFieldY=" + this.fieldY + ".." + (this.fieldY + BUTTON_HEIGHT)
				+ " buttonsY=" + this.firstButtonY + ".." + (this.lastButtonY + BUTTON_HEIGHT) + " insideScreen=" + inside);
		rows.add("  drawn y=" + this.titleY + " required (heading): " + getTitle().getString());

		for (Line line : this.drawnLines) {
			rows.add("  drawn y=" + line.y + (line.required ? " required: " : " explanation: ") + line.text);
		}

		for (Line line : this.droppedLines) {
			rows.add("  dropped (no room above statusY=" + this.statusY + ") explanation: " + line.text);
		}

		// Minecraft lays a screen out more than once at startup, because the window's real size arrives after
		// the first pass. Only a layout that differs from the last one printed is logged again, so a launch
		// leaves exactly one block per screen, and a window resize still leaves a fresh one.
		String layout = String.join("\n", rows);

		if (layout.equals(this.lastLayout)) return;

		this.lastLayout = layout;

		for (String row : rows) {
			System.out.println("[OptiFabric] prompt " + this.mode + ": " + row);
		}
	}

	private List<Line> buildLines() {
		List<Line> lines = new ArrayList<>();
		String installed = this.installedBuild == null || this.installedBuild.isEmpty()
				? t("(unknown)", "(未知)") : this.installedBuild;

		if (this.mode == Mode.MISSING) {
			// What is wrong is already in the heading text, so this sentence is an explanation: it may go.
			lines.add(new Line(t("OptiFabric is loaded, but no OptiFine jar was found in the mods folder. The game started without it.",
					"OptiFabric 已加载,但 mods 文件夹里没有找到 OptiFine,游戏已在没有它的情况下启动。"), COLOR_DIM, false));
		} else {
			// The two halves of "what is installed against what this release wants" are the key line here.
			lines.add(new Line(t("Installed: " + installed + " / Recommended: " + this.build.buildName(),
					"已安装:" + installed + " / 建议:" + this.build.buildName()), COLOR_DIM, true));
		}

		// Both modes say the same thing: what this release recommends is the *latest* build for it, and the
		// file name stays visible because that is what the user has to recognise on OptiFine's download page.
		// This is the one line that must never be dropped in either mode - it names the jar to look for.
		lines.add(new Line(t("Use the latest OptiFine for this version (currently: " + this.build.file + ")",
				"建议使用最新版 OptiFine(当前最新:" + this.build.file + ")"), COLOR_OK, true));

		if (this.failed) {
			lines.add(new Line(t("Open the official download page, download it there, then put it in the mods folder.",
					"请打开官网下载页自行下载,再把它放进 mods 文件夹。"), COLOR_DIM, false));
		}

		String note = noteText();
		if (note != null) lines.add(new Line(note, COLOR_WARN, false));

		return lines;
	}

	private String noteText() {
		if (OptifineSupport.NOTE_SHADERS_CRASH.equals(this.build.note)) {
			return t("Shader packs crash inside this OptiFine build - not recommended.",
					"光影包会崩在这个 OptiFine 构建内部,不推荐使用光影。");
		}

		return null;
	}

	private String idleStatus() {
		if (this.mode == Mode.MISSING) {
			return t("Click Download OptiFine for the latest build, or put the jar into the mods folder yourself.",
					"点「下载 OptiFine」下载最新版,也可以自己把 jar 放进 mods 文件夹。");
		}

		return t("Click Download the right build for the latest one, or Continue anyway to play as it is.",
				"点「下载正确的版本」换成最新版,或点「仍要继续」直接进游戏。");
	}

	private String downloadLabel() {
		if (this.mode == Mode.MISSING) return t("Download OptiFine", "下载 OptiFine");

		return t("Download the right build", "下载正确的版本");
	}

	private String dismissLabel() {
		if (this.mode == Mode.MISSING) return t("Continue to main menu", "继续返回主菜单");

		return t("Continue anyway", "仍要继续");
	}

	private static String titleFor(Mode mode) {
		if (mode == Mode.MISSING) return t("OptiFine is not installed", "OptiFine 未安装");

		return t("OptiFine version mismatch", "OptiFine 版本不匹配");
	}

	// ------------------------------------------------------------------ drawing

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		// Re-laying out has to happen on the render thread, and before the widgets are drawn.
		if (this.relayoutRequested) {
			this.relayoutRequested = false;
			this.clearAndInit();
		}

		super.render(context, mouseX, mouseY, delta);

		int center = this.width / 2;

		// The heading and the rows init() decided on. Whatever is not in drawnLines was dropped there, where
		// the room was known - render() itself never gives a line up.
		context.drawCenteredTextWithShadow(this.textRenderer, getTitle(), center, this.titleY, COLOR_TEXT);

		for (Line line : this.drawnLines) {
			context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(line.text), center, line.y, line.color);
		}

		for (Line line : this.drawnStatus) {
			context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(line.text), center, line.y, line.color);
		}

		for (Line line : this.drawnCaption) {
			context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(line.text), center, line.y, line.color);
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		// Escape dismisses the same way the last button does.
		return true;
	}

	@Override
	public void close() {
		dismiss();
	}

	// ------------------------------------------------------------------ actions

	private void openOfficialPage() {
		Util.getOperatingSystem().open(OptifineSupport.OFFICIAL_DOWNLOAD_PAGE);
	}

	private void openModsFolder() {
		Util.getOperatingSystem().open(modsDir());
	}

	private static File modsDir() {
		return new File(FabricLoader.getInstance().getGameDirectory(), "mods");
	}

	private void dismiss() {
		if (this.mode == Mode.MISSING) {
			missingDismissed = true;
		} else {
			acknowledge(this.installedBuild);
		}

		if (this.client != null) this.client.setScreen(new TitleScreen());
	}

	private void closeGame() {
		if (this.client != null) this.client.scheduleStop();
	}

	/** {@code 重新检查}: has the jar the official page provides been dropped into {@code mods/} meanwhile? */
	private void recheck() {
		File jar = new File(modsDir(), this.build.file);

		if (jar.isFile() && OptifineDownloader.isOptifineArchive(jar)) {
			this.failed = false;
			setStatus(t("Found " + this.build.file + ".", "已找到 " + this.build.file + "。"), COLOR_OK);
			showRestartPrompt();
		} else {
			setStatus(t("Still no " + this.build.file + " in the mods folder.",
					"mods 文件夹里还是没有 " + this.build.file + "。"), COLOR_WARN);
		}

		this.relayoutRequested = true;
	}

	private void startDownload() {
		if (this.busy || this.closeGameOnly || this.failed) return;

		String source = this.urlField == null ? "" : this.urlField.getText().trim();

		if (source.isEmpty()) {
			setStatus(t("The download URL is empty.", "下载地址是空的。"), COLOR_ERROR);

			return;
		}

		File mods = modsDir();

		this.busy = true;
		if (this.actionButton != null) this.actionButton.active = false;
		setStatus(t("Starting the download...", "正在开始下载..."), COLOR_DIM);

		Thread worker = new Thread(() -> runDownload(source, mods), "OptiFabric-OptiFine-download");
		worker.setDaemon(true);
		worker.start();
	}

	/** Runs on the worker thread; only the volatile fields and the log are touched here. */
	private void runDownload(String source, File mods) {
		try {
			OptifineDownloader.Outcome outcome = OptifineDownloader.download(source, this.build, mods, new OptifineDownloader.Progress() {
				@Override
				public void stage(String key, String detail) {
					switch (key) {
					case "page":
						setStatus(t("Fetching the download page...", "正在获取下载页..."), COLOR_DIM);
						break;
					case "save":
						setStatus(t("Saving into the mods folder...", "正在写入 mods 文件夹..."), COLOR_DIM);
						break;
					default:
						break;
					}
				}

				@Override
				public void bytes(long received, long total) {
					String count = total > 0 ? mb(received) + " / " + mb(total) : mb(received);

					setStatus(t("Downloading ", "正在下载 ") + count, COLOR_DIM);
				}
			});

			if (outcome.alreadyPresent) {
				setStatus(t(outcome.file.getName() + " is already there.", outcome.file.getName() + " 已经存在,无需下载。"), COLOR_OK);
			} else {
				setStatus(t("Downloaded " + outcome.file.getName() + " from " + outcome.source,
						"已从 " + outcome.source + " 下载 " + outcome.file.getName()), COLOR_OK);
			}

			// A successful download must not restart the game unannounced: ask, and let "Restart now" do it.
			showRestartPrompt();
		} catch (Throwable t) {
			System.out.println("[OptiFabric] OptiFine download failed: " + t);
			t.printStackTrace();
			this.failed = true;
			setStatus(t("Download failed: ", "下载失败:") + String.valueOf(t.getMessage()), COLOR_ERROR);
		} finally {
			this.busy = false;
			this.relayoutRequested = true;
		}
	}

	/** After a download (or a re-check that found the jar): ask instead of restarting unannounced. */
	private void showRestartPrompt() {
		if (this.client == null) return;
		MinecraftClient client = this.client;
		client.execute(() -> {
			Text title = Text.literal(t("Download complete", "下载已完成"));
			Text message = Text.literal(t("Restart the game to make OptiFine take effect?",
					"是否重启游戏使 OptiFine 生效?"));
			Text yes = Text.literal(t("Restart now", "立即重启"));
			Text no = Text.literal(t("Continue to main menu", "继续返回主菜单"));
			client.setScreen(new ConfirmScreen(ok -> {
				if (ok) {
					// "Restart now": start the replacement process, then end this one - otherwise both would
					// run at once. When the relaunch is impossible (a launcher that hides the command line and
					// no Windows/JNA path), closing is all that is left, and the launcher's own Play button is
					// then the way back in.
					OptifineDownloader.restart();
					client.scheduleStop();
				} else {
					client.setScreen(new TitleScreen());
				}
			}, title, message, yes, no));
		});
	}

	private void setStatus(String text, int color) {
		this.statusColor = color;
		this.status = text;
	}

	private static String mb(long bytes) {
		return String.format(Locale.ROOT, "%.1f MB", bytes / 1048576.0);
	}

	// ------------------------------------------------------------------ language

	/** Chinese when the game's own language is any {@code zh_*} variant, English otherwise. */
	private static boolean isChinese() {
		try {
			MinecraftClient client = MinecraftClient.getInstance();

			if (client == null || client.options == null) return false;

			String language = client.options.language;

			return language != null && language.toLowerCase(Locale.ROOT).startsWith("zh");
		} catch (Throwable t) {
			return false;
		}
	}

	private static String t(String english, String chinese) {
		return isChinese() ? chinese : english;
	}
}
