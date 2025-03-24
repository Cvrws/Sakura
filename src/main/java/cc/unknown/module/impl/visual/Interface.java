package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.FontRenderer;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "Interface", category = Category.VISUALS)
public class Interface extends Module {
	public final MultiBoolValue elements = new MultiBoolValue("Elements", Arrays.asList(
			new BoolValue("Watermark", true),
			new BoolValue("IGN", true),
			new BoolValue("FPS", true),
			new BoolValue("Ping", true),
			new BoolValue("CPS", true),
			new BoolValue("PlayerPosition", true),
			new BoolValue("ArrayList", true), 
			new BoolValue("PotionStatus", true), 
			new BoolValue("Inventory", true)),
			this);

	public final BoolValue cFont = new BoolValue("Custom Font", this, true, () -> elements.isEnabled("ArrayList"));
	public final ModeValue fontMode = new ModeValue("Font Mode", this, () -> cFont.canDisplay() && cFont.get(), "Roboto", "Comfortaa", "Consolas", "Roboto", "Verdana", "SFUI");
	public final SliderValue fontSize = new SliderValue("Font Size", this, 18, 15, 30, cFont::get);
	public final SliderValue textHeight = new SliderValue("Text Height", this, 5, 0, 15, () -> elements.isEnabled("ArrayList"));

	public final BoolValue armorBg = new BoolValue("Armor Background", this, true, () -> elements.isEnabled("Armor"));
	public final BoolValue armorEnchanted = new BoolValue("Armor Enchanted", this, true, () -> elements.isEnabled("Armor"));
	public final BoolValue armorInfo = new BoolValue("Armor Info", this, true, () -> elements.isEnabled("Armor"));

	public final ModeValue color = new ModeValue("Arraylist Color", this, "Hyper", "Rainbow", "Fade", "Slinky", "Hyper", "Magic", "Neon", "Astolfo", "Primavera", "Ocean", "Blaze", "Ghoul", "Custom");
	public final ColorValue mainColor = new ColorValue("Main Color", new Color(128, 128, 255), this, () -> color.is("Custom") || color.is("Fade"));
	public final ColorValue secondColor = new ColorValue("Second Color", new Color(128, 255, 255), this, () -> color.is("Fade"));
	public final SliderValue fadeSpeed = new SliderValue("Fade Speed", this, 1, 1, 10, 1, () -> color.is("Fade"));

	public final BoolValue background = new BoolValue("Background", this, true, () -> elements.isEnabled("ArrayList"));
	public final ModeValue bgColor = new ModeValue("Background Color", this, background::get, "Dark", "Synced", "Custom", "Synced", "Dark");
	private final ColorValue bgCustomColor = new ColorValue("Custom Color", new Color(32, 32, 64), this, () -> bgColor.canDisplay() && bgColor.is("Custom"));
	private final SliderValue bgAlpha = new SliderValue("Background Alpha", this, 100, 1, 255, 1);
	
    public final BoolValue customScoreboard = new BoolValue("Custom Scoreboard", this, false);
    public final BoolValue hideScoreboard = new BoolValue("Hide Scoreboard", this, false,() -> !customScoreboard.get());
    public final BoolValue hideScoreRed = new BoolValue("Hide Scoreboard Red Points", this, true, customScoreboard::get);
    public final BoolValue fixHeight = new BoolValue("Fix Height", this, true, customScoreboard::get);
    public final BoolValue hideBackground = new BoolValue("Hide Background", this, true, customScoreboard::get);
    
    public final BoolValue wavey = new BoolValue("Wavey Cape", this, true);

	public int getRainbow(int counter) {
		return Color.HSBtoRGB(getRainbowHSB(counter)[0], getRainbowHSB(counter)[1], getRainbowHSB(counter)[2]);
	}

	public float[] getRainbowHSB(int counter) {
		double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * 20) / 8;
		rainbowState %= 360;

		float hue = (float) (rainbowState / 360);
		float saturation = mainColor.getSaturation();
		float brightness = mainColor.getBrightness();

		return new float[] { hue, saturation, brightness };
	}

	public int color() {
		return color(0);
	}

	public int color(int counter, int alpha) {
		int col = mainColor.get().getRGB();
		switch (color.get()) {
		case "Slinky":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.PINK, new Color(255, 0, 255), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Hyper":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.LIGHT_GRAY, new Color(52, 148, 230), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Magic":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.CYAN, new Color(142, 45, 226), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Neon":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.MAGENTA, new Color(0, 200, 255), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Astolfo":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(64, 224, 208), new Color(152, 165, 243), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Primavera":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(211, 211, 211), new Color(255, 255, 224), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Ocean":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(173, 216, 230), new Color(0, 255, 255), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Blaze":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(139, 0, 0), new Color(255, 140, 0), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Ghoul":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(255, 0, 0), new Color(0, 0, 0), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Rainbow":
			col = ColorUtil.swapAlpha(getRainbow(counter), alpha);
			break;
		case "Fade":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(mainColor.get(), secondColor.get(), 2000.0F, counter, 75L, fadeSpeed.get()).getRGB(), alpha);
			break;
		case "Custom":
			col = ColorUtil.swapAlpha(mainColor.get().getRGB(), alpha);
			break;
		default:
			break;
		}
		return new Color(col, true).getRGB();
	}

	public FontRenderer getFr() {
		FontRenderer fr = null;
		switch (fontMode.get()) {
		case "Comfortaa":
			fr = FontUtil.getFontRenderer("comfortaa.ttf", (int) fontSize.get());
			break;
		case "Consolas":
			fr = FontUtil.getFontRenderer("consolas.ttf", (int) fontSize.get());
			break;
		case "Roboto":
			fr = FontUtil.getFontRenderer("roboto.ttf", (int) fontSize.get());
			break;
		case "Verdana":
			fr = FontUtil.getFontRenderer("verdana.ttf", (int) fontSize.get());
			break;
		case "SFUI":
			fr = FontUtil.getFontRenderer("sfui.ttf", (int) fontSize.get());
			break;
		default:
			break;
		}
		return fr;
	}

	public int bgColor(int counter, int alpha) {
		int col = mainColor.get().getRGB();
		switch (bgColor.get()) {
		case "Dark":
			col = (new Color(21, 21, 21, alpha)).getRGB();
			break;
		case "Synced":
			col = new Color(ColorUtil.applyOpacity(color(counter, alpha), alpha / 255f), true).darker().darker()
					.getRGB();
			break;
		case "None":
			col = new Color(0, 0, 0, 0).getRGB();
			break;
		case "Custom":
			col = ColorUtil.swapAlpha(bgCustomColor.get().getRGB(), alpha);
			break;
		}
		return col;
	}

	public int color(int counter) {
		return color(counter, mainColor.get().getAlpha());
	}

	public int bgColor(int counter) {
		return bgColor(counter, (int) bgAlpha.get());
	}

	public int bgColor() {
		return bgColor(0);
	}
}