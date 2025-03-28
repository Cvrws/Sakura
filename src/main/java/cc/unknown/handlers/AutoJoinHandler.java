package cc.unknown.handlers;

import java.util.List;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.ClientTickForgeEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

public class AutoJoinHandler implements Accessor {
	
    private static boolean joining;
    private static int lobby;
    private static Item item;
    private static int stage;
    private static boolean foundItem;

	@Kisoji
	public final Listener<PacketEvent> onPacketReceive = event -> {
		final Packet<?> packet = event.getPacket();
		if (!event.isIncoming()) return;
		
		if (isInGame()) {
			if (packet instanceof S08PacketPlayerPosLook)
				joining = false;
			if (stage == 2 && packet instanceof S2DPacketOpenWindow)
				stage = 3;
			if (stage >= 3 && packet instanceof S2EPacketCloseWindow)
				stage = 0;
		}
	};
	
	@Kisoji
	public final Listener<ClientTickForgeEvent> onTick = event -> {
		if (event.isPost()) return;
        if (isInGame()) {
            if (mc.currentScreen instanceof GuiChat || MoveUtil.isMoving()) {
                joining = false;
                return;
            }

            if (!joining)
                return;

            switch (stage) {

                case 0:
                    if (!foundItem && mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        stage++;
                    }
                    break;
                case 1:
                    if (mc.currentScreen instanceof GuiContainer) {
                        GuiContainer container = (GuiContainer) mc.currentScreen;
                        List<ItemStack> inventory = container.inventorySlots.getInventory();
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack slot = inventory.get(i);
                            if (slot != null)
                                if (slot.getItem() == item) {
                                    PacketUtil.send(new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
                                    stage++;
                                    break;
                                }
                        }
                    }
                    break;
                case 3:
                    if (mc.currentScreen instanceof GuiContainer) {
                        GuiContainer container = (GuiContainer) mc.currentScreen;
                        List<ItemStack> inventory = container.inventorySlots.getInventory();
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack slot = inventory.get(i);
                            if (slot != null)
                                if (slot.stackSize == lobby) {
                                    PacketUtil.send(new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
                                    stage++;
                                    break;
                                }
                        }
                    }
                    break;
                case 4:
                    if (mc.thePlayer.ticksExisted % 11 == 0)
                        stage = 3;
                    break;
            }
        }
	};
	
    public static void init(Item name, int lobbyNumber) {
        joining = true;
        item = name;
        lobby = lobbyNumber;
        stage = 0;
        foundItem = false;
    }
}
