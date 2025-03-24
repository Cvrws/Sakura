package cc.unknown.ui.click;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.impl.ModuleComponent;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.FontRenderer;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.RoundedUtil;

public class Window implements IComponent {
    private final List<ModuleComponent> moduleComponents;
    private final Category category;
    private float x, y, dragX, dragY;
    private final float width = 100;
    private float height;
    private boolean expand = false;
    private boolean dragging = false;

    public Window(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.moduleComponents = Sakura.instance.getModuleManager()
                .getModulesByCategory(category)
                .stream()
                .map(ModuleComponent::new)
                .collect(Collectors.toList());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }

        Color outlineColor = new Color(getModule(ClickGUI.class).outlineColor.get().getRGB());
        
        if (getModule(ClickGUI.class).roundedOutline.get()) {
        	RoundedUtil.drawRoundOutline(x, y, width, height, 8, getModule(ClickGUI.class).outline.getValue(), new Color(25, 25, 25), outlineColor);
        } else {
        	RenderUtil.drawBorderedRect(x, y, width, height, 1F, new Color(25, 25, 25).getRGB(), outlineColor.getRGB());
        }

        float componentOffsetY = 15;
        if (expand) {
            for (ModuleComponent module : moduleComponents) {
                module.setX(x);
                module.setY(y + componentOffsetY);
                module.setWidth(width);
                module.drawScreen(mouseX, mouseY);
                componentOffsetY += module.getHeight();
            }
        }

        height = componentOffsetY;

        String categoryName = category.getName().toUpperCase();
        FontRenderer fontRenderer = FontUtil.getFontRenderer("interSemiBold.ttf", 15);
        float centeredX = (float) (x + (width - fontRenderer.getStringWidth(categoryName)) / 2F);
        fontRenderer.drawString(categoryName, centeredX, y + 5F, -1);

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        moduleComponents.forEach(module -> module.mouseReleased(mouseX, mouseY, state));
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean isHeaderHovered = MouseUtil.isHovered(x, y, width, 14F, mouseX, mouseY);

        if (isHeaderHovered) {
            if (mouseButton == 0) {
                dragging = true;
                dragX = x - mouseX;
                dragY = y - mouseY;
            } else if (mouseButton == 1) {
                expand = !expand;
            }
        } else if (expand) {
            moduleComponents.forEach(module -> module.mouseClicked(mouseX, mouseY, mouseButton));
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
}
