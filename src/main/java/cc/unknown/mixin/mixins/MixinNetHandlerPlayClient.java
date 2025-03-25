package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Sakura;
import cc.unknown.event.impl.VelocityEvent;
import cc.unknown.mixin.impl.INetHandlerPlayClient;
import cc.unknown.mixin.impl.INetworkManager;
import cc.unknown.ui.click.AstolfoGui;
import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("rawtypes")
@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient implements INetHandlerPlayClient, Accessor {
	
	@Shadow
	@Final
	private NetworkManager netManager;
	
	@Shadow
	private Minecraft gameController;
	
	@Shadow
	private WorldClient clientWorldController;
	
	@Shadow
	public boolean doneLoadingTerrain;

	@Override
	public void addToSendQueueUnregistered(final Packet p_147297_1_) {
		((INetworkManager) netManager).sendUnregisteredPacket(p_147297_1_);
	}

	@Override
	public void addToReceiveQueue(final Packet packet) {
		((INetworkManager) netManager).receivePacket(packet);
	}

	@Override
	public void addToReceiveQueueUnregistered(final Packet packet) {
		((INetworkManager) netManager).receiveUnregisteredPacket(packet);
	}

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	private void handleCloseWindow(final S2EPacketCloseWindow packetIn, final CallbackInfo ci) {
		if (gameController.currentScreen instanceof AstolfoGui) {
			ci.cancel();
		}
	}
	
	@Inject(method = "handleEntityVelocity", at = @At("HEAD"), cancellable = true)
	private void handleEntityVelocity(S12PacketEntityVelocity packetIn, final CallbackInfo ci) {
	    PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayClient) (Object) this, this.gameController);
	    Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

	    if (entity != null) {
	        VelocityEvent knockBack = new VelocityEvent(
	            (double) packetIn.getMotionX() / 8000.0D, 
	            (double) packetIn.getMotionY() / 8000.0D, 
	            (double) packetIn.getMotionZ() / 8000.0D
	        );

	        if (entity.getEntityId() == this.gameController.thePlayer.getEntityId()) {
	            Sakura.instance.getEventBus().handle(knockBack);
	        }

	        entity.setVelocity(knockBack.getX(), knockBack.getY(), knockBack.getZ());
	    }
	    
	    ci.cancel();
	}
	
	@Inject(method = "handleEntityVelocity", at = @At("RETURN"))
	public void onPostHandleEntityVelocity(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
	    if (!isInGame()) return;

	    if (packetIn.getEntityID() == this.gameController.thePlayer.getEntityId()) {
	        Sakura.instance.getEventBus().handle(new VelocityEvent());
	    }
	}
	
	@Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
	private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) { }
}
