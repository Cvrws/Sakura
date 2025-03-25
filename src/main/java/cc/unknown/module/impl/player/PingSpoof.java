package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.BoolValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S00PacketKeepAlive;

@SuppressWarnings("rawtypes")
@ModuleInfo(name = "PingSpoof", category = Category.PLAYER)
public class PingSpoof extends Module {
	
	private final BoolValue client = new BoolValue("Outgoing", this, true);
	private final BoolValue server = new BoolValue("Incoming", this, false);

	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		Packet packet = event.getPacket();
		if (event.isOutgoing()) {
			if (client.get() && packet instanceof C00PacketKeepAlive) {
				event.setCancelled(true);
			}
		}
		
		if (event.isIncoming()) {
			if (server.get() && packet instanceof S00PacketKeepAlive) {
				event.setCancelled(true);
			}
		}
	};
}