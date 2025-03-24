package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.Sakura;
import cc.unknown.event.impl.JumpEvent;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.util.player.PlayerExt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

	@Shadow
	@Final
	private ItemStack[] previousEquipment = new ItemStack[5];
	
	@Shadow
	private BaseAttributeMap attributeMap;
	
	@Shadow
	protected float prevOnGroundSpeedFactor;
	
	@Shadow
	public float renderYawOffset;
	
	@Shadow
	public int arrowHitTimer;
	
	@Shadow
	protected float movedDistance;
	
	@Shadow
	protected float onGroundSpeedFactor;
	
	@Shadow
	public float prevRenderYawOffset;
	
	@Shadow
    public float rotationYawHead;

	@Shadow
    public float prevRotationYawHead;
	
	@Shadow
	public float swingProgress;
	
	@Shadow
    public float moveStrafing;
    
	@Shadow
	public float moveForward;

	@Shadow
	public abstract CombatTracker getCombatTracker();
	
	@Shadow
	protected abstract float getJumpUpwardsMotion();

	@Shadow
	public abstract boolean isPotionActive(Potion potionIn);

	@Shadow
	public abstract PotionEffect getActivePotionEffect(Potion potionIn);
	
	@Shadow
	@Final
	public abstract int getArrowCountInEntity();
	@Shadow
	@Final
	public abstract void setArrowCountInEntity(int count);

	@Shadow
	public abstract ItemStack getEquipmentInSlot(int slotIn);
	
	@Shadow
	public abstract void onLivingUpdate();
	
	@Shadow
	public abstract Vec3 getLook(float partialTicks);
	
	@Overwrite
	protected void jump() {
		float jumpMotion = this.getJumpUpwardsMotion();

		if (this.isPotionActive(Potion.jump)) {
			jumpMotion += (float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
		}

		if ((Object) this == Minecraft.getMinecraft().thePlayer) {
			final JumpEvent event = new JumpEvent(jumpMotion, PlayerExt.movementYaw);
			Sakura.instance.getEventBus().handle(event);
			jumpMotion = event.getJumpMotion();
			PlayerExt.movementYaw = event.getYaw();
			PlayerExt.velocityYaw = event.getYaw();

			if (event.isCancelled()) {
				return;
			}
		}

		this.motionY = jumpMotion;

		if (this.isSprinting()) {
			float f = PlayerExt.movementYaw * 0.017453292F;
			this.motionX -= MathHelper.sin(f) * 0.2F;
			this.motionZ += MathHelper.cos(f) * 0.2F;
		}

		this.isAirBorne = true;
	}

	@Overwrite
	protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
        float yaw = this.rotationYaw;
        if ((Object) this == Minecraft.getMinecraft().thePlayer && RotationHandler.rotations != null) yaw = RotationHandler.rotations.x;

        final float f = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += f * 0.3F;
        float f1 = MathHelper.wrapAngleTo180_float(yaw - this.renderYawOffset);
        final boolean flag = f1 < -90.0F || f1 >= 90.0F;

        if (f1 < -75.0F) {
            f1 = -75.0F;
        }

        if (f1 >= 75.0F) {
            f1 = 75.0F;
        }

        this.renderYawOffset = yaw - f1;

        if (f1 * f1 > 2500.0F) {
            this.renderYawOffset += f1 * 0.2F;
        }

        if (flag) {
            p_110146_2_ *= -1.0F;
        }

        return p_110146_2_;
	}
	
	@Overwrite
	public void onUpdate() {
		super.onUpdate();

		if (!this.worldObj.isRemote) {
			int i = this.getArrowCountInEntity();

			if (i > 0) {
				if (this.arrowHitTimer <= 0) {
					this.arrowHitTimer = 20 * (30 - i);
				}

				--this.arrowHitTimer;

				if (this.arrowHitTimer <= 0) {
					this.setArrowCountInEntity(i - 1);
				}
			}

			for (int j = 0; j < 5; ++j) {
				ItemStack itemstack = this.previousEquipment[j];
				ItemStack itemstack1 = this.getEquipmentInSlot(j);

				if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
					((WorldServer) this.worldObj).getEntityTracker().sendToAllTrackingEntity((EntityLivingBase) (Object)this,
							new S04PacketEntityEquipment(this.getEntityId(), j, itemstack1));

					if (itemstack != null) {
						this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
					}

					if (itemstack1 != null) {
						this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
					}

					this.previousEquipment[j] = itemstack1 == null ? null : itemstack1.copy();
				}
			}

			if (this.ticksExisted % 20 == 0) {
				this.getCombatTracker().reset();
			}
		}
		
		this.onLivingUpdate();
		
		float yaw = this.rotationYaw;
		if ((Object) this == Minecraft.getMinecraft().thePlayer && RotationHandler.rotations != null) {
			yaw = RotationHandler.rotations.x;
		}
		
		final double d0 = this.posX - this.prevPosX;
		final double d1 = this.posZ - this.prevPosZ;
		final float f = (float) (d0 * d0 + d1 * d1);
		float f1 = this.renderYawOffset;
		float f2 = 0.0F;
		this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
		float f3 = 0.0F;

		if (f > 0.0025000002F) {
			f3 = 1.0F;
			f2 = (float) Math.sqrt(f) * 3.0F;
			f1 = (float) MathHelper.atan2(d1, d0) * 180.0F / (float) Math.PI - 90.0F;
		}

		if (this.swingProgress > 0.0F) {
			f1 = yaw;
		}

		if (!this.onGround) {
			f3 = 0.0F;
		}

		this.onGroundSpeedFactor += (f3 - this.onGroundSpeedFactor) * 0.3F;
		this.worldObj.theProfiler.startSection("headTurn");

		f2 = this.func_110146_f(f1, f2);
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("rangeChecks");
		
		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
			this.prevRenderYawOffset -= 360.0F;
		}

		while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
			this.prevRenderYawOffset += 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
			this.prevRotationPitch -= 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
			this.prevRotationYawHead -= 360.0F;
		}

		while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
			this.prevRotationYawHead += 360.0F;
		}

		this.worldObj.theProfiler.endSection();
		this.movedDistance += f2;
	}

}