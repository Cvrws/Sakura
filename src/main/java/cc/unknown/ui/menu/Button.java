package cc.unknown.ui.menu;

import java.awt.Color;

import cc.unknown.Sakura;
import cc.unknown.module.impl.visual.MainMenu;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton {
	private int x;
	private int y;
	private int width;
	private int height;
	private String text;
	double size;

	public Button(final int button, final int x, final int y, final int width, final int height, final String text) {
		super(button, x, y, width, height, text);
		this.size = 0.0;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
	}

	public Button(final int i, final int j, final int k, final String stringParams) {
		this(i, j, k, 200, 20, stringParams);
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
		final boolean isOverButton = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		final int color = isOverButton ? new Color(255, 255, 255).getRGB() : new Color(200, 200, 200).getRGB();
		
		if (Sakura.instance.getModuleManager().getModule(MainMenu.class).roundedButtons.get()) {
			RoundedUtil.drawRound(x, y, width, height, 8, new Color(1, 1, 1, 150));
		} else {
			RenderUtil.drawRect(x, y, width, height, new Color(1, 1, 1, 150));
		}

		int textWidth = (int) FontUtil.getFontRenderer("comfortaa.ttf", 16).getStringWidth(text);
		int textHeight = (int) FontUtil.getFontRenderer("comfortaa.ttf", 16).getHeight();
		float centeredX = x + (width / 2.0f) - (textWidth / 2.0f);
		float centeredY = y + (height / 1.5f) - (textHeight / 1.5f);
		FontUtil.getFontRenderer("comfortaa.ttf", 16).drawStringWithShadow(text, centeredX, centeredY, color);
	}
}