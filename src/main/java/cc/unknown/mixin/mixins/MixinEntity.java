package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.Sakura;
import cc.unknown.event.impl.StrafeEvent;
import cc.unknown.event.impl.StrafeEvent.StrafeType;
import cc.unknown.mixin.impl.IEntity;
import cc.unknown.util.player.PlayerExt;
import cc.unknown.util.structure.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {
	
	@Shadow
	public boolean onGround;

	@Shadow
	public float rotationYaw;
	
	@Shadow
	public boolean noClip;
	
	@Shadow
	public float rotationPitch;
	
	@Shadow
	public int ticksExisted;
	
	@Shadow
    public double motionX;
	
	@Shadow
	public double motionZ;
	
	@Shadow
	public double motionY;
	
	@Shadow
	public boolean isAirBorne;
	
	@Shadow
    public float prevRotationYaw;
    
	@Shadow
	public float prevRotationPitch;
	
	@Shadow
	public World worldObj;
	
	@Shadow
	public Entity ridingEntity;
	
	@Shadow
    public double prevPosX;
	
	@Shadow
	public double prevPosY;
    
	@Shadow
	public double prevPosZ;

	@Shadow
    public double posX;

	@Shadow
    public double posY;

	@Shadow
    public double posZ;
	
	@Shadow
	public abstract int getEntityId();
	
	@Shadow
	public abstract boolean isSprinting();
	
	@Shadow
	public abstract void onEntityUpdate();
	
	@Shadow
	public abstract boolean isRiding();
	
	@Shadow
	@Final
	public abstract Vec3 getVectorForRotation(float pitch, float yaw);
	
	@Shadow
	public abstract Vec3 getPositionEyes(float partialTicks);
	
	@Overwrite
	public void onUpdate() {
		if (this.onGround) {
			PlayerExt.offGroundTicks = 0;
			PlayerExt.onGroundTicks++;
		} else {
			PlayerExt.onGroundTicks = 0;
			PlayerExt.offGroundTicks++;
		}

		this.onEntityUpdate();
	}
	
	@Overwrite
	public void moveFlying(float strafe, float forward, float friction) {
		boolean player = (Object) this == Minecraft.getMinecraft().thePlayer;
		float yaw = this.rotationYaw;

		if (player) {
			final StrafeEvent event = new StrafeEvent(forward, strafe, friction, PlayerExt.movementYaw, StrafeType.PRE);

			Sakura.instance.getEventBus().handle(event);

			if (event.isCancelled()) {
				return;
			}

			forward = event.getForward();
			strafe = event.getStrafe();
			friction = event.getFriction();
			yaw = event.getYaw();
		}

		float f = strafe * strafe + forward * forward;

		if (f >= 1.0E-4F) {
			f = MathHelper.sqrt_float(f);

			if (f < 1.0F) {
				f = 1.0F;
			}

			f = friction / f;
			strafe = strafe * f;
			forward = forward * f;
			float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
			float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
			this.motionX += (double) (strafe * f2 - forward * f1);
			this.motionZ += (double) (forward * f2 + strafe * f1);
		}

		if (player) {
			final StrafeEvent event = new StrafeEvent(StrafeType.POST);

			Sakura.instance.getEventBus().handle(event);
		}
	}
	
	@Override
	public Vector3d getCustomPositionVector() {
		return new Vector3d(posX, posY, posZ);
	}

	@Override
    public Vec3 getLookCustom(float yaw, float pitch) {
        return this.getVectorForRotation(pitch, yaw);
    }
	
	@Override
    public MovingObjectPosition rayTraceCustom(double blockReachDistance, float yaw, float pitch) {
        final Vec3 vec3 = this.getPositionEyes(1.0F);
        final Vec3 vec31 = this.getLookCustom(yaw, pitch);
        final Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return this.worldObj.rayTraceBlocks(vec3, vec32, false, false, true);
    }
}