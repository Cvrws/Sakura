package cc.unknown.util.client;

import cc.unknown.util.Accessor;

public class MouseUtil implements Accessor {
    public static boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}