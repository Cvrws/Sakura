package cc.unknown.module.impl.visual;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "ClickGUI",  category = Category.VISUALS, key = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {

	public final BoolValue moveGui = new BoolValue("MoveGui", this, true);
	public final BoolValue roundedOutline = new BoolValue("RoundedOutline", this, false);
	public final SliderValue outline = new SliderValue("Outline", this, 0.01f, 0.01f, 1f, 0.01f, () -> roundedOutline.get());
	public final BoolValue roundedButtons = new BoolValue("RoundedButtons", this, false);
    public final ColorValue outlineColor = new ColorValue("OutlineColor", new Color(128, 128, 255), this);
    public final ColorValue mainColor = new ColorValue("MainColor", new Color(164, 53, 144), this);
    
    @Override
    public void onEnable() {
    	mc.displayGuiScreen(Sakura.instance.getAstolfoGui());
        toggle();
        super.onEnable();
    }
}