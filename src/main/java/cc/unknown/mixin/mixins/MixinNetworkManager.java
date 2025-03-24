package cc.unknown.mixin.mixins;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Queues;

import cc.unknown.Sakura;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.mixin.impl.INetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;

@SuppressWarnings("all")
@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager {
	
	@Shadow
	private Channel channel;
	
	@Shadow
	public INetHandler packetListener;
	
	@Shadow
	public abstract boolean isChannelOpen();
	
	@Shadow
	public abstract void flushOutboundQueue();
	
	@Shadow
	public abstract void setConnectionState(EnumConnectionState newState);
	
	@Shadow
	@Final
    public ReentrantReadWriteLock field_181680_j = new ReentrantReadWriteLock();
	
	@Shadow
    public abstract void dispatchPacket(final Packet inPacket, final GenericFutureListener <? extends Future <? super Void >> [] futureListeners);
	
	@Shadow
	@Final
    public Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.<NetworkManager.InboundHandlerTuplePacketListener>newConcurrentLinkedQueue();

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        final PacketEvent event = new PacketEvent(packet, (NetworkManager) (Object) this, EnumPacketDirection.SERVERBOUND);
        Sakura.instance.getEventBus().handle(event);

        if (event.isCancelled()) {
            callback.cancel();
            return;
        }
    }
    
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> inPacket, CallbackInfo ci) {
        final PacketEvent event = new PacketEvent(inPacket, (NetworkManager) (Object) this, EnumPacketDirection.CLIENTBOUND);
        Sakura.instance.getEventBus().handle(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
    }

	@Override
	public void receivePacket(Packet packet) {
        if (this.channel.isOpen()) {
            try {
                final PacketEvent event = new PacketEvent(packet, (NetworkManager) (Object) this, EnumPacketDirection.SERVERBOUND);
                Sakura.instance.getEventBus().handle(event);

                if (event.isCancelled()) {
                    return;
                }
                
                packet.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException var4) {
            }
        }		
	}

	@Override
	public void receiveUnregisteredPacket(Packet packet) {
        if (this.channel.isOpen()) {
            try {
                packet.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException var4) {
            }
        }		
	}

	@Override
	public void sendUnregisteredPacket(Packet packetIn) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
        } else {
            this.field_181680_j.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[]) null));
            } finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
	}
}
