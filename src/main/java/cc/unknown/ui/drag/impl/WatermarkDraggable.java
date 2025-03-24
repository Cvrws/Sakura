package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;

public class WatermarkDraggable extends Drag {
    public WatermarkDraggable() {
        super("Watermark");
        this.x = 0f;
        this.y = 0f;
    }
    
    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        float x = renderX;
        float y = renderY;
        float fontSize = 15f;
        float iconSize = 5.0f;
        float padding = 5.0F;

        String title = " | Sakura";
        float titleWidth = (float) FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).getStringWidth(title);

        width = iconSize * 2.5F + titleWidth + padding * 2;
        height = 20;

        width = Math.min(width, sr.getScaledWidth() - renderX);
        height = Math.min(height, sr.getScaledHeight() - renderY);

        RoundedUtil.drawRound(x, y, width, height - 2, 6.0F, new Color(getModule(Interface.class).bgColor(), true));

        FontUtil.getFontRenderer("flower.otf", 30).drawString("A", x + padding, y + (height / 2) - 6, setting.color());
        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).drawString(title, x + padding + iconSize * 2.5F, y + (height / 2) - 3, setting.color());
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Watermark");
    }
}
