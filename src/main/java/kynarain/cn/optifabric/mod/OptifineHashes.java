/*
 * New in the 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt).
 */

package kynarain.cn.optifabric.mod;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The SHA-256 of every OptiFine build <b>this line</b> supports, and the check the downloader runs against
 * what it fetched. The hashes were computed from the files those builds are shipped as, not quoted.
 *
 * <p>One table per line, deliberately: a build name from another Minecraft version must never appear here,
 * because a table that does not name the build being downloaded refuses every download. One 1.21.x table was
 * once copied to all of them, which is why this file is generated per line now.
 *
 * <p>A build can have more than one accepted hash: OptiFine re-uploads builds. The check is "what arrived
 * matches one of the recorded contents", and a download that matches none is refused with both sides
 * printed rather than accepted silently.
 */
public final class OptifineHashes {
	private OptifineHashes() {
	}

	private static final Map<String, List<String>> ACCEPTED = Map.ofEntries(
			Map.entry("preview_OptiFine_1.20.6_HD_U_J1_pre18.jar", List.of("451b8ac78e291b720ecc5e5c125610c2feec0d2590d529ff1be65345054d50f4", "8721c3b11aee3e634e3e18c2209e3de1d6eea3a3c2110396112119c32226d517")));

	/** The recorded contents of the given build, or an empty list when nothing is recorded for it. */
	private static List<String> accepted(String file) {
		return ACCEPTED.getOrDefault(file, List.of());
	}

	/** Whether these bytes are one of the recorded contents of this build. Never throws. */
	public static boolean matches(OptifineSupport.Build build, byte[] bytes) {
		return accepted(build.file).contains(sha256(bytes));
	}

	/** Refuse the download unless its bytes are one of the recorded contents of this build. */
	public static void require(OptifineSupport.Build build, byte[] bytes, String source) throws IOException {
		List<String> accepted = accepted(build.file);
		String actual = sha256(bytes);

		if (accepted.contains(actual)) {
			System.out.println("[OptiFabric] " + build.file + " matches the recorded SHA-256 " + actual);
			return;
		}

		throw new IOException(source + " produced " + build.file + " with SHA-256 " + actual + ", which is not one of"
				+ " the recorded contents of that build (" + (accepted.isEmpty() ? "none recorded for this build" : String.join(", ", accepted)) + "); refusing it");
	}

	private static String sha256(byte[] bytes) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
			StringBuilder text = new StringBuilder(digest.length * 2);

			for (byte b : digest) {
				text.append(String.format(Locale.ROOT, "%02x", b));
			}

			return text.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No SHA-256 on this JVM", e);
		}
	}
}
