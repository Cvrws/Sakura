package cc.unknown.ui.click.impl;

import java.util.List;

import cc.unknown.ui.click.Component;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.value.impl.ModeValue;

public class ModeComponent extends Component {

    private final ModeValue value;

    public ModeComponent(ModeValue value) {
        this.value = value;
        setHeight(11);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), getX() + 5F, getY() + 4F, -1);
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.get(),
                getX() + (getWidth() - 5) - FontUtil.getFontRenderer("interSemiBold.ttf", 13).getStringWidth(value.get()), getY() + 4F,
                -1);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(getX(), getY(), 100F, getHeight(), mouseX, mouseY)) {
            List<String> modes = value.getModes();
            
            if (modes.isEmpty()) {
                return;
            }

            int currentIndex = modes.indexOf(value.get());

            if (mouseButton == 0) {
                value.set(modes.get((currentIndex + 1) % modes.size()));
            } else if (mouseButton == 1) {
                value.set(modes.get((currentIndex - 1 + modes.size()) % modes.size()));
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }



    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
