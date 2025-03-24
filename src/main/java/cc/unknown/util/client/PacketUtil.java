package cc.unknown.util.client;

import cc.unknown.mixin.impl.INetHandlerPlayClient;
import cc.unknown.util.Accessor;
import net.minecraft.network.Packet;

public final class PacketUtil implements Accessor {

	public static void send(final Packet<?> packet) {
		mc.getNetHandler().addToSendQueue(packet);
	}

	public static void sendNoEvent(final Packet<?> packet) {
		((INetHandlerPlayClient) mc.getNetHandler()).addToSendQueueUnregistered(packet);
	}
	
	public static void receive(final Packet<?> packet) {
		((INetHandlerPlayClient) mc.getNetHandler()).addToReceiveQueue(packet);
	}

	public static void receiveNoEvent(final Packet<?> packet) {
		((INetHandlerPlayClient) mc.getNetHandler()).addToReceiveQueueUnregistered(packet);
	}
}