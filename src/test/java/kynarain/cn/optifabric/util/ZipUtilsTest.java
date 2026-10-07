/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * The zip helpers are the only part of the patcher that touches the filesystem directly, and the entry-name
 * check in {@link ZipUtils#extract} is the one place where a hostile archive could write outside the output
 * directory. These tests keep that check honest without needing Minecraft: everything here is plain java.util.zip.
 */
class ZipUtilsTest {
	private static File zip(Path dir, String name, String... entries) throws IOException {
		File file = dir.resolve(name).toFile();

		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(file.toPath()))) {
			for (String entry : entries) {
				boolean directory = entry.endsWith("/");
				out.putNextEntry(new ZipEntry(entry));

				if (!directory) {
					out.write(("content of " + entry).getBytes(StandardCharsets.UTF_8));
				}

				out.closeEntry();
			}
		}

		return file;
	}

	@Test
	void aRealZipIsValidAndSomethingElseIsNot(@TempDir Path dir) throws IOException {
		File good = zip(dir, "good.zip", "a.txt");

		assertTrue(ZipUtils.isValid(good), "a zip written by ZipOutputStream has to be readable");

		File bad = dir.resolve("bad.zip").toFile();
		Files.write(bad.toPath(), "this is not a zip".getBytes(StandardCharsets.UTF_8));

		assertFalse(ZipUtils.isValid(bad), "a file that is not a zip has to be rejected, not thrown out of");
	}

	@Test
	void extractWritesEveryEntryIncludingNestedDirectories(@TempDir Path dir) throws IOException {
		File archive = zip(dir, "tree.zip", "top.txt", "sub/", "sub/nested.txt");
		File out = dir.resolve("out").toFile();

		ZipUtils.extract(archive, out);

		assertEquals("content of top.txt", Files.readString(new File(out, "top.txt").toPath()));
		assertEquals("content of sub/nested.txt", Files.readString(new File(out, "sub/nested.txt").toPath()));
	}

	@Test
	void extractRefusesAnEntryThatTriesToLeaveTheOutputDirectory(@TempDir Path dir) throws IOException {
		File archive = zip(dir, "escape.zip", "../escaped.txt");
		File out = dir.resolve("out").toFile();
		File escaped = dir.resolve("escaped.txt").toFile();

		SecurityException thrown = assertThrows(SecurityException.class, () -> ZipUtils.extract(archive, out), "an entry named ../escaped.txt has to be refused");

		assertTrue(thrown.getMessage().contains("escaped.txt"), "the message has to name the entry that was refused");
		assertFalse(escaped.exists(), "nothing may be written outside the output directory");
	}

	@Test
	void iterateContentsStopsAsSoonAsTheVisitorSaysSo(@TempDir Path dir) throws IOException {
		File archive = zip(dir, "many.zip", "1.txt", "2.txt", "3.txt");
		List<String> seen = new ArrayList<>();

		ZipUtils.iterateContents(archive, (zipFile, entry) -> {
			seen.add(entry.getName());
			return seen.size() < 2;
		});

		assertEquals(List.of("1.txt", "2.txt"), seen, "returning false has to end the walk immediately");
	}

	@Test
	void filterKeepsOnlyTheEntriesTheVisitorAccepts(@TempDir Path dir) throws IOException {
		File archive = zip(dir, "in.zip", "keep.txt", "drop.txt");
		File out = dir.resolve("out.zip").toFile();

		ZipUtils.filter(archive, (zipFile, entry) -> entry.getName().startsWith("keep"), out);

		try (ZipFile result = new ZipFile(out)) {
			List<String> names = new ArrayList<>();
			result.entries().asIterator().forEachRemaining(e -> names.add(e.getName()));

			assertEquals(List.of("keep.txt"), names);
		}
	}
}
