package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;

@ModuleInfo(name = "PingSpoof", category = Category.PLAYER)
public class PingSpoof extends Module {

	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		Packet packet = event.getPacket();
		if (event.isOutgoing()) {
			if (packet instanceof C00PacketKeepAlive) {
				event.setCancelled(true);
			}
		}
	};
}