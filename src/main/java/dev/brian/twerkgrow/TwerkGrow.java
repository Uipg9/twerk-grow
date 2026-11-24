package dev.brian.twerkgrow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sneak to bonemeal nearby crops, saplings and sugar cane. Idea credit: Sneak Fertilizing (MIT).
 */
public class TwerkGrow implements ModInitializer {
	public static final String MOD_ID = "twerk_grow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final int VERTICAL_RANGE = 3;
	private static final int LEVEL_EVENT_BONEMEAL = 1505;
	private final SneakTracker tracker = new SneakTracker();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				boolean sneaking = player.isShiftKeyDown() && !player.isSpectator();
				if (tracker.update(player.getUUID(), sneaking)) {
					growAround(player);
				}
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> tracker.remove(handler.getPlayer().getUUID()));
	}

	private static void growAround(ServerPlayer player) {
		ServerLevel level = player.level();
		BlockPos center = player.blockPosition();
		int r = GrowthRules.DEFAULT_RADIUS;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -VERTICAL_RANGE, -r), center.offset(r, VERTICAL_RANGE, r))) {
			BlockState state = level.getBlockState(pos);
			if (state.isAir()) continue;
			String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
			if (GrowthRules.isExcluded(path) || state.is(BlockTags.FLOWERS)) continue;
			if (state.is(Blocks.SUGAR_CANE)) {
				growSugarCane(level, pos.immutable());
			} else if (state.getBlock() instanceof BonemealableBlock b
					&& b.getType() == BonemealableBlock.Type.GROWER
					&& b.isValidBonemealTarget(level, pos, state, BonemealSource.INTERACTION)
					&& b.isBonemealSuccess(level, level.getRandom(), pos, state, BonemealSource.INTERACTION)) {
				b.performBonemeal(level, level.getRandom(), pos, state, BonemealSource.INTERACTION);
				level.levelEvent(LEVEL_EVENT_BONEMEAL, pos, 0);
			}
		}
	}

	private static void growSugarCane(ServerLevel level, BlockPos pos) {
		if (level.getBlockState(pos.above()).is(Blocks.SUGAR_CANE)) return; // only act on the column top
		int height = 1;
		while (level.getBlockState(pos.below(height)).is(Blocks.SUGAR_CANE)) height++;
		BlockPos above = pos.above();
		if (GrowthRules.canGrowSugarCane(height, level.getBlockState(above).isAir())
				&& Blocks.SUGAR_CANE.defaultBlockState().canSurvive(level, above)) {
			level.setBlockAndUpdate(above, Blocks.SUGAR_CANE.defaultBlockState());
			level.levelEvent(LEVEL_EVENT_BONEMEAL, above, 0);
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	/** Translation key following the vanilla convention: {@code category.modid.path}. */
	public static String langKey(String category, String path) {
		return category + "." + MOD_ID + "." + path;
	}
}
