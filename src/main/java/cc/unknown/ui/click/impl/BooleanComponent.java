package cc.unknown.ui.click.impl;

import java.awt.Color;

import cc.unknown.ui.click.Component;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.value.impl.BoolValue;

public class BooleanComponent extends Component {

    private final BoolValue value;
    private boolean expanded = false;

    public BooleanComponent(BoolValue value) {
        this.value = value;
        setHeight(11);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), getX() + 5F, getY() + 4F, -1);

        float boxSize = 8F;
        float boxX = getX() + getWidth() - boxSize - 6F;
        float boxY = getY() + 2F;

        RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 3f, new Color(36, 36, 36).getRGB());

        if (value.get()) {
            RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 3f, new Color(164, 53, 144).getRGB());
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(getX(), getY(), getWidth(), 9, mouseX, mouseY)) {
            if (mouseButton == 0) {
                expanded = !expanded;
            }
        }

        if (MouseUtil.isHovered(getX(), getY(), getWidth(), 9, mouseX, mouseY)) {
            if (mouseButton == 0) {
                value.set(!value.get());
            }
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
