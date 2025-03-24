package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.value.impl.BoolValue;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(name = "AutoTool", category = Category.PLAYER)
public class AutoTool extends Module {

    public final BoolValue spoof = new BoolValue("Spoof Slot", this, true);
    public final BoolValue switchBack = new BoolValue("Switch Back", this, true, spoof::get);
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
	public final Listener<TickForgeEvent> onTick = event -> {
		if (event.isPost()) return;
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