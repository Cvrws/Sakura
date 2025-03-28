package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.EnemyUtil;
import cc.unknown.util.client.FriendUtil;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.RotationUtil;
import cc.unknown.util.structure.Vector2f;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@SuppressWarnings("rawtypes")
@ModuleInfo(name = "AimAssist", category = Category.COMBAT)
public class AimAssist extends Module {

	private final ModeValue priority = new ModeValue("Priority", this, "Distance", "Distance", "BestVectorPosition", "Nearest", "BestVector");
	
	private final SliderValue speedYaw = new SliderValue("HorizontalSpeed", this, 1.2f, 1.0f, 20f, 0.01f);
	private final SliderValue speedYaw2 = new SliderValue("HorizontalMult", this, 1, 0, 40);
	private final BoolValue aimPitch = new BoolValue("Vertical", this, true);
	private final SliderValue speedPitch = new SliderValue("VerticalSpeed", this, 1.2f, 0.01f, 20f, 0.01f, aimPitch::get);
	private final SliderValue speedPitch2 = new SliderValue("VerticalMult", this, 1, 0, 40, aimPitch::get);
	private final SliderValue fovRange = new SliderValue("Angle", this, 75, 15, 360, 5);
	private final SliderValue range = new SliderValue("Range", this, 3.4f, 1.0f, 6.0f, 0.05f);

