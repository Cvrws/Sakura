package cc.unknown.mixin.impl;

import net.minecraft.network.Packet;

@SuppressWarnings("rawtypes")
public interface INetworkManager {

	void receivePacket(final Packet packet);
	
	void receiveUnregisteredPacket(final Packet packet);
	
	void sendUnregisteredPacket(final Packet packetIn);
	
}
