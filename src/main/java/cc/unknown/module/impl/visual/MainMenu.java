package cc.unknown.module.impl.visual;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.BoolValue;

@ModuleInfo(name = "MainMenu", category = Category.VISUALS)
public class MainMenu extends Module {

	public final BoolValue particles = new BoolValue("Particles", this, false);
	public final BoolValue roundedButtons = new BoolValue("RoundedButtons", this, false);
	
    @Override
    public void onEnable() {
        toggle();
        super.onEnable();
    }
}