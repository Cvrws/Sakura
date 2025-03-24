package cc.unknown.ui.click;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.module.api.Category;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.lists.SList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class AstolfoGui extends GuiScreen {
    private final SList<Window> windows = new SList<>();
    private int guiYMoveLeft = 0;
    private static final int SCROLL_SPEED = 30;

    public AstolfoGui() {
        ScaledResolution sr = new ScaledResolution(Accessor.mc);
        float screenWidth = sr.getScaledWidth();
        float screenHeight = sr.getScaledHeight();

        float buttonWidth = 100;
        float spacingX = 50;
        float spacingY = 40;

        float totalRowWidth = (buttonWidth * 3) + (spacingX * 2);
        float startX = (screenWidth - totalRowWidth) / 2.0f;
        float startY = screenHeight / 4.0f;

        for (int i = 0; i < Category.values().length; i++) {
            float x = startX + (i % 3) * (buttonWidth + spacingX);
            float y = startY + (i / 3) * (buttonWidth / 2 + spacingY);
            windows.add(new Window(Category.values()[i], x, y));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Accessor.mc);
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 150));

        if (guiYMoveLeft != 0) {
            int step = (int) (guiYMoveLeft * 0.15);
            if (step == 0) {
                guiYMoveLeft = 0;
            } else {
                for (Window window : windows) {
                    window.setY(window.getY() + step);
                }
                guiYMoveLeft -= step;
            }
        }

        windows.forEach(window -> window.drawScreen(mouseX, mouseY));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(window -> window.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        windows.forEach(window -> window.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            mouseScrolled(dWheel);
        }
    }

    public void mouseScrolled(int dWheel) {
        if (dWheel > 0) {
            guiYMoveLeft += SCROLL_SPEED;
        } else if (dWheel < 0) {
            guiYMoveLeft -= SCROLL_SPEED;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_UP) {
            guiYMoveLeft += SCROLL_SPEED;
        } else if (keyCode == Keyboard.KEY_DOWN) {
            guiYMoveLeft -= SCROLL_SPEED;
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
