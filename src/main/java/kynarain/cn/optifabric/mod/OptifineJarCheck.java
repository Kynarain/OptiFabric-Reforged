/*
 * New in the 1.21.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
package kynarain.cn.optifabric.mod;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipFile;

/**
 * Whether a file the user put in {@code mods/} is a usable OptiFine jar.
 *
 * <p>This is the "is it really OptiFine" half of the class that used to fetch OptiFine as well. Fetching is
 * gone: <b>OptiFabric downloads nothing at runtime</b> - no HTTP client, no URL fetch, no jar written from a
 * response (the platform's review asked for the runtime download to be removed, see CHANGELOG.md). The user
 * downloads OptiFine from the official site themselves; {@code 重新检查} then runs this check over the file
 * they put there.
 *
 * <p>Only the archive is opened, and only locally: the ten builds of this line keep
 * {@code net/optifine/Config.class} under {@code notch/}, the older 1.14 - 1.16 jars put it at
 * {@code net/optifine/Config.class} - the same pair {@link OptifineVersion} looks for when it decides whether
 * a jar is OptiFine, so both are accepted.
 */
public final class OptifineJarCheck {
	/** The class every OptiFine jar carries; this line's ten builds keep it under {@code notch/}. */
	public static final String CONFIG_PLAIN = "net/optifine/Config.class";
	public static final String CONFIG_NOTCH = "notch/net/optifine/Config.class";

	private OptifineJarCheck() {
	}

	/** The zip entry carrying OptiFine's {@code Config.class}, or null when there is none. */
	public static ZipEntry findConfig(ZipFile zip) {
		ZipEntry entry = zip.getEntry(CONFIG_PLAIN);

		if (entry == null) entry = zip.getEntry(CONFIG_NOTCH);

		return entry;
	}

	/** Whether {@code file} is a readable zip carrying OptiFine's {@code Config.class}. Never throws. */
	public static boolean isOptifineArchive(File file) {
		if (file == null || !file.isFile()) return false;

		try (ZipFile zip = new ZipFile(file)) {
			if (findConfig(zip) != null) {
				System.out.println("[OptiFabric] " + file + " is an OptiFine jar");

				return true;
			}

			System.out.println("[OptiFabric] " + file + " contains neither " + CONFIG_PLAIN + " nor "
					+ CONFIG_NOTCH + ", so it is not an OptiFine jar");

			return false;
		} catch (ZipError | IOException e) {
			System.out.println("[OptiFabric] " + file + " is not a usable OptiFine jar (" + e + ")");

			return false;
		}
	}
}
