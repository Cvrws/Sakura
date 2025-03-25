package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Mouse;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.AttackForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "AimAssist", category = Category.COMBAT)
public class AimAssist extends Module {

	private final ModeValue priority = new ModeValue("Priority", this, "Distance", "Distance", "VectorPosition", "BestVectorPosition", "Nearest", "BestVector");
	
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
			new BoolValue("VisibilityCheck", false), 
			new BoolValue("RequireClicking", true), 
			new BoolValue("BreakBlocks", false)));
	
	private final Set<EntityPlayer> lockedTargets = new HashSet<>();
	public EntityPlayer target;

	@Kisoji
	public final Listener<MotionEvent.Pre> onPreMotion = event -> {
		if (mc.currentScreen != null || !mc.inGameHasFocus) return;

		if (!conditionals.isEnabled("OnlyWeapons") || InventoryUtil.isSword()) {
			if (conditionals.isEnabled("BreakBlocks")) {
				BlockPos blockPos = mc.objectMouseOver.getBlockPos();

				if (blockPos != null) {
					Block block = PlayerUtil.getBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ());

					if (block != Blocks.air && block != Blocks.lava && block != Blocks.water && block != Blocks.flowing_lava && block != Blocks.flowing_water)
						return;
				}
			}

			if (!conditionals.isEnabled("RequireClicking") || Mouse.isButtonDown(0)) {
				target = getTarget();

				if (target != null) {
					Vector2f rotations = getRotations(target, event.getYaw(), event.getPitch());

					if (conditionals.isEnabled("IncreaseStrafe")) {
						if (mc.thePlayer.moveStrafing != 0) {
							float value = 0.5f;
							rotations.x += value;
						}
					}

					if (aimPitch.get())
						mc.thePlayer.rotationPitch = rotations.y;

					mc.thePlayer.rotationYaw = rotations.x;
				}
			}
		}
	};
	
    @Kisoji
    public final Listener<AttackForgeEvent> onAttack = event -> {
    	if (conditionals.isEnabled("LockTarget") && event.getEvent().entityLiving instanceof EntityPlayer) {
    	    EntityPlayer attackedTarget = (EntityPlayer) event.getEvent().entityLiving;
    	    if (isValidTarget(attackedTarget)) {
    	        lockedTargets.add(attackedTarget);
    	        if (target == null) {
    	            target = attackedTarget;
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
    
	public EntityPlayer getTarget() {
	    EntityPlayer bestTarget = null;
	    Vec3 playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
	    
	    if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
	        EntityPlayer aimedTarget = (EntityPlayer) mc.objectMouseOver.entityHit;
	        if (lockedTargets.contains(aimedTarget) && isValidTarget(aimedTarget)) {
	            return aimedTarget;
	        }
	    }

	    double bestScore = Double.MAX_VALUE;

	    for (EntityPlayer target : mc.theWorld.playerEntities) {
	        if (lockedTargets.contains(target)) continue;
	        if (target == mc.thePlayer) continue;
	        if (!isValidTarget(target)) continue;

	        double score = 0;
            
            switch (priority.getMode()) {
            case "Distance":
            	score = mc.thePlayer.getDistanceSqToEntity(target);
            	break;
            case "Nearest":
            	RotationUtil.nearestRotation(target.getEntityBoundingBox());
            	break;
            case "VectorPosition":
            	score = playerPos.distanceTo(target.getPositionVector());
            	break;
            case "BestVector":
            	score = RotationUtil.getDistanceToEntityBox(target);
            	break;
            case "BestVectorPosition":
            	score = RotationUtil.getDistanceToEntityBoxFromPosition(target.posX, target.posY, target.posZ, target);
            	break;
            }
            
            if (score < bestScore) {
                bestTarget = target;
                bestScore = score;
            }
	    }

	    return bestTarget;
	}

	private boolean isValidTarget(EntityPlayer target) {
	    return target != mc.thePlayer &&
	           !target.isDead &&
	           (!conditionals.isEnabled("VisibilityCheck") || mc.thePlayer.canEntityBeSeen(target)) &&
	           (!conditionals.isEnabled("IgnoreInvisibles") || !target.isInvisible()) &&
	           (!conditionals.isEnabled("IgnoreFriends") || !FriendUtil.isFriend(target)) &&
	           (!isEnabled(Teams.class) || !PlayerUtil.isTeam(target, true, true)) &&
	           PlayerUtil.inRange(target, range.getValue()) &&
	           PlayerUtil.isInFov(target, fovRange.getValue());
	}
}
