package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(name = "NoClickDelay", category = Category.PLAYER)
public class NoClickDelay extends Module {
		
	@Kisoji
	public final Listener<TickForgeEvent> onTick = event -> {
		if (event.isPost()) return;
		if (!isInGame()) return;
		mc.leftClickCounter = 0;
	};
}