package cc.unknown.util.client;

import java.util.Arrays;

import cc.unknown.util.render.FontRenderer;

public class StringUtil {

    public static String getToFit(FontRenderer font, String string, double length) {
        double l = 0;
        int index = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (l < length && index < string.length()) {
            String character = String.valueOf(string.charAt(index));
            l += font.getStringWidth(character);
            index++;
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }
    
    public static String upperSnakeCaseToPascal(String string) {
        return string.charAt(0) + string.substring(1).toLowerCase();
    }

    public static byte[] encodeString(String text) {
    	return text.getBytes();
    }
    
    public static boolean containsAny(String source, String... targets) {
        return Arrays.stream(targets).anyMatch(source::contains);
    }
}
