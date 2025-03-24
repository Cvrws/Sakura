package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.PacketUtil;
import cc.unknown.util.structure.lists.SList;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager.InboundHandlerTuplePacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

@SuppressWarnings("all")
@ModuleInfo(name = "Blink", category = Category.PLAYER)
public class Blink extends Module {
	private final SList<Packet<?>> packets = new SList<>();
	
	@Override
	public void onEnable() {
		packets.clear();
	}

	@Override
	public void onDisable() {
		for (Packet packet : packets) {
			mc.getNetHandler().getNetworkManager().outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener) null));
		}
		packets.clear();
	}
	
	@Kisoji
	public final Listener<TickForgeEvent> onTickPost = event -> {
		if (event.isPre()) return;
		if (mc.thePlayer == null) return;
		while (!packets.isEmpty()) {
			Packet packet = packets.get(0);

			if (packet instanceof S32PacketConfirmTransaction) {
				S32PacketConfirmTransaction transaction = (S32PacketConfirmTransaction) packet;
				PacketUtil.sendNoEvent(new C0FPacketConfirmTransaction(transaction.getWindowId(), transaction.getActionNumber(), false));
			} else if (packet instanceof S00PacketKeepAlive) {
				S00PacketKeepAlive keepAlive = (S00PacketKeepAlive) packet;
				PacketUtil.sendNoEvent(new C00PacketKeepAlive(keepAlive.func_149134_c()));
			} else if (packet instanceof C03PacketPlayer) {
				break;
			}

			PacketUtil.sendNoEvent(packets.get(0));
			packets.remove(packets.get(0));
		}
	};
	
	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isOutgoing()) {
			packets.add(event.getPacket());
			event.setCancelled(true);
		} else if (event.isIncoming()) {
			if (event.getPacket() instanceof S18PacketEntityTeleport || event.getPacket() instanceof S14PacketEntity
					|| event.getPacket() instanceof S14PacketEntity.S15PacketEntityRelMove
					|| event.getPacket() instanceof S14PacketEntity.S16PacketEntityLook
					|| event.getPacket() instanceof S14PacketEntity.S17PacketEntityLookMove) {
				return;
			}
		}
	};
}