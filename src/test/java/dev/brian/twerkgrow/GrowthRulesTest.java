package dev.brian.twerkgrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GrowthRulesTest {
	@Test
	void excludesGrassAndGroundCover() {
		for (var id : new String[] {"grass_block", "short_grass", "tall_grass", "fern", "large_fern",
				"moss_block", "moss_carpet", "crimson_nylium", "warped_nylium", "glow_lichen"}) {
			assertTrue(GrowthRules.isExcluded(id), id);
		}
	}

	@Test
	void keepsCropsSaplingsAndSugarcane() {
		for (var id : new String[] {"wheat", "carrots", "potatoes", "beetroots", "oak_sapling",
				"melon_stem", "pumpkin_stem", "sweet_berry_bush", "cocoa", "sugar_cane", "bamboo_sapling"}) {
			assertFalse(GrowthRules.isExcluded(id), id);
		}
	}

	@Test
	void sugarCaneGrowsUnderHeightThree() {
		assertTrue(GrowthRules.canGrowSugarCane(1, true));
		assertTrue(GrowthRules.canGrowSugarCane(2, true));
		assertFalse(GrowthRules.canGrowSugarCane(3, true));
		assertFalse(GrowthRules.canGrowSugarCane(1, false), "needs air above");
	}

	@Test
	void radiusIsClampedToSaneRange() {
		assertEquals(5, GrowthRules.DEFAULT_RADIUS);
		assertEquals(1, GrowthRules.clampRadius(0));
		assertEquals(16, GrowthRules.clampRadius(99));
		assertEquals(7, GrowthRules.clampRadius(7));
	}
}
