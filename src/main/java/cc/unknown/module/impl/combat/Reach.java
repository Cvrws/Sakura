package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.ClickMouseEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "Reach", category = Category.COMBAT)
public class Reach extends Module {
	
	private final SliderValue range = new SliderValue("Range", this, 3, 0.1f, 6, 0.01f);
    private final SliderValue chance = new SliderValue("Chance", this, 0.9f, 0f, 1f, 0.1f);
	
	private final SliderValue tickDelay = new SliderValue("TickDelay", this, 2, 0, 10, () -> this.conditionals.isEnabled("TicksDelay"));
	
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("OnlyWeapon", false),
			new BoolValue("OnlyMove", false),
			new BoolValue("OnlySprint", false),
			new BoolValue("OnlySpeedPotion", false),
			new BoolValue("ThroughWalls", true),
			new BoolValue("ComboMode", false),
			new BoolValue("TradeMode", false), 
			new BoolValue("TapMode", false), 
			new BoolValue("WaterCheck", true),
			new BoolValue("TicksDelay", false)));
	
	private int ticks = 0;
	
    @Override
    public void onEnable() {
        ticks = 0;
    }

    @Override
    public void onDisable() {
        ticks = 0;
    }
    
    @Kisoji
    public final Listener<TickForgeEvent> onPreTick = event -> {
        if (event.isPost()) return;
        ticks++;
    };
	
	@Kisoji
	public final Listener<ClickMouseEvent> onMouse = event -> {
		AutoClicker clicker = getModule(AutoClicker.class);
		if (isInGame() && event.getButton() == 0 && (!clicker.isEnabled() || !Mouse.isButtonDown(0)) || isClicking()) {
			callReach();
		}
	};
	
	private boolean callReach() {
	    if (!isInGame()) return false;

	    if ((conditionals.isEnabled("Onlymove") && MoveUtil.isMoving()) ||
	        (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) ||
	        (conditionals.isEnabled("OnlySprint") && !mc.thePlayer.isSprinting()) ||
	        (conditionals.isEnabled("OnlySpeedPotion") && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) ||
	        (!(chance.getValue() == 1.0 || Math.random() <= chance.getValue())) ||
	        (conditionals.isEnabled("TicksDelay") && ticks > tickDelay.getValue()) ||
	        (conditionals.isEnabled("TradeMode") && (mc.thePlayer.hurtResistantTime > 0 || !mc.thePlayer.onGround)) ||
	        (conditionals.isEnabled("ComboMode") && (!(mc.thePlayer.hurtResistantTime > 0) && MoveUtil.isMoving())) ||
	        (conditionals.isEnabled("TapMode") && mc.thePlayer.moveForward == 0) ||
	        (conditionals.isEnabled("WaterCheck") && mc.thePlayer.isInWater())) {
	        ticks = 0;
	        return false;
	    }
	    
	    if (!conditionals.isEnabled("ThroughWalls") && mc.objectMouseOver != null) {
	    	BlockPos p = mc.objectMouseOver.getBlockPos();
	    	if (p != null && mc.theWorld.getBlockState(p).getBlock() != Blocks.air) {
	    		return false;
	    	}
	    }

	    Object[] object = findEntitiesWithinReach(range.getValue());
	    if (object == null) return false; 
	    
	    mc.objectMouseOver = new MovingObjectPosition((Entity) object[0], (Vec3) object[1]);
	    mc.pointedEntity = (Entity) object[0];
	    return true;
	}

	private Object[] findEntitiesWithinReach(double reach) {
		if (!this.isEnabled()) {
			reach = mc.playerController.extendedReach() ? 6.0D : 3.0D;
		}

		Entity renderView = mc.getRenderViewEntity();
		Entity target = null;
		if (renderView == null) {
			return null;
		} else {
			mc.mcProfiler.startSection("pick");
			Vec3 eyePosition = renderView.getPositionEyes(1.0F);
			Vec3 playerLook = renderView.getLook(1.0F);
			Vec3 reachTarget = eyePosition.addVector(playerLook.xCoord * reach, playerLook.yCoord * reach,
					playerLook.zCoord * reach);
			Vec3 targetHitVec = null;
			List<Entity> targetsWithinReach = mc.theWorld.getEntitiesWithinAABBExcludingEntity(renderView,
					renderView.getEntityBoundingBox()
							.addCoord(playerLook.xCoord * reach, playerLook.yCoord * reach, playerLook.zCoord * reach)
							.expand(1.0D, 1.0D, 1.0D));
			double adjustedReach = reach;

			for (Entity entity : targetsWithinReach) {
				if (entity.canBeCollidedWith()) {
					float ex = (float) ((double) entity.getCollisionBorderSize());
					AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(ex, ex, ex);
					MovingObjectPosition targetPosition = entityBoundingBox.calculateIntercept(eyePosition,
							reachTarget);
					if (entityBoundingBox.isVecInside(eyePosition)) {
						if (0.0D < adjustedReach || adjustedReach == 0.0D) {
							target = entity;
							targetHitVec = targetPosition == null ? eyePosition : targetPosition.hitVec;
							adjustedReach = 0.0D;
						}
					} else if (targetPosition != null) {
						double distanceToVec = eyePosition.distanceTo(targetPosition.hitVec);
						if (distanceToVec < adjustedReach || adjustedReach == 0.0D) {
							if (entity == renderView.ridingEntity) {
								if (adjustedReach == 0.0D) {
									target = entity;
									targetHitVec = targetPosition.hitVec;
								}
							} else {
								target = entity;
								targetHitVec = targetPosition.hitVec;
								adjustedReach = distanceToVec;
							}
						}
					}
				}
			}

			if (adjustedReach < reach && !(target instanceof EntityLivingBase)
					&& !(target instanceof EntityItemFrame)) {
				target = null;
			}

			mc.mcProfiler.endSection();
			if (target != null && targetHitVec != null) {
				return new Object[] { target, targetHitVec };
			} else {
				return null;
			}
		}
	}
	
	private boolean isClicking() {
		AutoClicker clicker = getModule(AutoClicker.class);
		if (clicker != null && clicker.isEnabled()) {
			return clicker.isEnabled() && (Mouse.isButtonDown(0) || mc.gameSettings.keyBindAttack.isKeyDown());
		}
		return false;
	}
}
