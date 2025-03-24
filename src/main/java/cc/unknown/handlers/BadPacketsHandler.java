package cc.unknown.handlers;

import static net.minecraft.network.play.client.C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class BadPacketsHandler {
    private static boolean c09, c02, c0a, c08, c16;

    public static boolean bad() {
        return bad(true, true, true, true, true);
    }

    public static boolean bad(final boolean c09, final boolean c02, final boolean c0a, final boolean c08, final boolean c16) {
        return (BadPacketsHandler.c09 && c09) ||
                (BadPacketsHandler.c02 && c02) ||
                (BadPacketsHandler.c0a && c0a) ||
                (BadPacketsHandler.c08 && c08) ||
                (BadPacketsHandler.c16 && c16);
    }

    @Kisoji(value = Priority.VERY_HIGH)
    public final Listener<PacketEvent> onPacketSend = event -> {
        final Packet<?> packet = event.getPacket();
        if (!event.isOutgoing()) return;

        if (packet instanceof C09PacketHeldItemChange) {
            c09 = true;
        } else if (packet instanceof C0APacketAnimation) {
            c0a = true;
        } else if (packet instanceof C02PacketUseEntity) {
            c02 = true;
        } else if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging) {
            c08 = true;
        } else if (packet instanceof C0EPacketClickWindow || (packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == OPEN_INVENTORY_ACHIEVEMENT) || packet instanceof C0DPacketCloseWindow) {
            c16 = true;
        } else if (packet instanceof C03PacketPlayer) {
            reset();
        }
    };

    public static void reset() {
        c09 = false;
        c0a = false;
        c02 = false;
        c08 = false;
        c16 = false;
    }
}
