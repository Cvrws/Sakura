package cc.unknown.module.impl.world;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.Render3DForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "FastPlace", category = Category.WORLD)
public class FastPlace extends Module {
	private final SliderValue cps = new SliderValue("CPS", this, 10, 1, 25);
	private final StopWatch stopWatch = new StopWatch();

	@Kisoji
	public final Listener<Render3DForgeEvent> onRenderHand = event -> {
		if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
			if (stopWatch.finished(1000 / (int) cps.getValue())) {
				PlayerUtil.rightClick(true);
				stopWatch.reset();
			}
		}
	};
}
