/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart. The 1.21.x line, which has its own branch, carries the same three files written against yarn
 * names; this is the 26.x counterpart, and nothing in here is Minecraft-specific - which is why it is one of
 * the two classes the 26.x line can carry over unchanged.
 */
package kynarain.cn.optifabric.mod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.Locale;
import java.util.Set;

/**
 * Fetches the OptiFine jar the support table asks for and puts it into a {@code mods} folder.
 *
 * <p>Everything here is plain JDK: no Minecraft, no Fabric, no third-party HTTP client. That is what lets
 * {@link #selfTest} be run both from the game (through {@link #TEST_PROPERTY}) and from a bare JVM, which is
 * how the resolution, validation and save paths are exercised without the screen.
 *
 * <h2>One source, and only the one that was asked for</h2>
 * <ul>
 *   <li>a URL containing {@code optifine.net} is the <b>official two-step flow</b>: the {@code adloadx?f=<file>}
 *       page carries a one-use token, and the jar is behind {@code downloadx?f=<file>&x=<token>} on the same
 *       host. The page is HTML, so it is asked for with a browser User-Agent;</li>
 *   <li>a URL with {@code {mc}} / {@code {type}} / {@code {patch}} / {@code {file}} is filled in from the
 *       support table and fetched directly - this only happens for a URL the <em>user</em> typed;</li>
 *   <li>anything else is a <b>direct URL</b>.</li>
 * </ul>
 *
 * <p>There is deliberately <b>no second source</b>, and this line ships no mirror of OptiFine's files anywhere
 * in code, documentation or tests. If what was asked for fails - a non-200 status, no token on the page, a body
 * that is not a zip, a zip that is not OptiFine, an unreachable host - the failure is reported with its reason
 * and that is the end of it. Silently fetching the same jar from somewhere else is not this mod's decision to
 * make, and neither is naming such a place: the screen points at OptiFine's own download page instead and lets
 * the user do it by hand.
 *
 * <h2>Bounds</h2>
 * Connect timeout 15 s, whole-response timeout 3 min, at most {@link #MAX_BYTES} read, https→http redirects
 * refused ({@link HttpClient.Redirect#NORMAL}), no scheme other than http/https accepted. A hostile or broken
 * URL therefore ends as an {@link IOException} with a readable reason, never as an exception out of the screen
 * and never as a partially written jar in {@code mods}.
 *
 * <h2>JNA</h2>
 * {@link #restartWindows()} is the only JNA user and the only Windows-specific code; JNA itself is
 * {@code compileOnly} in {@code build.gradle} and is loaded at runtime from Minecraft's own libraries.
 * <b>5.17.0 is the version Minecraft 26.2 declares</b> (the 1.21.x line compiles against 5.14.0, which is what
 * 1.21.1 ships); the two {@code WinBase} records and {@code Kernel32} used here have the same shape in both,
 * so the 5.x API in this file is stable across the two lines.
 */
public final class OptifineDownloader {
	/** {@code -Doptifabric.optifineDownloadTest=<url>[,<file>][;<test>…]} runs {@link #selfTest}. */
	public static final String TEST_PROPERTY = "optifabric.optifineDownloadTest";

	/** The class every OptiFine jar carries. */
	public static final String CONFIG_PLAIN = "net/optifine/Config.class";
	public static final String CONFIG_NOTCH = "notch/net/optifine/Config.class";

