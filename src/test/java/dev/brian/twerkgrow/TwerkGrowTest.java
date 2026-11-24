package dev.brian.twerkgrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TwerkGrowTest {
	@Test
	void idUsesModNamespace() {
		var id = TwerkGrow.id("example");
		assertEquals(TwerkGrow.MOD_ID, id.getNamespace());
		assertEquals("example", id.getPath());
	}

	@Test
	void langKeyFollowsMinecraftConvention() {
		assertEquals("item.twerk_grow.example", TwerkGrow.langKey("item", "example"));
	}
}
