package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.Sakura;
import cc.unknown.event.impl.ChatEvent;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.PushOutOfBlockEvent;
import cc.unknown.event.impl.UpdateEvent;
import cc.unknown.mixin.impl.IEntityPlayerSP;
import cc.unknown.util.player.PlayerExt;
import cc.unknown.util.structure.Vector2f;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(EntityPlayerSP.class)
@SideOnly(Side.CLIENT)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer implements IEntityPlayerSP {
	
	@Shadow
	public MovementInput movementInput;
	
	@Shadow
	@Final
	public NetHandlerPlayClient sendQueue;
	
	@Shadow
	private boolean serverSneakState;
	@Shadow
	private double lastReportedPosX;
	@Shadow
	private double lastReportedPosY;
	@Shadow
	private double lastReportedPosZ;
	@Shadow
	private float lastReportedPitch;
	@Shadow
	private int positionUpdateTicks;
	@Shadow
	private boolean serverSprintState;
	@Shadow
	public float lastReportedYaw;
	@Shadow
	protected abstract boolean isCurrentViewEntity();
	
	@Shadow
	public abstract void onUpdateWalkingPlayer();
	
	@Shadow
	public abstract boolean isSneaking();
	
	@Overwrite
    public void onUpdate() {
		PlayerExt.prevRenderPitchHead = PlayerExt.renderPitchHead;
		PlayerExt.renderPitchHead = rotationPitch;

    	UpdateEvent event = new UpdateEvent.Pre();
    	Sakura.instance.getEventBus().handle(event);
        if (event.isCancelled()) return;
        
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
        	Sakura.instance.getEventBus().handle(new UpdateEvent.Post());
        	
            super.onUpdate();

            if (this.isRiding()) {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            } else {
                this.onUpdateWalkingPlayer();
            }
        }
    }
	
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayer(CallbackInfo ci) {
        MotionEvent.Pre event = new MotionEvent.Pre(posX, posY, posZ, rotationYaw, rotationPitch, onGround, isSprinting(), isSneaking());

        Sakura.instance.getEventBus().handle(event);
        if (event.isCancelled()) {
        	Sakura.instance.getEventBus().handle(new MotionEvent.Post(posX, posY, posZ, rotationYaw, rotationPitch, onGround, isSprinting(), isSneaking()));
        	return;
        }

        boolean flag = event.isSprinting();
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.serverSprintState = flag;
        }

        boolean flag1 = event.isSneaking();
        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = flag1;
        }

        if (this.isCurrentViewEntity()) {
            double d0 = event.getPosX() - this.lastReportedPosX;
            double d1 = event.getPosY() - this.lastReportedPosY;
            double d2 = event.getPosZ() - this.lastReportedPosZ;
            
            float yaw = event.getYaw();
            float pitch = event.getPitch();
            
            double d3 = yaw - this.lastReportedYaw;
            double d4 = pitch - this.lastReportedPitch;
            
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0 || d4 != 0.0;
            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.getPosX(), event.getPosY(), event.getPosZ(), yaw, pitch, event.isOnGround()));
                } else if (flag2) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.getPosX(), event.getPosY(), event.getPosZ(), event.isOnGround()));
                } else if (flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, event.isOnGround()));
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(event.isOnGround()));
                }
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, this.motionZ, yaw, pitch, event.isOnGround()));
                flag2 = false;
            }

            ++this.positionUpdateTicks;

            if (flag2) {
                this.lastReportedPosX = event.getPosX();
                this.lastReportedPosY = event.getPosY();
                this.lastReportedPosZ = event.getPosZ();
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = yaw;
                this.lastReportedPitch = pitch;
            }
        }
        
    	Sakura.instance.getEventBus().handle(new MotionEvent.Post(posX, posY, posZ, rotationYaw, rotationPitch, onGround, isSprinting(), isSneaking()));
    	ci.cancel();
    }
    
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
    	PushOutOfBlockEvent event = new PushOutOfBlockEvent();
        if (noClip) {
            event.isCancel();
        }
        Sakura.instance.getEventBus().handle(event);

        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
    
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo ci) {
		ChatEvent event = new ChatEvent(message);
		Sakura.instance.getEventBus().handle(event);
		if (event.isCancelled()) {
			ci.cancel();
		}
	}
    
    @Override
    public void setServerSprintState(final boolean serverSprintState) {
        this.serverSprintState = serverSprintState;
    }

	@Override
    public Vector2f getPreviousRotation() {
        return new Vector2f(lastReportedYaw, lastReportedPitch);
    }
}