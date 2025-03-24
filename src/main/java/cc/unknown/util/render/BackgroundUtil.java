package cc.unknown.util.render;

import java.awt.Color;

import cc.unknown.util.Accessor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class BackgroundUtil implements Accessor {	
	public static void renderBackground(GuiScreen gui) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.image(new ResourceLocation("sakura/images/background.png"), 0, 0, gui.width, gui.height);
        RenderUtil.drawRoundedRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, new Color(0, 0, 0, 140).getRGB());
    }
}