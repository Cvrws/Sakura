package cc.unknown.ui.click.impl;

import java.awt.Color;

import cc.unknown.ui.click.Component;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.MultiBoolValue;

public class MultiBooleanComponent extends Component {

    private final MultiBoolValue value;
    private boolean expanded;

    public MultiBooleanComponent(MultiBoolValue value) {
        this.value = value;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0;

        if (expanded) {
            RenderUtil.drawRect(getX() + 3F, getY(), getWidth() - 5, getHeight(), new Color(17, 17, 17));
        }

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawCenteredString(value.getName() + "...", getX() + 50F, getY() + 4F, -1);

        if (expanded) {
            RenderUtil.drawRoundedRect(getX() + 88, getY() + 1F, 9F, 9F, 3f, new Color(17, 17, 17).getRGB());

            for (BoolValue boolValue : value.getValues()) {
                offset += 11;

                FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(boolValue.getName(), getX() + 5F, getY() + 4F + offset, -1);

                float boxSize = 8F;
                float boxX = getX() + getWidth() - boxSize - 6F;
                float boxY = getY() + offset + 2F;

                RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 3f, new Color(36, 36, 36).getRGB());

                if (boolValue.get()) {
                    RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 3f, new Color(164, 53, 144).getRGB());
                }
            }
        }

        this.setHeight(offset + 11);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float offset = 0;
        
        if (MouseUtil.isHovered(getX(), getY(), getWidth(), 11, mouseX, mouseY)) {
            if (mouseButton == 0) {
                expanded = !expanded;
            }
        }

        if (expanded) {
            for (BoolValue boolValue : value.getValues()) {
                offset += 11;
                if (MouseUtil.isHovered(getX(), getY() + offset, getWidth(), 11, mouseX, mouseY)) {
                    if (mouseButton == 0) {
                        boolValue.set(!boolValue.get());
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
