package cc.unknown.ui.drag.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public class ArrayListDraggable extends Drag {

    private final int PADDING = 2;

    public ArrayListDraggable() {
        super("ArrayList");
        this.x = 10;
        this.y = 10;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (!shouldRender()) return;

        int middle = sr.getScaledWidth() / 2;
        List<Module> enabledModules = getEnabledModules();

        float offset = 0;
        float lastWidth = 0;

        for (int i = 0; i < enabledModules.size(); i++) {
            Module module = enabledModules.get(i);
            int width = getModuleWidth(module);
            int height = getModuleHeight() - 2;

            renderModule(module, renderX, renderY, offset, width, height, 1.0f, middle, lastWidth, i, enabledModules.size());

            if (!module.isHidden()) {
                offset = calculateNextOffset(module, height, offset);
            }
            lastWidth = width;
        }
    }

    private List<Module> getEnabledModules() {
        List<Module> enabledModules = new ArrayList<>();
        for (Module module : Sakura.instance.getModuleManager().getModules()) {
            if (module.isHidden()) {
                continue;
            }
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        enabledModules.sort(Comparator.comparing(this::getModuleWidth).reversed());
        return enabledModules;
    }

    private int getModuleWidth(Module module) {
        return (int) (!setting.cFont.get() ? mc.fontRendererObj.getStringWidth(module.getName()) : setting.getFr().getStringWidth(module.getName()));
    }

    private int getModuleHeight() {
        return !setting.cFont.get() ? mc.fontRendererObj.FONT_HEIGHT : setting.getFr().getHeight();
    }

    private void renderModule(Module module, float localX, float localY, float offset, int width, int height,  float alphaAnimation, int middle, float lastWidth, int index, int totalModules) {
        if (setting.background.get()) {
            renderBackground(localX, localY, offset, width, height, middle,index);
        }
        
        renderText(module, localX, localY, offset, width, alphaAnimation, middle,index);
    }

    private void renderBackground(float localX, float localY, float offset, int width, int height, int middle, int index) {
        if (localX < middle) {
            RenderUtil.drawRect(localX - PADDING, localY + offset, width + 3, height + PADDING + setting.textHeight.get(), setting.bgColor(index));
        } else {
        	if (setting.cFont.get()) {
        		RenderUtil.drawRect(localX + this.width - 4 - width, localY + offset + 2, width + 3, height + PADDING + setting.textHeight.get(), setting.bgColor(index));
        	} else {
        		RenderUtil.drawRect(localX + this.width - 4 - width, localY + offset - 2, width + 3, height + PADDING + setting.textHeight.get(), setting.bgColor(index));
        	}
        }
    }

    private void renderText(Module module, float localX, float localY, float offset, int width, float alphaAnimation, int middle, int index) {
        String text = module.getName();
        int color = ColorUtil.swapAlpha(setting.color(index), (int) alphaAnimation * setting.mainColor.get().getAlpha());
        float textY = localY + offset + (setting.cFont.get() ? 6 : 2);

        if (localX < middle) {
            if (!setting.cFont.get()) {
                mc.fontRendererObj.drawStringWithShadow(text, localX, textY, color);
            } else {
                setting.getFr().drawStringWithShadow(text, localX, textY, color);
            }
        } else {
            float textX = localX - width + this.width - 2;
            if (!setting.cFont.get()) {
                mc.fontRendererObj.drawStringWithShadow(text, textX, textY, color);
            } else {
                setting.getFr().drawStringWithShadow(text, textX, textY, color);
            }
        }
    }

    private float calculateNextOffset(Module module, int height, float offset) {
        return (float) (offset + (1 * (height + setting.textHeight.get())) + PADDING);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("ArrayList");
    }
}