	private final BoolValue randomization = new BoolValue("Randomization", this, false);
	private final SliderValue randomYaw = new SliderValue("RandomYaw", this, 6.5f, 1.0f, 20.0f, 0.5f, randomization::get);
	private final SliderValue randomPitch = new SliderValue("RandomPitch", this, 14.5F, 1.5F, 40.5F, 0.5F, randomization::get);
	
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("OnlyWeapons", false),
			new BoolValue("IgnoreInvisibles", true),
			new BoolValue("IgnoreFriends", true),
			new BoolValue("LockTarget", true),
			new BoolValue("IncreaseStrafe", false),
			new BoolValue("Smoothness", false),
			new BoolValue("MouseOverEntity", false),
			new BoolValue("MultiPoint", false),
			new BoolValue("VisibilityCheck", false), 
			new BoolValue("RequireClicking", true), 
			new BoolValue("BreakBlocks", false)));
	
	private final Set<EntityPlayer> lockedTargets = new HashSet<>();
	public EntityPlayer target;
	
	@Override
	public void onDisable() {
		target = null;
	}

	@Kisoji
	public final Listener<MotionEvent.Pre> onPreMotion = event -> {		
	    if (noAim()) {
	        return;
	    }
	    
        if (!conditionals.isEnabled("LockTarget") || target == null || !onTarget()) {
            target = getTarget();
        }
        
	    if (target == null) {
	        return;
	    }

		Vector2f rotations = getRotations(target, event.getYaw(), event.getPitch());

		if (conditionals.isEnabled("IncreaseStrafe")) {
			if (mc.thePlayer.moveStrafing != 0) {
				float value = 0.5f;
				rotations.x += value;
			}
		}
		
		if (onTarget(target)) {
			if (aimPitch.get()) mc.thePlayer.rotationPitch = rotations.y;
			mc.thePlayer.rotationYaw = rotations.x;
		} else {	
			if (aimPitch.get()) mc.thePlayer.rotationPitch = rotations.y;
			mc.thePlayer.rotationYaw = rotations.x;
		}
	};
	
	@Kisoji
    public final Listener<PacketEvent> onPacket = event -> {
		Packet packet = event.getPacket();
    	if (event.isIncoming()) return;
    	
    	if (packet instanceof C02PacketUseEntity) {
    		C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;
    		if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
    	        if (conditionals.isEnabled("LockTarget") && wrapper.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer) {
    	            EntityPlayer attackedTarget = (EntityPlayer) wrapper.getEntityFromWorld(mc.theWorld);
    	            lockedTargets.add(attackedTarget);
    	            if (target == null && isValidTarget(attackedTarget, new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), (int) fovRange.getValue())) {
    	            	target = attackedTarget;
    	            }
    	        }
    		}
    	}
    };

    
    private Vector2f getRotations(Entity target, float yaw, float pitch) {
		Vector2f rotations = RotationUtil.getEntityRotations(target);
		float sens = (float) (Math.pow(mc.gameSettings.mouseSensitivity * 0.6F + 0.2F, 3) * 1.2F);

		float speedY = speedYaw.getValue() + speedYaw2.getValue();
		float speedP = speedPitch.getValue() + speedPitch2.getValue();
			
		if (randomization.get()) {
			float randomYaw = MathUtil.randomizeFloat(this.randomYaw.getValue() / 2.0F, this.randomYaw.getValue() / 2.0F);
			float randomPitch = MathUtil.randomizeFloat(this.randomPitch.getValue() / 2.0F, this.randomPitch.getValue() / 2.0F);

			rotations.x -= randomYaw;
			rotations.y += randomPitch;
		}

		if (conditionals.isEnabled("Smoothness")) {
			float endRot = Math.round(rotations.x - yaw);

			if (endRot > fovRange.getValue() / 2.0F) {
				speedY /= 1.5F;
			}

			if (endRot > fovRange.getValue() / 3.0F) {
				speedY /= 2.0F;
			}

			if (endRot > fovRange.getValue() / 4.0F) {
				speedY /= 2.5F;
			}

			if (endRot > fovRange.getValue() / 8.0F) {
				speedY /= 3.0F;
			}
		}
		
		float strafe = mc.thePlayer.moveStrafing;

		if (mc.thePlayer.moveStrafing != 0) {
			if (mc.thePlayer.isSneaking()) {
				if (strafe > 0.2F) {
					rotations.x -= 0.5f / 4;
				}

				if (strafe < -0.2F) {
					rotations.x += 0.5f / 4;
				}
			} else {
				if (strafe > 0.6F) {
					rotations.x -= 0.5f;
				}

				if (strafe < -0.6F) {
					rotations.x += 0.5f;
				}
			}
		}

		rotations.x = RotationUtil.updateRotation(yaw, rotations.x, speedY);
		rotations.y = RotationUtil.updateRotation(pitch, rotations.y, speedP);

		rotations.x = Math.round(rotations.x / sens) * sens;
		rotations.y = Math.round(rotations.y / sens) * sens;
			
		return new Vector2f(rotations.x, rotations.y);
    }
	
    private EntityPlayer getTarget() {
        int fov = (int) fovRange.getValue();
        Vec3 playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        EntityPlayer bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (lockedTargets.contains(player) && !isValidTarget(player, playerPos, fov)) {
                continue;
            }
            if (!isValidTarget(player, playerPos, fov)) {
                continue;
            }
            
            double score = playerPos.distanceTo(player.getPositionVector());
            
            switch (priority.getMode()) {
            case "Distance":
            	score = mc.thePlayer.getDistanceSqToEntity(player);
            	break;
            case "Nearest":
            	score = RotationUtil.nearestRotation(player.getEntityBoundingBox());
            	break;
            case "BestVector":
            	score = RotationUtil.getDistanceToEntityBox(player);
            	break;
            case "BestVectorPosition":
            	score = RotationUtil.getDistanceToEntityBoxFromPosition(player.posX, player.posY, player.posZ, player);
            	break;
            }
            
            if (score < bestScore) {
                bestTarget = player;
                bestScore = score;
            }
        }
        
        return bestTarget;
    }
	
	private boolean isValidTarget(EntityPlayer player, Vec3 playerPos, int fov) {
	    if (player == mc.thePlayer || !player.isEntityAlive()) return false;
	    if (EnemyUtil.isEnemy(player)) return false;
	    if (PlayerUtil.unusedNames(player)) return false;
	    if (conditionals.isEnabled("MultiPoint") && mc.pointedEntity != null && mc.pointedEntity == target) return false;
	    if (!conditionals.isEnabled("IgnoreInvisibles") && !player.isInvisible()) return false;
	    if (FriendUtil.isFriend(player) && conditionals.isEnabled("IgnoreFriends")) return false;
	    if (PlayerUtil.isTeam(player, true, true) && isEnabled(Teams.class)) return false;
	    if (mc.thePlayer.getDistanceToEntity(player) > range.getValue()) return false;
	    if (conditionals.isEnabled("VisibilityCheck") && !mc.thePlayer.canEntityBeSeen(player)) return false;	    
	    return fov == 180 || PlayerUtil.isInFov(player, fov);
	}


	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
	    if (conditionals.isEnabled("OnlyWeapons") && !InventoryUtil.isSword()) return true;
	    if (conditionals.isEnabled("RequireClicking") && !Mouse.isButtonDown(0)) return true;
	    if (conditionals.isEnabled("MouseOverEntity")) {
	        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
	            return true;
	        }
	    }
	    
	    if (conditionals.isEnabled("BreakBlocks") && mc.objectMouseOver != null) {
	        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
	        if (blockPos != null) {
	            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
	            if (block != Blocks.air && block != Blocks.lava && block != Blocks.water &&
	                block != Blocks.flowing_lava && block != Blocks.flowing_water) {
	                return true;
	            }
	        }
	    }
	    
	    return false;
	}
	
    private boolean onTarget() {
        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
                && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                && mc.objectMouseOver.entityHit == target;
    }
    
    private boolean onTarget(EntityPlayer target) {
    	return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
    			&& mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
    			&& mc.objectMouseOver.entityHit == target;
    }
}

