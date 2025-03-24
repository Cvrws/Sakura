package cc.unknown.module.impl.visual;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.GameEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(name = "FullBright", category = Category.VISUALS)
public class FullBright extends Module {

	private float oldGamma;
	
    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
    }
	
    @Kisoji
    public final Listener<GameEvent> onGame = event -> mc.gameSettings.gammaSetting = 100.0F;
}