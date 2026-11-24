package dev.brian.twerkgrow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SneakTrackerTest {
	private final UUID player = UUID.randomUUID();

	@Test
	void firesOnRisingEdgeOnly() {
		var t = new SneakTracker();
		assertFalse(t.update(player, false));
		assertTrue(t.update(player, true));
		assertFalse(t.update(player, true), "holding must not re-trigger");
		assertFalse(t.update(player, false));
		assertTrue(t.update(player, true), "each new press triggers");
	}

	@Test
	void sneakingAtFirstSightCountsAsPress() {
		assertTrue(new SneakTracker().update(player, true));
	}

	@Test
	void removeForgetsPlayer() {
		var t = new SneakTracker();
		t.update(player, true);
		t.remove(player);
		assertTrue(t.update(player, true));
		assertTrue(t.size() == 1);
		t.remove(player);
		assertTrue(t.size() == 0);
	}

	@Test
	void playersAreIndependent() {
		var t = new SneakTracker();
		var other = UUID.randomUUID();
		t.update(player, true);
		assertTrue(t.update(other, true));
	}
}
