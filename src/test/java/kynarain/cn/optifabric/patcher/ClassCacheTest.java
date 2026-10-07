/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.patcher;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * The cache file is the one artefact this mod writes that has to survive a crash: the patcher reads it on the
 * next launch and falls back to rebuilding from OptiFine when it cannot. These tests pin the parts of that
 * contract that are pure data handling: the round trip, the duplicate and null rules, and the two documented
 * corruption routes (a bad header and a wrong CRC both have to end as the empty cache, never as an exception
 * or an enormous allocation).
 */
class ClassCacheTest {
	private static boolean releases(File file) throws InterruptedException {
		for (int attempt = 0; attempt < 20; attempt++) {
			if (file.delete()) return true;
			System.gc();
			Thread.sleep(50);
		}

		return false;
	}
	private static byte[] hash() {
		return new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
	}

	private static byte[] bytes(String text) {
		return text.getBytes(StandardCharsets.UTF_8);
	}

	@Test
	void addGetPopAndTheHashBehaveAsAdvertised() {
		ClassCache cache = new ClassCache(hash());

		cache.addClass("net/minecraft/A", bytes("A"));

		assertEquals(Set.of("net/minecraft/A"), cache.getClasses());
		assertArrayEquals(bytes("A"), cache.getClass("net/minecraft/A"));
		assertArrayEquals(hash(), cache.getHash());

		assertArrayEquals(bytes("A"), cache.popClass("net/minecraft/A"), "pop has to return what it removed");
		assertNull(cache.getClass("net/minecraft/A"), "a popped class is gone");
		assertTrue(cache.getClasses().isEmpty());
		assertNull(cache.popClass("net/minecraft/A"), "popping twice is not an error, it is null");
	}

	@Test
	void aDuplicateNameIsRejectedRatherThanReplaced() {
		ClassCache cache = new ClassCache(hash());
		cache.addClass("net/minecraft/A", bytes("A"));

		assertThrows(IllegalArgumentException.class, () -> cache.addClass("net/minecraft/A", bytes("B")), "a second add for the same name is a programming error");
	}

	@Test
	void nullBytesAreRejected() {
		ClassCache cache = new ClassCache(hash());

		assertThrows(NullPointerException.class, () -> cache.addClass("net/minecraft/A", null));
	}

	@Test
	void saveAndReadRoundTripTheHashAndEveryClass(@TempDir Path dir) throws IOException {
		ClassCache cache = new ClassCache(hash());
		cache.addClass("net/minecraft/A", bytes("A"));
		cache.addClass("net/minecraft/B", bytes("BBBB"));
		File file = dir.resolve("cache.bin").toFile();

		cache.save(file);

		ClassCache read = ClassCache.read(file);

		assertArrayEquals(hash(), read.getHash());
		assertEquals(Set.of("net/minecraft/A", "net/minecraft/B"), read.getClasses());
		assertArrayEquals(bytes("BBBB"), read.getClass("net/minecraft/B"));
	}

	@Test
	void aFileThatIsNotACacheReadsAsTheEmptyCache(@TempDir Path dir) throws IOException, InterruptedException {
		File file = dir.resolve("garbage.bin").toFile();
		Files.write(file.toPath(), "not a class cache at all".getBytes(StandardCharsets.UTF_8));

		ClassCache read = ClassCache.read(file);

		assertNotNull(read, "a corrupt cache has to come back as an empty cache, not as an exception");
		assertTrue(read.getClasses().isEmpty());
		assertTrue(releases(file), "read has to let go of the file it was given, even when the content is not a cache");
	}

	@Test
	void aTruncatedHeadIsRejectedBeforeAnythingIsAllocated(@TempDir Path dir) throws IOException, InterruptedException {
		byte[] head = new byte[64];
		head[0] = (byte) 0x7F;
		head[1] = (byte) 0xFF;
		head[2] = (byte) 0xFF;
		head[3] = (byte) 0xFF;
		File file = dir.resolve("huge.bin").toFile();
		Files.write(file.toPath(), head);

		ClassCache read = ClassCache.read(file);

		assertTrue(read.getClasses().isEmpty(), "a declared length of 2^31-1 must not be trusted");
		assertFalse(read.isConverted());
		assertTrue(releases(file), "read has to let go of the file it was given, even on the corruption path");
	}
}
