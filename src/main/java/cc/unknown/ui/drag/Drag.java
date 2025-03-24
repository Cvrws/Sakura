package cc.unknown.ui.drag;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cc.unknown.Sakura;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;

public abstract class Drag implements Accessor {
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("x")
    public float x;
    @Expose
    @SerializedName("y")
    public float y;
    protected float renderX, renderY;
    public float width;
    public float height;
    public boolean dragging;
    private int dragX, dragY;
    public int align;
    protected Interface setting = Sakura.instance.getModuleManager().getModule(Interface.class);

    public Drag(String name) {
        this.name = name;
        this.x = 0f;
        this.y = 0f;
        this.width = 100f;
        this.height = 100f;
        this.align = DragAlign.LEFT | DragAlign.TOP;
    }

    public Drag(String name, int align) {
        this(name);
        this.align = align;
    }

    public abstract void render(ScaledResolution sr);
    
    public void updatePos(ScaledResolution sr) {

        renderX = x * sr.getScaledWidth();
        renderY = y * sr.getScaledHeight();

        if (renderX < 0f) x = 0f;
        if (renderX > sr.getScaledWidth() - width) x = (sr.getScaledWidth() - width) / sr.getScaledWidth();
        if (renderY < 0f) y = 0f;
        if (renderY > sr.getScaledHeight() - height) y = (sr.getScaledHeight() - height) / sr.getScaledHeight();

        if (align == (DragAlign.LEFT | DragAlign.TOP)) return;

        if ((align & DragAlign.RIGHT) != 0) {
            renderX -= width;
        } else if ((align & DragAlign.CENTER) != 0) {
            renderX -= width / 2f;
        }

        if ((align & DragAlign.BOTTOM) != 0) {
            renderY -= height;
        } else if ((align & DragAlign.MIDDLE) != 0) {
            renderY -= height / 2f;
        }
    }

    public final void onChatGUI(int mouseX, int mouseY, boolean drag, ScaledResolution sr) {
        boolean hovering = MouseUtil.isHovered(renderX, renderY, width, height, mouseX, mouseY);

        if (dragging) {
            RoundedUtil.drawRoundOutline(renderX, renderY, width, height, 2f, 0.05f, new Color(0, 0, 0, 0), Color.WHITE);
        }

        if (hovering && Mouse.isButtonDown(0) && !dragging && drag) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        if (!Mouse.isButtonDown(0)) dragging = false;

        if (dragging) {
            float deltaX = (float) (mouseX - dragX) / sr.getScaledWidth();
            float deltaY = (float) (mouseY - dragY) / sr.getScaledHeight();

            x += deltaX;
            y += deltaY;

            dragX = mouseX;
            dragY = mouseY;
        }

    }

    public abstract boolean shouldRender();
}
