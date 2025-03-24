package cc.unknown.util.player;

import java.util.Arrays;

import cc.unknown.event.impl.MoveInputEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class MoveUtil implements Accessor {
    public static boolean isMoving() {
        return mc.thePlayer != null && mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }
    
    public static void preventDiagonalSpeed() {
        KeyBinding[] gameSettings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft};

        final int[] down = {0};

        Arrays.stream(gameSettings).forEach(keyBinding -> down[0] = down[0] + (keyBinding.isKeyDown() ? 1 : 0));

        boolean active = down[0] == 1;

        if (active) return;

        final double groundIncrease = (0.1299999676734952 - 0.12739998266255503) + 1E-7 - 1E-8;
        final double airIncrease = (0.025999999334873708 - 0.025479999685988748) - 1E-8;
        final double increase = mc.thePlayer.onGround ? groundIncrease : airIncrease;

        moveFlying(-increase);
    }
    
    public static double getBPS() {
        return getBPS(mc.thePlayer);
    }

    public static double getBPS(EntityPlayer player) {
        if (player == null || player.ticksExisted < 1) {
            return 0.0;
        }
        return getDistance(player.lastTickPosX, player.lastTickPosZ) * (20.0f * mc.timer.timerSpeed);
    }

    public static double getDistance(final double x, final double z) {
        final double xSpeed = mc.thePlayer.posX - x;
        final double zSpeed = mc.thePlayer.posZ - z;
        return MathHelper.sqrt_double(xSpeed * xSpeed + zSpeed * zSpeed);
    }
    
    public static void moveFlying(double increase) {
        if (!isMoving()) return;
        final double yaw = direction();
        mc.thePlayer.motionX += -MathHelper.sin((float) yaw) * increase;
        mc.thePlayer.motionZ += MathHelper.cos((float) yaw) * increase;
    }
    
    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
    
    public static double direction(float inputForward, float inputStrafe) {
        float rotationYaw = PlayerExt.movementYaw;

        if (inputForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (inputForward < 0) {
            forward = -0.5F;
        } else if (inputForward > 0) {
            forward = 0.5F;
        }

        if (inputStrafe > 0) {
            rotationYaw -= 70 * forward;
        }

        if (inputStrafe < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }
    
    public static double direction() {
        float rotationYaw = PlayerExt.movementYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 90 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 90 * forward;
        }

        return Math.toRadians(rotationYaw);
    }
    
    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }
    
    public static void keybindStop() {
    	mc.gameSettings.keyBindForward.pressed = false;
    	mc.gameSettings.keyBindBack.pressed = false;
    	mc.gameSettings.keyBindLeft.pressed = false;
    	mc.gameSettings.keyBindRight.pressed = false;
    }
    
    public static void fixMovement(final MoveInputEvent event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

}
