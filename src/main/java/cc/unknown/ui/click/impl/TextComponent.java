package cc.unknown.ui.click.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import cc.unknown.ui.click.Component;
import cc.unknown.util.client.MouseUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.value.impl.TextValue;

public class TextComponent extends Component {
	private final TextValue setting;
	private boolean inputting;
	private String text = "";

	public TextComponent(TextValue setting) {
		this.setting = setting;
		setHeight(11);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY) {
		text = setting.get();
		if (setting.isOnlyNumber() && !NumberUtils.isNumber(text)) {
			text = text.replaceAll("[a-zA-Z]", "");
		}
        boolean showCursor = inputting && text.length() < 59 && (System.nanoTime() / 500_000_000L) % 2 == 0;
        String displayText = text.isEmpty() && !inputting ? "Empty..." : text + (showCursor ? "|" : "");

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(setting.getName() + ":", getX() + 5F, getY() + 4F, -1);
        drawTextWithLineBreaks(displayText, getX() + 72, getY() + 4, 90);

        super.drawScreen(mouseX, mouseY);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(getX(), getY(), 100F, getHeight(), mouseX, mouseY)) {
			inputting = !inputting;
		} else {
			inputting = false;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (setting.isOnlyNumber() && !NumberUtils.isNumber(String.valueOf(typedChar))) {
			return;
		}
		
		if (inputting) {
			if (keyCode == Keyboard.KEY_BACK) {
				deleteLastCharacter();
			}

			if (text.length() < 18 && (Character.isLetterOrDigit(typedChar) || keyCode == Keyboard.KEY_SPACE)) {
				text += typedChar;
				setting.setText(text);
			}
		}
		super.keyTyped(typedChar, keyCode);
	}

	private void drawTextWithLineBreaks(String text, float x, float y, float maxWidth) {
		String[] lines = text.split("\n");
		float currentY = y;

		for (String line : lines) {
			java.util.List<String> wrappedLines = wrapText(line, 6, maxWidth);
			for (String wrappedLine : wrappedLines) {

				FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(wrappedLine, x, currentY,
						ColorUtil.interpolateColor2(new Color(-1).darker(), new Color(-1), 250));
				currentY += FontUtil.getFontRenderer("interSemiBold.ttf", 13).getHeight();
			}
		}
	}

	private java.util.List<String> wrapText(String text, float size, float maxWidth) {
		java.util.List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder currentLine = new StringBuilder();

		for (String word : words) {
			if (FontUtil.getFontRenderer("interSemiBold.ttf", 13).getStringWidth(word) <= maxWidth) {
				if (FontUtil.getFontRenderer("interSemiBold.ttf", 13)
						.getStringWidth(currentLine.toString() + word) <= maxWidth) {
					currentLine.append(word).append(" ");
				} else {
					lines.add(currentLine.toString());
					currentLine = new StringBuilder(word).append(" ");
				}
			} else {
				if (!currentLine.toString().isEmpty()) {
					lines.add(currentLine.toString());
					currentLine = new StringBuilder();
				}
				currentLine = breakAndAddWord(word, currentLine, size, lines);
			}
		}

		if (!currentLine.toString().isEmpty()) {
			lines.add(currentLine.toString());
		}

		return lines;
	}

	private void deleteLastCharacter() {
		if (!text.isEmpty()) {
			text = text.substring(0, text.length() - 1);
			setting.setText(text);
		}
	}

	private StringBuilder breakAndAddWord(String word, StringBuilder currentLine, float maxWidth, List<String> lines) {
		int wordLength = word.length();
		for (int i = 0; i < wordLength; i++) {
			char c = word.charAt(i);
			String nextPart = currentLine.toString() + c;
			if (FontUtil.getFontRenderer("interSemiBold.ttf", 13).getStringWidth(nextPart) <= maxWidth) {
				currentLine.append(c);
			} else {
				lines.add(currentLine.toString());
				currentLine = new StringBuilder(String.valueOf(c));
			}
		}
		return currentLine;
	}

	@Override
	public boolean isVisible() {
		return setting.visible.get();
	}
}
