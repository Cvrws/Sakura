package cc.unknown.ui.click.impl;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.ui.click.Component;
import cc.unknown.ui.click.HSBData;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.value.impl.ColorValue;

public class ColorComponent extends Component {

    private final ColorValue value;
    private boolean expanded;

    public ColorComponent(ColorValue value) {
        this.value = value;
        setHeight(11);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        HSBData data = new HSBData(value.get());
        final float[] hsba = {
                data.getHue(),
                data.getSaturation(),
                data.getBrightness(),
                value.get().getAlpha(),
        };
        
        RenderUtil.drawRoundedRect(getX() + 86, getY() + 1F, 8F, 8F, 3f, value.get().getRGB());
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), getX() +  5f, 5.5f + getY(), 0xffffffff);

        if (expanded) {
            RenderUtil.drawRect(getX() + 98 + 3, getY(), 61, 61, new Color(0, 0, 0));
            RenderUtil.drawRect(getX() + 98 + 3.5F, 0.5F + getY(), 60, 60, Color.getHSBColor(hsba[0], 1, 1));
            
            RenderUtil.drawHorizontalGradientSideways(getX() + 98F + 3.5F, 0.5F + getY(), 60, 60, Color.getHSBColor(hsba[0], 0, 1).getRGB(), 0x00F);
            RenderUtil.drawVerticalGradientSideways(getX() + 98 + 3.5f, 0.5F + getY(), 60, 60, 0x00F, Color.getHSBColor(hsba[0], 1, 0).getRGB());
            
            RenderUtil.drawRect(getX() + 98 + 3.5f + hsba[1] * 60 - .5f, 0.5F + ((1 - hsba[2]) * 60) - .5f + getY(), 1.5f, 1.5f, new Color(0, 0, 0));
            RenderUtil.drawRect(getX() + 98 + 3.5F + hsba[1] * 60,  0.5F + ((1 - hsba[2]) * 60) + getY(), .5f, .5f, value.get());

            final boolean onSB = MouseUtil.isHovered(getX() + 98 + 3, getY() + 0.5F, getWidth() + 40, getHeight() + 70, mouseX, mouseY);
            final boolean onHue = MouseUtil.isHovered(getX() + 98 + 67, getY(), 10, 70, mouseX, mouseY);

            if (onHue && Mouse.isButtonDown(0)) {
            	data.setHue(Math.min(Math.max((mouseY - getY()) / 60F, 0), 1));
                value.set(data.getAsColor());
            } else if (onSB && Mouse.isButtonDown(0)) {
                data.setSaturation(Math.min(Math.max((mouseX - (getX() + 98) - 3) / 60F, 0), 1));
                data.setBrightness(1 - Math.min(Math.max((mouseY - getY() - getHeight()) / 60F, 0), 1));
                value.set(data.getAsColor());
            }
            
            RenderUtil.drawRect(getX() + 98 + 67, getY(), 10, 61, new Color(0, 0, 0));

            for (float f = 0F; f < 5F; f += 1F) {
                final Color lasCol = Color.getHSBColor(f / 5F, 1F, 1F);
                final Color tarCol = Color.getHSBColor(Math.min(f + 1F, 5F) / 5F, 1F, 1F);
                RenderUtil.drawVerticalGradientSideways(getX() + 98 + 67.5F, 0.5F + f * 12 + getY(), 9, 12, lasCol.getRGB(), tarCol.getRGB());
            }

            RenderUtil.drawRect(getX() + 98 + 67.5F, -1 + hsba[0] * 60 + getY(), 9, 2, new Color(0, 0, 0));
            RenderUtil.drawRect(getX() + 98 + 67.5F, -0.5f + hsba[0] * 60 + getY(), 9, 1, new Color(204, 198, 255));
        }
        super.drawScreen(mouseX, mouseY);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(getX() ,getY(), 100F, getHeight(), mouseX, mouseY)) {
            if (mouseButton == 1) {
                expanded = !expanded;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
