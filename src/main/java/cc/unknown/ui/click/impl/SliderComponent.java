package cc.unknown.ui.click.impl;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.lwjgl.input.Mouse;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.Component;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.RoundedUtil;
import cc.unknown.util.value.impl.SliderValue;

public class SliderComponent extends Component {

    private final SliderValue value;
    private final DecimalFormat FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public SliderComponent(SliderValue value) {
        this.value = value;
        setHeight(11);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	if (getModule(ClickGUI.class).roundedButtons.get()) {
    		RoundedUtil.drawRound(getX() + 3F, getY() + 2, (getWidth() - 5) * (value.get() / value.getMax()), getHeight() - 4, 2, new Color(164, 53, 144));
    	} else {
            RenderUtil.drawRect(getX() + 3F, getY() + 2, (getWidth() - 5) * (value.get() / value.getMax()), getHeight() - 4, new Color(164, 53, 144));
    	}

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), getX() + 5F, getY() + 4F, -1);
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawCenteredString(FORMAT.format(value.get()), getX() + 88F, getY() + 4F, -1);

        if (Mouse.isButtonDown(0) && MouseUtil.isHovered(getX(), getY(), getWidth() - 4, getHeight(), mouseX, mouseY)) {
            float set = Math.min(value.getMax(), Math.max(value.getMin(), (mouseX - getX()) / (getWidth() - 5) * (value.getMax() - value.getMin()) + value.getMin()));
            value.setValue((float) MathUtil.incValue(set, value.getIncrement()));
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
