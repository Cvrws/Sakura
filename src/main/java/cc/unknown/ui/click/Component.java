package cc.unknown.ui.click;

import java.awt.Color;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.RoundedUtil;

public class Component implements IComponent, Accessor {

	private float x, y, width, height;
	private Color color = getModule(ClickGUI.class).outlineColor.get();
	private int colorRGB = color.getRGB();

	public void drawBackground(Color color) {
		RenderUtil.drawRect(x, y, width, height, color.getRGB());
	}

	public void drawRoundBackground(Color color) {
		RoundedUtil.drawRound(x, y, width, height, 3, color);
	}

	public boolean isHovered(float mouseX, float mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public boolean isHovered(float mouseX, float mouseY, float height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public boolean isVisible() {
		return true;
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getColorRGB() {
		return colorRGB;
	}

	public void setColorRGB(int colorRGB) {
		this.colorRGB = colorRGB;
	}
}