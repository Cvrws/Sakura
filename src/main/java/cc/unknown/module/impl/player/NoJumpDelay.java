package cc.unknown.module.impl.player;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.UpdateEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(name = "NoJumpDelay", category = Category.PLAYER)
public class NoJumpDelay extends Module {
		
	@Kisoji
	public final Listener<UpdateEvent.Pre> onPreUpdate = event -> {
		if (!isInGame()) return;
		mc.thePlayer.jumpTicks = 0;
	};
}