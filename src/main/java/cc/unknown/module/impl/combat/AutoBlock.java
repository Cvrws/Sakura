package cc.unknown.module.impl.combat;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "AutoBlock", category = Category.COMBAT)
public class AutoBlock extends Module {

	private final ModeValue mode = new ModeValue("Mode", this, "Legit", "Legit", "Fast");
	private final SliderValue delay = new SliderValue("Ticks", this, 0, 0, 5, () -> mode.is("Legit"));

	private final StopWatch stopWatch = new StopWatch();
	
	@Kisoji
	public final Listener<TickForgeEvent> onTick = event -> {
		if (event.isPost()) return;
		
		if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (mode.is("Legit") && mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit.isEntityAlive() && mc.thePlayer.inventory.getCurrentItem() != null) {
                if (InventoryUtil.isSword() && stopWatch.finished((int) (delay.getValue() * 50))) {
                    mc.thePlayer.getCurrentEquippedItem().useItemRightClick(mc.theWorld, mc.thePlayer);
                    stopWatch.reset();
                }
            }

            if (mode.is("Fast")) {
                mc.thePlayer.getCurrentEquippedItem();
                if (!InventoryUtil.isSword()) {
                    return;
                }
                mc.thePlayer.getHeldItem().useItemRightClick(mc.theWorld, mc.thePlayer);
            }
		}
	};
}
