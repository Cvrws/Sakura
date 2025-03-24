package cc.unknown.handlers;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.WorldForgeEvent;
import cc.unknown.util.Accessor;
import net.minecraft.item.ItemStack;

public class SpoofHandler implements Accessor  {
	private static int spoofedSlot;

	private static boolean spoofing;

	public static void startSpoofing(int slot) {
		spoofing = true;
		spoofedSlot = slot;
	}

	public static void stopSpoofing() {
		spoofing = false;
	}

	public static int getSpoofedSlot() {
		return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
	}

	public static ItemStack getSpoofedStack() {
		return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
	}

	@Kisoji
	public Listener<WorldForgeEvent> onWorld = event -> stopSpoofing();

	public static boolean isSpoofing() {
		return spoofing;
	}
}