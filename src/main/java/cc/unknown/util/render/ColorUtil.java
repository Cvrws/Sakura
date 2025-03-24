package cc.unknown.util.render;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.client.MathUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ColorUtil {
	
	public static EnumChatFormatting yellow = EnumChatFormatting.YELLOW;
	public static EnumChatFormatting red = EnumChatFormatting.RED;
	public static EnumChatFormatting reset = EnumChatFormatting.RESET;
	public static EnumChatFormatting white = EnumChatFormatting.RESET;
	public static EnumChatFormatting aqua = EnumChatFormatting.AQUA;
	public static EnumChatFormatting gray = EnumChatFormatting.GRAY;
	public static EnumChatFormatting green = EnumChatFormatting.GREEN;
	public static EnumChatFormatting blue = EnumChatFormatting.BLUE;
	public static EnumChatFormatting black = EnumChatFormatting.BLACK;
	public static EnumChatFormatting gold = EnumChatFormatting.GOLD;
	
	public static EnumChatFormatting darkAqua = EnumChatFormatting.DARK_AQUA;
	public static EnumChatFormatting darkGray = EnumChatFormatting.DARK_GRAY;
	public static EnumChatFormatting darkPurple = EnumChatFormatting.DARK_PURPLE;
	public static EnumChatFormatting darkBlue = EnumChatFormatting.DARK_BLUE;
	public static EnumChatFormatting darkGreen = EnumChatFormatting.DARK_GREEN;
	public static EnumChatFormatting darkRed = EnumChatFormatting.DARK_RED;

	public static EnumChatFormatting pink = EnumChatFormatting.LIGHT_PURPLE;
	
	public static EnumChatFormatting underline = EnumChatFormatting.UNDERLINE;
	
    private static Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)§([0-9A-FK-OR])");
   	public static String usu = " ?§r§{0,3}§8§8\\[§r§f§fUsu§r§8§8\\]| ?§8\\[§fUsu§8\\]";
   	public static String jup = " ?§r§{0,3}§8§8\\[§r§b§bJup§r§8§8\\]| ?§8\\[§bJup§8\\]";

	public static String getPrefix(String rank, EnumChatFormatting rankColor) {
		return darkGray + "[" + rankColor + rank + darkGray + "] " + rankColor;
	}
	
    public static int swapAlpha(int color, float alpha) {
        int f = color >> 16 & 0xFF;
        int f1 = color >> 8 & 0xFF;
        int f2 = color & 0xFF;
        return getColor(f, f1, f2, (int) alpha);
    }
	
    public static void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0F;
        final float r = (hex >> 16 & 0xFF) / 255.0F;
        final float g = (hex >> 8 & 0xFF) / 255.0F;
        final float b = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }
    
    public static Color colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        return colorSwitch(firstColor, secondColor, time, index, timePerIndex, speed, 255.0D);
    }
    
    public static int getColorFromPercentage(float percentage) {
        return Color.HSBtoRGB(Math.min(1.0F, Math.max(0.0F, percentage)) / 3, 0.9F, 0.9F);
    }

    public static Color colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed, double alpha) {
        long now = (long) (speed * (double) System.currentTimeMillis() + (double) ((long) index * timePerIndex));
        float redDiff = (float) (firstColor.getRed() - secondColor.getRed()) / time;
        float greenDiff = (float) (firstColor.getGreen() - secondColor.getGreen()) / time;
        float blueDiff = (float) (firstColor.getBlue() - secondColor.getBlue()) / time;
        int red = Math.round((float) secondColor.getRed() + redDiff * (float) (now % (long) time));
        int green = Math.round((float) secondColor.getGreen() + greenDiff * (float) (now % (long) time));
        int blue = Math.round((float) secondColor.getBlue() + blueDiff * (float) (now % (long) time));
        float redInverseDiff = (float) (secondColor.getRed() - firstColor.getRed()) / time;
        float greenInverseDiff = (float) (secondColor.getGreen() - firstColor.getGreen()) / time;
        float blueInverseDiff = (float) (secondColor.getBlue() - firstColor.getBlue()) / time;
        int inverseRed = Math.round((float) firstColor.getRed() + redInverseDiff * (float) (now % (long) time));
        int inverseGreen = Math.round((float) firstColor.getGreen() + greenInverseDiff * (float) (now % (long) time));
        int inverseBlue = Math.round((float) firstColor.getBlue() + blueInverseDiff * (float) (now % (long) time));

        return now % ((long) time * 2L) < (long) time ? (new Color(inverseRed, inverseGreen, inverseBlue, (int) alpha)) : (new Color(red, green, blue, (int) alpha));
    }
    
    public static int getRedFromColor(int color) {
        return color >> 16 & 0xFF;
    }

    public static int getGreenFromColor(int color) {
        return color >> 8 & 0xFF;
    }

    public static int getBlueFromColor(int color) {
        return color & 0xFF;
    }
    
    public static int getAlphaFromColor(int color) {
        return color >> 24 & 0xFF;
    }
    
    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }
    
    public static Color darker(final Color c, final double FACTOR) {
        return new Color(Math.max((int) (c.getRed() * FACTOR), 0),
                Math.max((int) (c.getGreen() * FACTOR), 0),
                Math.max((int) (c.getBlue() * FACTOR), 0),
                c.getAlpha());
    }

    public static int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity);
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }


    public static Color withAlpha(final Color color, final int alpha) {
        if (alpha == color.getAlpha()) return color;
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtil.clamp(0, 255, alpha));
    }
    
	public static Color getAlphaColor(Color color, int alpha) {
	    int clampedAlpha = MathHelper.clamp_int(alpha, 0, 255);
	    if (color.getAlpha() == clampedAlpha) {
	        return color;
	    }
	    return new Color(color.getRed(), color.getGreen(), color.getBlue(), clampedAlpha);
	}
    
	public static Color blend(Color color, Color color1, double d0) {
		float f = (float) d0;
		float f1 = 1.0F - f;
		float[] afloat = new float[3];
		float[] afloat1 = new float[3];
		color.getColorComponents(afloat);
		color1.getColorComponents(afloat1);
		return new Color(afloat[0] * f + afloat1[0] * f1, afloat[1] * f + afloat1[1] * f1,
				afloat[2] * f + afloat1[2] * f1);
	}
	
    public static int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 0.75F, 1.0F) | 0xFF000000;
    }
	
    public static Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5);
    }
	
	public static int getTeamColor(EntityPlayer player) {
		String name = player.getDisplayName().getFormattedText();
		name = removeFormatCodes(name);
        if (name.isEmpty() || !name.startsWith("§") || name.charAt(1) == 'f') {
            return -1;
        }
        switch (name.charAt(1)) {
            case '0':
                return -16777216;
            case '1':
                return -16777046;
            case '2':
                return -16733696;
            case '3':
                return -16733526;
            case '4':
                return -5636096;
            case '5':
                return -5635926;
            case '6':
                return -22016;
            case '7':
                return -5592406;
            case '8':
                return -11184811;
            case '9':
                return -11184641;
            case 'a':
                return -11141291;
            case 'b':
                return -11141121;
            case 'c':
                return -43691;
            case 'd':
                return -43521;
            case 'e':
                return -171;
        }
        return -1;
    }
	
    public static int interpolateColor2(Color color1, Color color2, float fraction) {
        int red = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * fraction);
        int green = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fraction);
        int alpha = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * fraction);
        try {
            return new Color(red, green, blue, alpha).getRGB();
        } catch (Exception ex) {
            return 0xffffffff;
        }
    }
	
    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        double inverse_percent;
        int redPart;
        if(offset > 1.0D) {
            inverse_percent = offset % 1.0D;
            redPart = (int)offset;
            offset = redPart % 2 == 0?inverse_percent:1.0D - inverse_percent;
        }
        inverse_percent = 1.0D - offset;
        redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offset);
        int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offset);
        int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }
	
    private static String removeFormatCodes(String str) {
        return str.replace("§k", "").replace("§l", "").replace("§m", "").replace("§n", "").replace("§o", "").replace("§r", "");
    }
    
    public static Color colorFromInt(int color) {
        Color c = new Color(color);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
    }
    
    public static int getColorFromTags(String displayName) {
        displayName = removeFormatCodes(displayName);
        if (displayName.isEmpty() || !displayName.startsWith("§") || displayName.charAt(1) == 'f') {
            return new Color(255, 255, 255).getRGB();
        }
        return getColorFromCode(displayName).getRGB();
    }
    
    private static Color getColorFromCode(String input) {
        Matcher matcher = COLOR_CODE_PATTERN.matcher(input);
        if (matcher.find()) {
            char code = matcher.group(1).charAt(0);
            switch (code) {
                case '0': return new Color(0, 0, 0);
                case '1': return new Color(0, 0, 170);
                case '2': return new Color(0, 170, 0);
                case '3': return new Color(0, 170, 170);
                case '4': return new Color(170, 0, 0);
                case '5': return new Color(170, 0, 170);
                case '6': return new Color(255, 170, 0);
                case '7': return new Color(170, 170, 170);
                case '8': return new Color(85, 85, 85);
                case '9': return new Color(85, 85, 255);
                case 'a': return new Color(85, 255, 85);
                case 'b': return new Color(85, 255, 255);
                case 'c': return new Color(255, 85, 85);
                case 'd': return new Color(255, 85, 255);
                case 'e': return new Color(255, 255, 85);
                case 'f': return new Color(255, 255, 255);
                default: return new Color(255, 255, 255);
            }
        }
        return new Color(255, 255, 255);
    }
    
    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }
    
    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }
}
