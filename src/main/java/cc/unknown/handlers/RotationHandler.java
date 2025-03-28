package cc.unknown.handlers;

import java.util.function.Function;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.JumpEvent;
import cc.unknown.event.impl.LookEvent;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.MoveInputEvent;
import cc.unknown.event.impl.StrafeEvent;
import cc.unknown.event.impl.UpdateEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.MoveFix;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerExt;
import cc.unknown.util.player.RotationUtil;
import cc.unknown.util.structure.Vector2f;
import net.minecraft.util.MathHelper;

public final class RotationHandler implements Accessor {
	private static boolean active, smoothed;
	public static Vector2f rotations, lastRotations = new Vector2f(0, 0), targetRotations, lastServerRotations;
	private static double rotationSpeed;
	private static MoveFix correctMovement;
	private static Function<Vector2f, Boolean> raycast;
	private static float randomAngle;
	private static final Vector2f offset = new Vector2f(0, 0);

	public static void setRotations(final Vector2f rotations, final double rotationSpeed,
			final MoveFix correctMovement) {
		setRotations(rotations, rotationSpeed, correctMovement, null);
	}

	public static void setRotations(final Vector2f rotations, final double rotationSpeed,
			final MoveFix correctMovement, final Function<Vector2f, Boolean> raycast) {
		RotationHandler.targetRotations = rotations;
		RotationHandler.rotationSpeed = rotationSpeed * 36;
		RotationHandler.correctMovement = correctMovement;
		RotationHandler.raycast = raycast;
		active = true;

		smooth();
	}

	@Kisoji(value = Priority.VERY_LOW)
	public final Listener<UpdateEvent.Pre> onPreUpdate = event -> {
		if (!active || rotations == null || lastRotations == null || targetRotations == null
				|| lastServerRotations == null) {
			rotations = lastRotations = targetRotations = lastServerRotations = new Vector2f(mc.thePlayer.rotationYaw,
					mc.thePlayer.rotationPitch);
		}

		if (active) {
			smooth();
		}
	};

	@Kisoji(value = Priority.LOW)
	public final Listener<MoveInputEvent> onMove = event -> {
		if (active && correctMovement == MoveFix.SILENT && rotations != null) {
			final float yaw = rotations.x;
			MoveUtil.fixMovement(event, yaw);
		}
	};

	@Kisoji(value = Priority.VERY_LOW)
	public final Listener<LookEvent> onLook = event -> {
		if (active && rotations != null) {
			event.setRotation(rotations);
		}
	};

	@Kisoji(value = Priority.VERY_LOW)
	public final Listener<StrafeEvent> onStrafe = event -> {
		if (event.isPost()) return;
		if (active && (correctMovement == MoveFix.SILENT || correctMovement == MoveFix.STRICT)
				&& rotations != null) {
			event.setYaw(rotations.x);
		}
	};

	@Kisoji(value = Priority.VERY_LOW)
	public final Listener<JumpEvent> onJump = event -> {
		if (active && (correctMovement == MoveFix.SILENT || correctMovement == MoveFix.STRICT) && rotations != null) {
			event.setYaw(rotations.x);
		}
	};

	@Kisoji(value = Priority.VERY_LOW)
	public final Listener<MotionEvent.Pre> onPreMotion = event -> {		
		if (active && rotations != null) {
			final float yaw = rotations.x;
			final float pitch = rotations.y;

			event.setYaw(yaw);
			event.setPitch(pitch);

			mc.thePlayer.rotationYawHead = yaw;
			PlayerExt.renderPitchHead = pitch;

			lastServerRotations = new Vector2f(yaw, pitch);

			if (Math.abs((rotations.x - mc.thePlayer.rotationYaw) % 360) < 1
					&& Math.abs((rotations.y - mc.thePlayer.rotationPitch)) < 1) {
				active = false;

				this.correctDisabledRotations();
			}

			lastRotations = rotations;
		} else {
			lastRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
		}

		targetRotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
		smoothed = false;

	};

	private void correctDisabledRotations() {
		final Vector2f rotations = new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
		final Vector2f fixedRotations = RotationUtil.resetRotation(RotationUtil.applySensitivityPatch(rotations, lastRotations));

		mc.thePlayer.rotationYaw = fixedRotations.x;
		mc.thePlayer.rotationPitch = fixedRotations.y;
	}

