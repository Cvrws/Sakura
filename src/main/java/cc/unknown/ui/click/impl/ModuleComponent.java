package cc.unknown.ui.click.impl;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.module.Module;
import cc.unknown.ui.click.Component;
import cc.unknown.ui.click.IComponent;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontRenderer;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.value.Value;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import cc.unknown.util.value.impl.TextValue;

public class ModuleComponent implements IComponent {
    private float x, y, width, height;
    private final Module module;
    private final CopyOnWriteArrayList<Component> values = new CopyOnWriteArrayList<>();

    public ModuleComponent(Module module) {
        this.module = module;
        for (Value value : module.getValues()) {
            if (value instanceof BoolValue) {
                values.add(new BooleanComponent((BoolValue) value));
            } else if (value instanceof ColorValue) {
                values.add(new ColorComponent((ColorValue) value));
            } else if (value instanceof SliderValue) {
                values.add(new SliderComponent((SliderValue) value));
            } else if (value instanceof ModeValue) {
                values.add(new ModeComponent((ModeValue) value));
            } else if (value instanceof MultiBoolValue) {
                values.add(new MultiBooleanComponent((MultiBoolValue) value));
            } else if (value instanceof TextValue) {
                values.add(new TextComponent((TextValue) value));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float yOffset = 11;
        String moduleName = module.getName().substring(0, 1).toUpperCase() + module.getName().substring(1);
        
        FontRenderer fontRenderer = FontUtil.getFontRenderer("interSemiBold.ttf", 15);
        float textWidth = (float) fontRenderer.getStringWidth(moduleName);
        float textX = getX() + (width - textWidth) / 2F;

        if (MouseUtil.isHovered(x, y, width, 11F, mouseX, mouseY)) {
            if (!module.isExpanded() && !module.isEnabled() || module.isExpanded()) {
            	fontRenderer.drawString(moduleName, textX, y + 4F, new Color(255, 255, 255).getRGB());
            }
        }
        
        fontRenderer.drawString(moduleName, textX, y + 4F, 
            module.isEnabled() ? new Color(164, 53, 144).getRGB() 
                                                      : new Color(160, 160, 160).getRGB());

        if (module.isExpanded()) {
            for (Component component : values) {
                if (!component.isVisible()) continue;
                component.setX(x);
                component.setY(y + yOffset);
                component.setWidth(width);
                component.drawScreen(mouseX, mouseY);
                yOffset += component.getHeight();
            }
        }

        this.height = yOffset;

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(x, y, width, 10F, mouseX, mouseY)) {
            if (mouseButton == 1) {
                if (!module.getValues().isEmpty()) {
                    module.setExpanded(!module.isExpanded());
                }
            }

            if (mouseButton == 0) {
                module.toggle();
            }
        }

        if (module.isExpanded()) {
            for (Component value : values) {
                value.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Module getModule() {
		return module;
	}

	public CopyOnWriteArrayList<Component> getValues() {
		return values;
	}
}
