package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

@SuppressWarnings("rawtypes")
@ModuleInfo(name = "NoSlow", category = Category.PLAYER)
public class NoSlow extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this, "NoItemRelease", "NoItemRelease");
	private final BoolValue sword = new BoolValue("Sword", this, false);
	private final BoolValue bow = new BoolValue("Bow", this, false);
	private final BoolValue foods = new BoolValue("Foods", this, false);
	private final BoolValue drinks = new BoolValue("Drinks", this, false);

	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isOutgoing()) {
			Packet packet = event.getPacket();
			
			if (!conditionals()) return;
			
			if (mode.is("NoItemRelease")) {
				if (packet instanceof C07PacketPlayerDigging) {
					C07PacketPlayerDigging wrapper = (C07PacketPlayerDigging) packet;
					if (wrapper.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
						event.isCancel();
					}
				}
			}
		}
	};
	
	private boolean conditionals() {
		ItemStack stack = mc.thePlayer.getHeldItem();
		if (stack == null) return false;
		Item item = stack.getItem();
		if (item == null) return false;
		return (sword.get() && item instanceof ItemSword) || (bow.get() && item instanceof ItemBow) || (foods.get() && item instanceof ItemFood) || (drinks.get() && (item instanceof ItemPotion || item instanceof ItemBucketMilk));
	}
}