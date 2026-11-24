package dev.brian.twerkgrow.client;

import dev.brian.twerkgrow.TwerkGrow;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Plants wheat, sugar cane, short grass and a poppy next to the player, sneaks with the real
 * sneak key, then asserts crops grew and grass/flowers were left alone.
 * Run with {@code .\gradlew.bat runClientGameTest}.
 */
public class TwerkGrowClientGameTest implements FabricClientGameTest {
	private static BlockState stateAt(TestServerContext server, BlockPos pos) {
		return server.computeOnServer(s -> s.overworld().getBlockState(pos));
	}

	private static void cmd(TestServerContext server, String fmt, Object... args) {
		server.runCommand(String.format(fmt, args));
	}

	@Override
	public void runTest(ClientGameTestContext context) {
		if (!FabricLoader.getInstance().isModLoaded(TwerkGrow.MOD_ID)) {
			throw new AssertionError(TwerkGrow.MOD_ID + " is not loaded");
		}

		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);
			TestServerContext server = world.getServer();
			BlockPos p = server.computeOnServer(s -> s.getPlayerList().getPlayers().get(0).blockPosition());
			int x = p.getX(), y = p.getY(), z = p.getZ();

			// Flat dirt platform with clear air above, then the test plants.
			cmd(server, "fill %d %d %d %d %d %d dirt", x + 1, y - 1, z - 3, x + 6, y - 1, z + 3);
			cmd(server, "fill %d %d %d %d %d %d air", x + 1, y, z - 3, x + 6, y + 5, z + 3);
			cmd(server, "setblock %d %d %d farmland", x + 2, y - 1, z);
			cmd(server, "setblock %d %d %d wheat[age=0]", x + 2, y, z);
			cmd(server, "setblock %d %d %d sand", x + 4, y - 1, z);
			cmd(server, "setblock %d %d %d water", x + 5, y - 1, z);
			cmd(server, "setblock %d %d %d sugar_cane", x + 4, y, z);
			cmd(server, "setblock %d %d %d short_grass", x + 2, y, z + 2);
			cmd(server, "setblock %d %d %d poppy", x + 2, y, z - 2);
			context.waitTicks(5);
			context.takeScreenshot("before_sneak");

			// Real input: press the sneak key (rising edge triggers the growth).
			context.getInput().holdKeyFor(options -> options.keyShift, 8);
			context.waitTicks(10);
			context.takeScreenshot("after_sneak");

			BlockState wheat = stateAt(server, new BlockPos(x + 2, y, z));
			if (!wheat.is(Blocks.WHEAT) || wheat.getValue(CropBlock.AGE) == 0) {
				throw new AssertionError("wheat did not grow after sneaking: " + wheat);
			}
			if (!stateAt(server, new BlockPos(x + 4, y + 1, z)).is(Blocks.SUGAR_CANE)) {
				throw new AssertionError("sugar cane did not grow taller after sneaking");
			}
			if (!stateAt(server, new BlockPos(x + 2, y, z + 2)).is(Blocks.SHORT_GRASS)) {
				throw new AssertionError("short grass must be left alone");
			}
			if (!stateAt(server, new BlockPos(x + 2, y, z - 2)).is(Blocks.POPPY)) {
				throw new AssertionError("poppy must be left alone");
			}

		}
	}
}
