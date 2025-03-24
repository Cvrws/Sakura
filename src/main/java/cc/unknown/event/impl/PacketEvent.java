package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent {
    private Packet<?> packet;
    private NetworkManager networkManager;
    private final EnumPacketDirection packetDir;
    
    public PacketEvent(Packet<?> packet, NetworkManager networkManager, EnumPacketDirection packetDir) {
		this.packet = packet;
		this.networkManager = networkManager;
		this.packetDir = packetDir;
	}

	public Packet<?> getPacket() {
		return packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public EnumPacketDirection getPacketDir() {
		return packetDir;
	}

	public boolean isOutgoing() {
    	return packetDir == EnumPacketDirection.CLIENTBOUND;
    }
    
    public boolean isIncoming() {
    	return packetDir == EnumPacketDirection.SERVERBOUND;
    }
}
