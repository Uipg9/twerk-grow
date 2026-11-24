package dev.brian.twerkgrow;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Detects the moment a player starts sneaking (rising edge), per player. */
public final class SneakTracker {
	private final Set<UUID> sneaking = new HashSet<>();

	/** Returns true only on the tick the player begins sneaking. */
	public boolean update(UUID player, boolean isSneaking) {
		if (isSneaking) {
			return sneaking.add(player);
		}
		sneaking.remove(player);
		return false;
	}

	public void remove(UUID player) {
		sneaking.remove(player);
	}

	public int size() {
		return sneaking.size();
	}
}
