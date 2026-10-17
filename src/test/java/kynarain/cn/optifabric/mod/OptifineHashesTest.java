/*
 * Tests for the recorded OptiFine contents. The class exists to refuse a download that does not match what was
 * verified; these tests pin the refusing, which is the half that matters when the table is wrong or out of date.
 */

package kynarain.cn.optifabric.mod;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class OptifineHashesTest {
	private static OptifineSupport.Build build(String file) {
		return new OptifineSupport.Build("26.2", "HD_U_K2", "pre4", file, "test");
	}

	@Test
	void aFileThatIsNotRecordedMatchesNothing() {
		OptifineSupport.Build unknown = build("preview_OptiFine_26.2_HD_U_K9_pre9.jar");
		byte[] anything = "these are not the bytes of any OptiFine jar".getBytes(StandardCharsets.US_ASCII);

		assertFalse(OptifineHashes.matches(unknown, anything), "an unrecorded file must not be accepted");
	}

	@Test
	void aRecordedFileWithOtherContentsStillMatchesNothing() {
		OptifineSupport.Build recorded = build("preview_OptiFine_26.2_HD_U_K2_pre4.jar");
		byte[] other = "a jar that is not the one that was verified".getBytes(StandardCharsets.US_ASCII);

		assertFalse(OptifineHashes.matches(recorded, other), "different bytes must not match the recorded hash");
	}

	@Test
	void requireRefusesAndSaysWhichSideIsWhich() {
		OptifineSupport.Build recorded = build("preview_OptiFine_26.2_HD_U_K2_pre4.jar");
		byte[] other = "still not that jar".getBytes(StandardCharsets.US_ASCII);

		IOException failure = assertThrows(IOException.class, () -> OptifineHashes.require(recorded, other, "the test"));
		String message = failure.getMessage();

		assertTrue(message != null && message.contains(recorded.file), "the message must name the file: " + message);
		assertTrue(message.contains("SHA-256") || message.contains("hash"), "the message must say what was compared: " + message);
	}
}
