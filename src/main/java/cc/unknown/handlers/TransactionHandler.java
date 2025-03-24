package cc.unknown.handlers;

import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;
import static cc.unknown.util.render.ColorUtil.reset;
import static cc.unknown.util.render.ColorUtil.yellow;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class TransactionHandler implements Accessor {
	
	private static AtomicBoolean toggle = new AtomicBoolean(false);

	public static void start() {
		toggle.set(!toggle.get());
	}
	
    @Kisoji
    public final Listener<PacketEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (!toggle.get()) return;
        if (!event.isIncoming()) return;
        
        if (packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            ChatUtil.display(yellow + "[" + green + "*" + yellow + "] " + reset + String.format(red + "Transaction " + reset + " (ID: %s) (WindowID: %s)", wrapper.getActionNumber(), wrapper.getWindowId()));
        }
    };
}