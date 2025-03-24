package cc.unknown.module.impl.world;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "FastBreak", category = Category.WORLD)
public class FastBreak extends Module {
	
	private final SliderValue speed = new SliderValue("Speed", this, 0.60f, 0.01f, 1f, 0.01f);
	
	@Kisoji
	public final Listener<TickForgeEvent> onTick = event -> {
		if (event.isPost()) return;
        if (mc.playerController.curBlockDamageMP >= speed.getValue()) {
            mc.playerController.curBlockDamageMP = 1.0F;
        }
	};
	
	@Kisoji
	public final Listener<MotionEvent.Pre> onPreMotion = event -> {
		mc.playerController.blockHitDelay = 0;
	};

}