    public static void smooth() {
        if (!smoothed) {
            float targetYaw = targetRotations.x;
            float targetPitch = targetRotations.y;

            // Randomisation
            if (raycast != null && (Math.abs(targetYaw - rotations.x) > 5 || Math.abs(targetPitch - rotations.y) > 5)) {
                final Vector2f trueTargetRotations = new Vector2f(targetRotations.getX(), targetRotations.getY());

                double speed = (Math.random() * Math.random() * Math.random()) * 20;
                randomAngle += (float) ((20 + (float) (Math.random() - 0.5) * (Math.random() * Math.random() * Math.random() * 360)) * (mc.thePlayer.ticksExisted / 10 % 2 == 0 ? -1 : 1));

                offset.setX((float) (offset.getX() + -MathHelper.sin((float) Math.toRadians(randomAngle)) * speed));
                offset.setY((float) (offset.getY() + MathHelper.cos((float) Math.toRadians(randomAngle)) * speed));

                targetYaw += offset.getX();
                targetPitch += offset.getY();

                if (!raycast.apply(new Vector2f(targetYaw, targetPitch))) {
                    randomAngle = (float) Math.toDegrees(Math.atan2(trueTargetRotations.getX() - targetYaw, targetPitch - trueTargetRotations.getY())) - 180;

                    targetYaw -= offset.getX();
                    targetPitch -= offset.getY();

                    offset.setX((float) (offset.getX() + -MathHelper.sin((float) Math.toRadians(randomAngle)) * speed));
                    offset.setY((float) (offset.getY() + MathHelper.cos((float) Math.toRadians(randomAngle)) * speed));

                    targetYaw = targetYaw + offset.getX();
                    targetPitch = targetPitch + offset.getY();
                }

                if (!raycast.apply(new Vector2f(targetYaw, targetPitch))) {
                    offset.setX(0);
                    offset.setY(0);

                    targetYaw = (float) (targetRotations.x + Math.random() * 2);
                    targetPitch = (float) (targetRotations.y + Math.random() * 2);
                }
            }

            rotations = RotationUtil.smooth(new Vector2f(targetYaw, targetPitch),
                    rotationSpeed + Math.random());

            if (correctMovement == MoveFix.SILENT || correctMovement == MoveFix.STRICT) {
            	PlayerExt.movementYaw = rotations.x;
            }

            PlayerExt.velocityYaw = rotations.x;
        }

        smoothed = true;
        mc.entityRenderer.getMouseOver(1);
    }

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean active) {
		RotationHandler.active = active;
	}

	public static boolean isSmoothed() {
		return smoothed;
	}

	public static void setSmoothed(boolean smoothed) {
		RotationHandler.smoothed = smoothed;
	}

	public static Vector2f getRotations() {
		return rotations;
	}

	public static void setRotations(Vector2f rotations) {
		RotationHandler.rotations = rotations;
	}

	public static Vector2f getLastRotations() {
		return lastRotations;
	}

	public static void setLastRotations(Vector2f lastRotations) {
		RotationHandler.lastRotations = lastRotations;
	}

	public static Vector2f getTargetRotations() {
		return targetRotations;
	}

	public static void setTargetRotations(Vector2f targetRotations) {
		RotationHandler.targetRotations = targetRotations;
	}

	public static Vector2f getLastServerRotations() {
		return lastServerRotations;
	}

	public static void setLastServerRotations(Vector2f lastServerRotations) {
		RotationHandler.lastServerRotations = lastServerRotations;
	}

	public static double getRotationSpeed() {
		return rotationSpeed;
	}

	public static void setRotationSpeed(double rotationSpeed) {
		RotationHandler.rotationSpeed = rotationSpeed;
	}

	public static MoveFix getCorrectMovement() {
		return correctMovement;
	}

	public static void setCorrectMovement(MoveFix correctMovement) {
		RotationHandler.correctMovement = correctMovement;
	}

	public static Function<Vector2f, Boolean> getRaycast() {
		return raycast;
	}

	public static void setRaycast(Function<Vector2f, Boolean> raycast) {
		RotationHandler.raycast = raycast;
	}

	public static float getRandomAngle() {
		return randomAngle;
	}

	public static void setRandomAngle(float randomAngle) {
		RotationHandler.randomAngle = randomAngle;
	}

	public static Vector2f getOffset() {
		return offset;
	}
}