package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.ClientTickForgeEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.value.impl.BoolValue;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(name = "AutoTool", category = Category.PLAYER)
public class AutoTool extends Module {

	private final BoolValue spoof = new BoolValue("SpoofSlot", this, true);
	private final BoolValue switchBack = new BoolValue("SwitchBack", this, true, spoof::get);
	private final BoolValue autoWeapon = new BoolValue("AutoWeapon", this, true);

	private int oldSlot;
	public boolean wasDigging;

	@Override
	public void onDisable() {
		if (wasDigging) {
			mc.thePlayer.inventory.currentItem = oldSlot;
			wasDigging = false;
		}

		SpoofHandler.stopSpoofing();
	}

	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isIncoming()) return;
		if (autoWeapon.get() && (event.getPacket() instanceof C02PacketUseEntity) && ((C02PacketUseEntity) event.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
			boolean checks = !mc.thePlayer.isEating();
			if (checks)
				InventoryUtil.bestSword(((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld));
		}
	};

	@Kisoji
	public final Listener<ClientTickForgeEvent> onTick = event -> {
		if (event.isPost())
			return;
		if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && InventoryUtil.findTool(mc.objectMouseOver.getBlockPos()) != -1) {
			if (!wasDigging) {
				oldSlot = mc.thePlayer.inventory.currentItem;
				if (spoof.get()) {
					SpoofHandler.startSpoofing(oldSlot);
				}
			}
			mc.thePlayer.inventory.currentItem = InventoryUtil.findTool(mc.objectMouseOver.getBlockPos());
			wasDigging = true;
		} else if (wasDigging && (switchBack.get() || spoof.get())) {
			mc.thePlayer.inventory.currentItem = oldSlot;
			SpoofHandler.stopSpoofing();
			wasDigging = false;
		} else {
			oldSlot = mc.thePlayer.inventory.currentItem;
		}
	};
}