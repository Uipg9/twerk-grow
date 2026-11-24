package dev.brian.twerkgrow;

import java.util.Set;

/** Pure rules, kept free of Minecraft classes so they are unit-testable. */
public final class GrowthRules {
	public static final int DEFAULT_RADIUS = 5;
	public static final int MAX_RADIUS = 16;
	public static final int SUGAR_CANE_MAX_HEIGHT = 3;

	private static final Set<String> EXCLUDED = Set.of(
			"grass_block", "short_grass", "tall_grass", "fern", "large_fern",
			"short_dry_grass", "tall_dry_grass", "bush",
			"moss_block", "moss_carpet", "pale_moss_block", "pale_moss_carpet", "pale_hanging_moss",
			"crimson_nylium", "warped_nylium", "rooted_dirt", "azalea", "flowering_azalea", "glow_lichen");

	private GrowthRules() {}

	/** True for grass, moss, nylium and similar blocks that should never grow. */
	public static boolean isExcluded(String blockPath) {
		return EXCLUDED.contains(blockPath);
	}

	/** Sugar cane may extend if its column is shorter than the vanilla cap and the space above is air. */
	public static boolean canGrowSugarCane(int columnHeight, boolean airAbove) {
		return airAbove && columnHeight < SUGAR_CANE_MAX_HEIGHT;
	}

	public static int clampRadius(int radius) {
		return Math.max(1, Math.min(MAX_RADIUS, radius));
	}
}
