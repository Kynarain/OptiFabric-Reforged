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
			Map.entry("OptiFine_1.21.1_HD_U_J1.jar", List.of("db6d2d14db0009bdea2d8f848e5cb55aac52b555e8e50506dbf7876755b79082")),
			Map.entry("OptiFine_1.21.11_HD_U_J9.jar", List.of("63a60c48b3370920e96d4c32570d7154d17b3a86654c4f1d1df418be668d3903", "f97b5d06df53760e21c5a91733ac55af5a250caca2c29945c855aef7ed2d0ec8")),
			Map.entry("OptiFine_1.21.3_HD_U_J2.jar", List.of("3916aadff6fd8814f93d5ca0fddf4921f67772737b1208099b1debaeb671667b")),
			Map.entry("OptiFine_1.21.4_HD_U_J3.jar", List.of("db8a1c508ecb3f89ae8f7b69ae25c78cbdb3d31131c4f789ffae50e2047be1d3")),
			Map.entry("preview_OptiFine_1.21.10_HD_U_J7_pre11.jar", List.of("bf845cfc6a387b0cc879512caefa86039fd5ca9e37aa6828a1967577ae96b6d7")),
			Map.entry("preview_OptiFine_1.21.6_HD_U_J6_pre3.jar", List.of("f73e5b90a89e523873b5b876cbd7601f08eface74879e8dd528bd25328ef5c7a")),
			Map.entry("preview_OptiFine_1.21.7_HD_U_J6_pre7.jar", List.of("4e7340eb61b39f100da639e62490e50432ba34380f96bbea822336dbef157e6c")),
			Map.entry("preview_OptiFine_1.21.8_HD_U_J6_pre16.jar", List.of("ffe496b39065fedc897dc179965f9a21f01ab1f816be973e496d94c9617d462c")),
			Map.entry("preview_OptiFine_1.21.9_HD_U_J7_pre2.jar", List.of("dc7d1182360c2b23b0c96797103ef1946fae6d1bda78ec2640117df3f368f4c4")),
			Map.entry("preview_OptiFine_1.21_HD_U_J1_pre9.jar", List.of("b26b22478592ca85c3ea829784c3b7bf0d98d882d18a937af969dcc14372d37f")));

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
