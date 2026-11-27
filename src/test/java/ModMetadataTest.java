import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Porting guard: the Minecraft range in fabric.mod.json must come from gradle.properties
 * (minecraft_dependency), so a version bump never needs a source edit.
 */
class ModMetadataTest {
	private static String modJson() throws IOException {
		try (InputStream in = ModMetadataTest.class.getResourceAsStream("/fabric.mod.json")) {
			assertNotNull(in, "fabric.mod.json missing from the test classpath");
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	@Test
	void allPlaceholdersAreExpanded() throws IOException {
		assertFalse(modJson().contains("${"), "fabric.mod.json still has an unexpanded ${...} placeholder");
	}

	@Test
	void minecraftRangeComesFromGradleProperties() throws IOException {
		String expected = System.getProperty("minecraft_dependency");
		assertNotNull(expected, "build.gradle must pass minecraft_dependency to the test JVM");
		Matcher m = Pattern.compile("\"minecraft\"\s*:\s*\"([^\"]*)\"").matcher(modJson());
		assertEquals(true, m.find(), "no \"minecraft\" dependency in fabric.mod.json");
		assertEquals(expected, m.group(1));
	}
}
