package cc.unknown.mixin.impl;

import net.minecraft.network.Packet;

@SuppressWarnings("rawtypes")
public interface INetHandlerPlayClient {

	void addToSendQueueUnregistered(final Packet p_147297_1_);

	void addToReceiveQueue(final Packet packet);

	void addToReceiveQueueUnregistered(final Packet packet);
}
