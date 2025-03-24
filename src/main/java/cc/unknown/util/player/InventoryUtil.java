package cc.unknown.util.player;

import java.util.Arrays;
import java.util.List;

import cc.unknown.util.Accessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;

public class InventoryUtil implements Accessor {
	
	public final static List<Block> blacklist = Arrays.asList(Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,
			Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.red_flower, Blocks.yellow_flower, Blocks.flower_pot,
			Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.light_weighted_pressure_plate,
			Blocks.heavy_weighted_pressure_plate, Blocks.jukebox, Blocks.air, Blocks.iron_bars,
			Blocks.stained_glass_pane, Blocks.ladder, Blocks.glass_pane, Blocks.carpet, Blocks.enchanting_table,
			Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch,
			Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
			Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.iron_door, Blocks.dropper, Blocks.tnt,
			Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch, Blocks.oak_door);
	
	public static int findBlock() {
		int slot = -1;
		int highestStack = -1;
		for (int i = 0; i < 9; ++i) {
			final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
			if (itemStack != null && itemStack.getItem() instanceof ItemBlock
					&& blacklist.stream().noneMatch(block -> block.equals(((ItemBlock) itemStack.getItem()).getBlock()))
					&& itemStack.stackSize > 0) {
				if (mc.thePlayer.inventory.mainInventory[i].stackSize > highestStack) {
					highestStack = mc.thePlayer.inventory.mainInventory[i].stackSize;
					slot = i;
				}
			}
		}
		return slot;
	}
	
	public static boolean isSword() {
		ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
		if (stack == null) {
			return false;
		} else {
			return stack.getItem() instanceof ItemSword;
		}
	}

	public static int findTool(final BlockPos blockPos) {
		float bestSpeed = 1;
		int bestSlot = -1;

		final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

		for (int i = 0; i < 9; i++) {
			final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

			if (itemStack == null) {
				continue;
			}

			final float speed = itemStack.getStrVsBlock(blockState.getBlock());

			if (speed > bestSpeed) {
				bestSpeed = speed;
				bestSlot = i;
			}
		}

		return bestSlot;
	}
}
