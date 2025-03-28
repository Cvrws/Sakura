package cc.unknown.util.player;

import cc.unknown.handlers.CPSHandler;
import cc.unknown.util.Accessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil implements Accessor {

    public static Item getItem() {
        ItemStack stack = getItemStack();
        return stack == null ? null : stack.getItem();
    }
    
    public static ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(mc.thePlayer.inventory.currentItem + 36).getStack());
    }
    
    public static boolean insideBlock() {
		if (mc.thePlayer.ticksExisted < 5) {
			return false;
		}

		final EntityPlayerSP player = mc.thePlayer;
		final WorldClient world = mc.theWorld;
		final AxisAlignedBB bb = player.getEntityBoundingBox();
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir)
							&& (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z),
									world.getBlockState(new BlockPos(x, y, z)))) != null
							&& player.getEntityBoundingBox().intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}
    
	public static boolean isTeam(EntityPlayer player, boolean scoreboard, boolean checkColor) {
		String entityName = player.getDisplayName().getUnformattedText();
		String playerName = mc.thePlayer.getDisplayName().getUnformattedText();

		if (entityName.length() >= 3 && playerName.startsWith(entityName.substring(0, 3))) {
			return true;
		}

		if (mc.thePlayer.isOnSameTeam(player)) {
			return true;
		}
		
		if (unusedNames(player)) {
			return false;
		}

		if (mc.thePlayer.getTeam() != null && player.getTeam() != null && mc.thePlayer.getTeam().isSameTeam(player.getTeam())) {
			return true;
		}

		if (playerName != null && player.getDisplayName() != null) {
			String targetName = player.getDisplayName().getFormattedText().replace("§r", "");
			String clientName = playerName.replace("§r", "");
			return targetName.startsWith("§" + clientName.charAt(1));
		}

		return false;
	}
	
    public static boolean unusedNames(EntityPlayer player) {
    	String name = player.getName();
    	return name.contains("CLICK DERECHO") || name.contains("MEJORAS") || name.contains("[NPC]") || name.contains("[SHOP]") || name.contains("CLIQUE PARA ABRIR");
    }
    
    public static void leftClick(boolean state) {
    	setState(mc.gameSettings.keyBindAttack.getKeyCode(), state);
    	if (!mc.thePlayer.isBlocking()) CPSHandler.addLeftClicks();
    }
    
    public static void rightClick(boolean state) {
    	setState(mc.gameSettings.keyBindUseItem.getKeyCode(), state);
    	if (!mc.thePlayer.isBlocking()) CPSHandler.addRightClicks();
    }
    
    public static void jump(boolean state) {
    	setState(mc.gameSettings.keyBindJump.getKeyCode(), state);
    }
    
    public static void setShift(boolean state) {
    	setState(mc.gameSettings.keyBindSneak.getKeyCode(), state);
    }
    
	private static void setState(int keycode, boolean state) {
		KeyBinding.setKeyBindState(keycode, state);
		KeyBinding.onTick(keycode);
	}
	
	public static boolean inRange(Entity target, double range) {
		if (target != null && !target.isDead && !target.isInvisibleToPlayer(mc.thePlayer)) {
			return (double) mc.thePlayer.getDistanceToEntity(target) < range;
		} else {
			return false;
		}
	}

	public static boolean isInFov(Entity target, float fov) {
		fov = (float) ((double) fov * 0.5);
		double v = ((double) (mc.thePlayer.rotationYaw - fovToEntity(target)) % 360.0 + 540.0) % 360.0 - 180.0;
		return v > 0.0 && v < (double) fov || (double) (-fov) < v && v < 0.0;
	}

	private static float fovToEntity(Entity target) {
		double x = target.posX - mc.thePlayer.posX;
		double z = target.posZ - mc.thePlayer.posZ;
		double w = Math.atan2(x, z) * 57.2957795;
		return (float) (w * -1.0);
	}

	public static Block getBlock(double x, double y, double z) {
		return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
	}
}