	/**
	 * The third location, and the one that matters for this line: modern OptiFine ships the mapped
	 * {@code srg/} tree as well as the {@code notch/} one, and the 26.2 preview carries {@code Config.class}
	 * under both while it carries nothing under {@code net/optifine/}. Accepting all three is deliberate -
	 * this is the same question {@link OptifineVersion} answers when it decides whether a jar is OptiFine.
	 */
	public static final String CONFIG_SRG = "srg/net/optifine/Config.class";

	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);
	private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(3);
	private static final long MAX_BYTES = 64L * 1024 * 1024;
	private static final long PROGRESS_INTERVAL_NANOS = 250_000_000L;

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
			+ " (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

	private static final Pattern DOWNLOAD_LINK = Pattern.compile("downloadx\\?[^\"'<>\\s]+");

	private static final HttpClient CLIENT = HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.NORMAL)
			.connectTimeout(CONNECT_TIMEOUT)
			.build();

	private OptifineDownloader() {
	}

	/** Progress of one download, called from the worker thread - a UI must only touch its own state there. */
	public interface Progress {
		/**
		 * A stage was entered. {@code key} is a stable id, so the UI can localise it:
		 * {@code "page"} (fetching the official download page), {@code "download"} (fetching the jar),
		 * {@code "save"}.
		 */
		void stage(String key, String detail);

		/** Bytes received so far; {@code total} is the server's Content-Length or -1 when it sent none. */
		void bytes(long received, long total);

		Progress NONE = new Progress() {
			@Override
			public void stage(String key, String detail) {
			}

			@Override
			public void bytes(long received, long total) {
			}
		};
	}

	/** What a finished download produced. */
	public static final class Outcome {
		/** The jar now in the target folder. */
		public final File file;
		/** The URL the bytes came from - always the one that was asked for. */
		public final String source;
		/** True when the jar was already there and nothing was downloaded. */
		public final boolean alreadyPresent;
		/** SHA-256 of the jar's bytes. */
		public final String sha256;

		Outcome(File file, String source, boolean alreadyPresent, String sha256) {
			this.file = file;
			this.source = source;
			this.alreadyPresent = alreadyPresent;
			this.sha256 = sha256;
		}
	}

	/**
	 * The screen's entry point: puts the expected OptiFine jar into {@code targetDir}, downloading it only when
	 * it is not already there.
	 *
	 * @param sourceTemplate the URL in the text field - the official one unless the user replaced it
	 * @param build the support table entry for the running Minecraft release
	 * @param targetDir normally the instance's {@code mods} folder
	 */
	public static Outcome download(String sourceTemplate, OptifineSupport.Build build, File targetDir, Progress progress) throws IOException {
		File target = new File(targetDir, build.file);

		if (target.isFile() && isOptifineArchive(target)) {
			byte[] existing = read(target.toPath());

			if (OptifineHashes.matches(build, existing)) {
				return new Outcome(target, target.toURI().toString(), true, sha256(existing));
			}

			// A jar that does not match the recorded contents of this build is removed rather than returned:
			// otherwise a download that was refused once would be accepted by the next launch.
			System.out.println("[OptiFabric] " + target + " is not one of the recorded contents of " + build.file + "; downloading it again");
			removeRejected(target);
		}

		return fetch(sourceTemplate, build, targetDir, build.file, progress);
	}

	/**
	 * Resolves {@code sourceTemplate}, validates what came back and writes it into {@code targetDir} under
	 * {@code fileName}. No "already present" check - the self-test relies on that, so every run really
	 * downloads. Nothing but this one source is ever asked.
	 */
	public static Outcome fetch(String sourceTemplate, OptifineSupport.Build build, File targetDir, String fileName, Progress progress) throws IOException {
		String url = build.expand(sourceTemplate);
		Payload payload = fetchValidated(url, progress);

		progress.stage("save", targetDir.getPath());

		// Checked before it is written: a jar that fails the check must not be left on disk for the next launch
		// to pick up as "already present".
		OptifineHashes.require(build, payload.bytes, payload.source);

		File target = write(payload.bytes, targetDir, fileName);
		verifyIdentity(target, build, payload.source);

		return new Outcome(target, payload.source, false, sha256(payload.bytes));
	}

	/** One source, resolved and validated. Throws with the reason, so the caller can show it. */
	/**
	 * Checks that the bytes really are the build that was asked for, by reading the version OptiFine declares in
	 * its own {@code Config.class} rather than trusting the URL or the file name. {@code VERSION} holds the jar's
	 * own name ({@code OptiFine_1.21.11_HD_U_J9}) and {@code MC_VERSION} the release it is for - the same pair
	 * {@link OptifineVersion} already reads to decide whether a jar is OptiFine at all, so this adds no new
	 * parsing and no table to maintain.
	 *
	 * <p>The file is deleted before throwing, so a jar that is not the requested build never stays in mods/.
	 */
	private static void verifyIdentity(File target, OptifineSupport.Build build, String source) throws IOException {
		OptifineVersion.Parsed parsed;

		try {
			parsed = OptifineVersion.parseJarType(target);
		} catch (IOException | RuntimeException e) {
			//parseJarType throws a RuntimeException on its own error path, and a jar whose type cannot be worked
			//out reaches a switch on a null. This method's contract is that a jar which is not the requested build
			//never stays in mods/, so both kinds have to remove it before they throw.
			removeRejected(target);
			throw new IOException(source + " produced a jar that cannot be read back to see which build it is: " + e, e);
		}

		boolean isOptifine = parsed.type == OptifineVersion.JarType.OPTIFINE_MOD
				|| parsed.type == OptifineVersion.JarType.OPTIFINE_INSTALLER;

		if (!isOptifine) {
			removeRejected(target);
			throw new IOException(source + " did not produce an OptiFine jar (" + parsed.type + ")");
		}

		if (!build.buildName().equals(parsed.version) || !build.mc.equals(parsed.minecraftVersion)) {
			removeRejected(target);
			throw new IOException(source + " produced " + parsed.version + " for Minecraft " + parsed.minecraftVersion
					+ ", not the " + build.buildName() + " for Minecraft " + build.mc + " that was asked for");
		}

		System.out.println("[OptiFabric] " + target.getName() + " declares " + parsed.version + " for Minecraft "
				+ parsed.minecraftVersion + ", which is the build that was asked for");
	}

	/** Removes a jar that failed the build check; the mismatch itself is the error worth reporting. */
	private static void removeRejected(File target) {
		try {
			Files.deleteIfExists(target.toPath());
		} catch (IOException e) {
			System.err.println("[OptiFabric] Could not remove " + target + " after it failed the build check: " + e);
		}
	}

	private static Payload fetchValidated(String url, Progress progress) throws IOException {
		Payload payload = fetchOne(url, progress);

		validate(payload.bytes, payload.source);

		return payload;
	}

	private static Payload fetchOne(String url, Progress progress) throws IOException {
		URI uri = toUri(url);
		requireHttps(uri);

		if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
			throw new IOException("only http and https sources are supported, not " + url);
		}

		if (isOfficial(uri)) {
			progress.stage("page", url);
			byte[] page = get(uri, progress);
			String link = findDownloadLink(new String(page, StandardCharsets.ISO_8859_1));

			if (link == null) {
				throw new IOException("no downloadx?f=...&x=... link on " + uri + " (the page carried no download token)");
			}

			URI jarUri;
			try {
				jarUri = uri.resolve(link);
				requireHttps(jarUri);
			} catch (IllegalArgumentException e) {
				throw new IOException("the download link on " + uri + " is not a usable URL: " + link, e);
			}

			if (!"http".equalsIgnoreCase(jarUri.getScheme()) && !"https".equalsIgnoreCase(jarUri.getScheme())) {
				throw new IOException("refusing the download link on " + uri + ": " + jarUri + " is not http(s)");
			}

			progress.stage("download", jarUri.toString());

			return new Payload(get(jarUri, progress), jarUri.toString());
		}

		progress.stage("download", url);

		return new Payload(get(uri, progress), url);
	}

	/** The only host whose pages are asked for a download token. */
	private static final Set<String> OFFICIAL_HOSTS = Set.of("optifine.net", "www.optifine.net");

	/**
	 * Whether this URI is the official site. The host is compared exactly rather than searched for in the
	 * string: {@code https://evil.example/?optifine.net} contains the name and is not the site, and it would
	 * otherwise be handed the two-step flow, which means fetching a page of somebody else's choosing and
	 * following the download link found on it.
	 */
	private static boolean isOfficial(URI uri) {
		String host = uri.getHost();

		return host != null && OFFICIAL_HOSTS.contains(host.toLowerCase(Locale.ROOT));
	}

	/**
	 * Every request this mod makes goes over TLS. The download is not pinned to a digest, so a plain-http
	 * source would be a way to hand the user a different jar than the one that was asked for.
	 */
	private static void requireHttps(URI uri) throws IOException {
		if (!"https".equalsIgnoreCase(uri.getScheme())) {
			throw new IOException("refusing " + uri + ": only https sources are supported");
		}
	}
	private static URI toUri(String url) throws IOException {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			throw new IOException("not a usable URL: " + url + " (" + e.getReason() + ")");
		}
	}

	/** The token-bearing link on an {@code adloadx} page, or null when the page does not carry one. */
	private static String findDownloadLink(String page) {
		Matcher matcher = DOWNLOAD_LINK.matcher(page);

		while (matcher.find()) {
			String link = matcher.group();

			if (link.contains("x=")) {
				return link.replace("&amp;", "&");
			}
		}

		return null;
	}

	private static byte[] get(URI uri, Progress progress) throws IOException {
	requireHttps(uri);
		HttpRequest request = HttpRequest.newBuilder(uri)
				.timeout(REQUEST_TIMEOUT)
				.header("User-Agent", USER_AGENT)
				.header("Accept", "*/*")
				.GET()
				.build();

		HttpResponse<InputStream> response;
		try {
			response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("interrupted while downloading " + uri, e);
		} catch (IllegalArgumentException e) {
			throw new IOException("refusing to fetch " + uri + ": " + e.getMessage(), e);
		} catch (IOException e) {
			// An unreachable host, a refused connection or a connect timeout arrives here; the URL is added
			// because a bare ConnectException has no message of its own to show the user.
			throw new IOException("could not reach " + uri + ": " + e, e);
		}

		try (InputStream in = response.body()) {
			if (response.statusCode() != 200) {
				throw new IOException("HTTP " + response.statusCode() + " from " + uri);
			}

			long total = response.headers().firstValueAsLong("content-length").orElse(-1L);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[16384];
			long received = 0;
			long lastReport = 0;
			long reported = -1;
			int read;

			while ((read = in.read(buffer)) > 0) {
				received += read;

				if (received > MAX_BYTES) {
					throw new IOException("the response from " + uri + " is larger than " + MAX_BYTES + " bytes, refusing it");
				}

				out.write(buffer, 0, read);

				long now = System.nanoTime();
				if (now - lastReport >= PROGRESS_INTERVAL_NANOS) {
					lastReport = now;
					reported = received;
					progress.bytes(received, total);
				}
			}

			if (received != reported) progress.bytes(received, total);

			return out.toByteArray();
		}
	}

	/**
	 * Rejects everything that is not an OptiFine jar: a response that is not a zip at all, a corrupt zip, and a
	 * zip without OptiFine's {@code Config.class}. Three layouts are accepted, because OptiFine has moved that
	 * class over the years and a 26.x jar carries it in two of the three at once:
	 * <ul>
	 *   <li>{@code net/optifine/Config.class} - F1 (1.14.2) to G9, where the jar is already extracted;</li>
	 *   <li>{@code notch/net/optifine/Config.class} - H1 (1.17.1) onwards, the installer/mapped layout;</li>
	 *   <li>{@code srg/net/optifine/Config.class} - the second mapped tree a modern jar ships.</li>
	 * </ul>
	 * That is the same set {@link OptifineVersion} reads when it decides whether a jar is OptiFine, and the
	 * 26.2 preview really does carry {@code notch/} and {@code srg/} while carrying no {@code net/optifine/}
	 * tree at all - accepting only the first layout would call OptiFine's own official jar "not OptiFine".
	 */
	public static void validate(byte[] bytes, String source) throws IOException {
		if (bytes.length < 4) {
			throw new IOException(source + " returned only " + bytes.length + " bytes, which cannot be a jar");
		}

		if (bytes[0] != 'P' || bytes[1] != 'K') {
			throw new IOException(source + " did not return a zip archive (it starts with " + preview(bytes) + ")");
		}

		try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
			ZipEntry entry;

			while ((entry = zip.getNextEntry()) != null) {
				String name = entry.getName();

				if (CONFIG_PLAIN.equals(name) || CONFIG_NOTCH.equals(name) || CONFIG_SRG.equals(name)) {
					System.out.println("[OptiFabric] " + source + " is an OptiFine jar (" + name + ")");

					return;
				}
			}
		} catch (ZipException | ZipError e) {
			throw new IOException(source + " returned a corrupt zip: " + e.getMessage(), e);
		}

		throw new IOException("the archive from " + source + " contains none of " + CONFIG_PLAIN + ", "
				+ CONFIG_NOTCH + " or " + CONFIG_SRG + ", so it is not an OptiFine jar");
	}

	/** Whether an existing file is an OptiFine jar, which is what makes a download unnecessary. */
	public static boolean isOptifineArchive(File file) {
		try {
			validate(read(file.toPath()), file.getName());

			return true;
		} catch (IOException e) {
			System.out.println("[OptiFabric] " + file + " is not a usable OptiFine jar (" + e.getMessage() + "), it will be replaced");

			return false;
		}
	}

	/** Writes the bytes into {@code targetDir} under the official file name, or says why it could not. */
	private static File write(byte[] bytes, File targetDir, String fileName) throws IOException {
		if (!targetDir.isDirectory() && !targetDir.mkdirs()) {
			throw new IOException("could not create the mods folder " + targetDir);
		}

		if (!targetDir.canWrite()) {
			throw new IOException("the mods folder " + targetDir + " is not writable");
		}

		File target = new File(targetDir, fileName);
		Path temporary = null;

		try {
			// Written beside the target first and moved into place, so a failure halfway can never leave a
			// truncated jar in mods/ - OptiFabric would then read it on the next launch and report it corrupt.
			temporary = Files.createTempFile(targetDir.toPath(), fileName + ".", ".part");
			Files.write(temporary, bytes);
			Files.move(temporary, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			if (temporary != null) {
				try {
					Files.deleteIfExists(temporary);
				} catch (IOException ignored) {
					// The reason we are here is the write itself; a leftover .part file is the lesser problem.
				}
			}

			throw new IOException("could not write " + target + ": " + e.getMessage(), e);
		}

		System.out.println("[OptiFabric] Saved " + bytes.length + " bytes to " + target);

		return target;
	}

	private static byte[] read(Path path) throws IOException {
		return Files.readAllBytes(path);
	}

	private static String sha256(byte[] bytes) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
			StringBuilder text = new StringBuilder(digest.length * 2);

			for (byte b : digest) {
				text.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
			}

			return text.toString();
		} catch (NoSuchAlgorithmException e) {
			return "(no SHA-256: " + e.getMessage() + ")";
		}
	}

	/** The first bytes of a response, escaped, so "it returned HTML" can be seen in the message. */
	private static String preview(byte[] bytes) {
		StringBuilder text = new StringBuilder();
		int count = Math.min(16, bytes.length);

		for (int i = 0; i < count; i++) {
			int b = bytes[i] & 0xFF;

			if (b >= 0x20 && b < 0x7F) {
				text.append((char) b);
			} else {
				text.append(String.format("\\x%02X", b));
			}
		}

		if (bytes.length > count) text.append("...");

		return "\"" + text + "\"";
	}

	/**
	 * Restarts the game with the command line this JVM was started with - {@link ProcessHandle} hands the
	 * command and its arguments over as a list, so nothing has to be split or re-quoted.
	 *
	 * @return false when the launcher hid the command line (some of them do), in which case the caller must
	 *         ask the user to restart instead. Never called for a failed download.
	 */
	public static boolean restart() {
		// Windows: the JVM cannot read its own command line back through ProcessHandle (a space in the game
		// directory makes the command line unparseable, so info().arguments()/commandLine() are empty). JNA reads
		// the raw command line from the OS and CreateProcessW re-executes it verbatim. Everywhere else the
		// ProcessHandle path below works, so JNA is only ever loaded on Windows.
		if (isWindows()) {
			return restartWindows();
		}

		ProcessHandle.Info info = ProcessHandle.current().info();
		Optional<String> command = info.command();
		Optional<String[]> arguments = info.arguments();

		if (command.isEmpty() || arguments.isEmpty() || arguments.get().length == 0) {
			System.out.println("[OptiFabric] Cannot restart automatically: this launcher does not expose the"
					+ " command line (command=" + command.orElse("?") + ", arguments=" + (arguments.isPresent() ? "present" : "hidden") + ")");

			return false;
		}

		List<String> commandLine = new ArrayList<>();
		commandLine.add(command.get());
		Collections.addAll(commandLine, arguments.get());

		try {
			// inheritIO keeps the launcher console and its log attached to the new process, which is what the
			// user is looking at when this happens.
			new ProcessBuilder(commandLine).inheritIO().start();
			System.out.println("[OptiFabric] Restarting the game: " + command.get()
					+ " with " + arguments.get().length + " argument(s)");

			return true;
		} catch (IOException | SecurityException e) {
			System.out.println("[OptiFabric] Could not restart the game: " + e);
			e.printStackTrace();

			return false;
		}
	}

	/** Whether this is a Windows JVM; only Windows takes the JNA path and only then is JNA loaded. */
	private static boolean isWindows() {
		String os = System.getProperty("os.name", "");
		return os != null && os.toLowerCase().startsWith("windows");
	}

	/**
	 * Windows-only relaunch: GetCommandLineW returns the exact command line the OS used to start this process
	 * (original quoting intact), and CreateProcessW with a null application name re-runs that command line. The
	 * command line is copied into a writable buffer because CreateProcessW may modify it in place.
	 */
	private static boolean restartWindows() {
		try {
			Pointer commandLine = Kernel32.INSTANCE.GetCommandLineW();
			if (commandLine == null) {
				System.out.println("[OptiFabric] Could not restart the game: GetCommandLineW returned null");

				return false;
			}

			String line = commandLine.getWideString(0);
			Memory buffer = new Memory(((long) line.length() + 1L) * Native.WCHAR_SIZE);
			buffer.setWideString(0, line);

			STARTUPINFO startupInfo = new STARTUPINFO();
			PROCESS_INFORMATION processInformation = new PROCESS_INFORMATION();
			boolean started = Kernel32.INSTANCE.CreateProcessW(null, buffer, null, null, false, 0, null, null,
					startupInfo, processInformation);

			System.out.println("[OptiFabric] Restarting the game (Windows, CreateProcessW " + started + ")");

			return started;
		} catch (Throwable t) {
			System.out.println("[OptiFabric] Could not restart the game (Windows/JNA): " + t);
			t.printStackTrace();

			return false;
		}
	}

	/** The two kernel32 entry points the Windows restart needs; loaded lazily so non-Windows never touches JNA. */
	private interface Kernel32 extends Library {
		Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

		Pointer GetCommandLineW();

		boolean CreateProcessW(Pointer lpApplicationName, Pointer lpCommandLine, Pointer lpProcessAttributes,
				Pointer lpThreadAttributes, boolean bInheritHandles, int dwCreationFlags, Pointer lpEnvironment,
				Pointer lpCurrentDirectory, STARTUPINFO lpStartupInfo, PROCESS_INFORMATION lpProcessInformation);
	}

	/**
	 * The downloader without the screen, driven by the {@value #TEST_PROPERTY} system property so it can be run
	 * from the game and from a bare JVM (see {@code test-downloads/OptiFineDownloadTest.java}).
	 *
	 * <p>Format: {@code <url>[,<file>][;<url>[,<file>]…]}, where {@code <file>} defaults to the support table's
	 * file name. Every test downloads into its own throwaway folder, so nothing lands in the game's
	 * {@code mods/}.
	 */
	public static void selfTest(String minecraftVersion, String spec) {
		OptifineSupport.Build build = OptifineSupport.forMc(minecraftVersion);

		System.out.println("[OptiFabric] selftest: Minecraft " + minecraftVersion + " expects "
				+ (build == null ? "(no support table entry)" : build.file));

		if (build == null) {
			System.out.println("[OptiFabric] selftest: nothing to test, this release has no table entry");

			return;
		}

		File root;
		try {
			root = Files.createTempDirectory("optifabric-selftest").toFile();
		} catch (IOException e) {
			System.out.println("[OptiFabric] selftest: could not create a work folder: " + e);

			return;
		}

		System.out.println("[OptiFabric] selftest: work folder " + root);

		int index = 0;

		for (String test : spec.split(";")) {
			if (test.isBlank()) continue;
			index++;

			String[] parts = test.split(",", -1);
			String url = parts[0].trim();
			String fileName = parts.length > 1 && !parts[1].isBlank() ? parts[1].trim() : build.file;
			int number = index;

			System.out.println("[OptiFabric] selftest " + number + ": source=" + url + " file=" + fileName);

			File folder = new File(root, "test" + number);

			try {
				Outcome outcome = fetch(url, build, folder, fileName, new Progress() {
					@Override
					public void stage(String key, String detail) {
						System.out.println("[OptiFabric] selftest " + number + ": stage " + key + " " + detail);
					}

					@Override
					public void bytes(long received, long total) {
						System.out.println("[OptiFabric] selftest " + number + ": bytes " + received
								+ (total > 0 ? "/" + total : ""));
					}
				});

				System.out.println("[OptiFabric] selftest " + number + " OK: " + outcome.file.getName()
						+ " (" + outcome.file.length() + " bytes) from " + outcome.source);
				System.out.println("[OptiFabric] selftest " + number + " sha256 " + outcome.sha256);
			} catch (Throwable t) {
				System.out.println("[OptiFabric] selftest " + number + " FAILED: " + t);
				System.out.println("[OptiFabric] selftest " + number + " no other source was tried"
						+ (folder.list() == null || folder.list().length == 0 ? ", nothing was written" : ", see " + folder));
			}
		}
	}

	/** Runs {@link #selfTest} when the JVM property is set; a no-op otherwise. */
	public static void selfTestFromProperty(String minecraftVersion) {
		String spec = System.getProperty(TEST_PROPERTY);

		if (spec == null || spec.isBlank()) return;

		selfTest(minecraftVersion, spec);
	}

	private static final class Payload {
		final byte[] bytes;
		final String source;

		Payload(byte[] bytes, String source) {
			this.bytes = bytes;
			this.source = source;
		}
	}

	// ------------------------------------------------------------------ the -full build's automatic download

	/**
	 * The GitHub-only {@code -full} behaviour, and the only reason that build exists: when this instance has no
	 * OptiFine, the official jar is fetched into its {@code mods/} folder, this JVM is replaced by a fresh
	 * launch of the same game, and the client never comes up without OptiFine.
	 *
	 * <p>Nothing about it is on a screen - the stages, the file and its SHA-256 go to the log - and it is not
	 * the platform-facing behaviour: the build submitted to CurseForge and Modrinth ships no
	 * {@code OptifineDownloader} class at all and asks the user to download OptiFine and put it in
	 * {@code mods/} themselves, which is the sentence that build's log line and release notes carry.
	 *
	 * @param minecraftVersion {@code FabricLoader.getRawGameVersion()} of the running game
	 * @param gameDir the running instance's game directory
	 * @return true when the missing jar was dealt with and this JVM is on its way out; false when the caller
	 *         should carry on and load OptiFine the normal way - which is also what every failure returns, so
	 *         the normal path reports it with its own message
	 */
	public static boolean downloadMissingOptifine(String minecraftVersion, File gameDir) {
		OptifineSupport.Build build = OptifineSupport.forMc(minecraftVersion);

		if (build == null) return false;
		if (hasOptifine(minecraftVersion, gameDir)) return false;

		File mods = new File(gameDir, "mods");
		System.out.println("[OptiFabric] No OptiFine in " + mods + ": this -full build downloads " + build.file
				+ " from " + OptifineSupport.OFFICIAL_DOWNLOAD_PAGE + " itself. The build submitted to CurseForge"
				+ " and Modrinth ships no downloader, starts no process and asks the user to fetch that jar by hand");

		Outcome outcome;

		try {
			outcome = download(build.officialUrl(), build, mods, new LogProgress());
		} catch (IOException e) {
			System.out.println("[OptiFabric] Could not download OptiFine: " + e.getMessage());
			System.out.println("[OptiFabric] Get " + build.file + " from " + OptifineSupport.OFFICIAL_DOWNLOAD_PAGE
					+ " yourself and put it in " + mods);
			return false;
		}

		System.out.println("[OptiFabric] OptiFine is in place: " + outcome.file + " (" + outcome.file.length()
				+ " bytes, " + (outcome.alreadyPresent ? "already there" : "downloaded") + ")");
		System.out.println("[OptiFabric] SHA-256 " + outcome.sha256);
		System.out.println("[OptiFabric] source " + outcome.source);

		if (!restart()) {
			System.out.println("[OptiFabric] Please start the game again by hand so it comes up with OptiFine");
			return false;
		}

		System.out.println("[OptiFabric] Restarted with the jar in place; stopping this JVM");
		System.out.flush();
		Runtime.getRuntime().halt(0);

		return true;
	}

	/** Whether this instance already has an OptiFine of its own: usable, or present but not usable. */
	private static boolean hasOptifine(String minecraftVersion, File gameDir) {
		try {
			if (!OptifineSearch.find(gameDir.toPath(), minecraftVersion).isEmpty()) return true;
		} catch (Throwable t) {
			// A jar that cannot be chosen (two copies, or two places offering the same build) is the normal
			// path's to report; downloading a second jar next to it is exactly what must not happen.
			return true;
		}

		return optifineJarInMods(gameDir);
	}

	/**
	 * Whether {@code mods/} already holds a jar that declares itself OptiFine. A jar for another Minecraft
	 * release and a corrupt one count: the normal path has a message for both, and downloading a second jar
	 * next to either would turn one problem into "you have 2 copies of OptiFine".
	 */
	private static boolean optifineJarInMods(File gameDir) {
		File[] entries = new File(gameDir, "mods").listFiles();

		if (entries == null) return false;

		for (File file : entries) {
			if (file.isDirectory() || file.isHidden() || file.getName().startsWith(".")) continue;
			if (!OptifineVersion.hasJarExtension(file.getName())) continue;

			OptifineVersion.JarType type;

			try {
				type = OptifineVersion.parseJarType(file).type;
			} catch (IOException e) {
				return true; // unreadable: leave it to the normal path rather than adding a jar beside it
			}

			if (type != OptifineVersion.JarType.SOMETHING_ELSE) return true;
		}

		return false;
	}

	/** The download's stages and its progress, one line each, so a log says what happened. */
	private static final class LogProgress implements Progress {
		private long lastMegabyte = -1;

		@Override
		public void stage(String key, String detail) {
			System.out.println("[OptiFabric] download " + key + ": " + detail);
		}

		@Override
		public void bytes(long received, long total) {
			long megabyte = received / (1024L * 1024L);

			if (megabyte == this.lastMegabyte) return;

			this.lastMegabyte = megabyte;
			System.out.println("[OptiFabric] download " + (received / 1024L) + " KiB"
					+ (total > 0 ? " of " + (total / 1024L) + " KiB" : ""));
		}
	}}
