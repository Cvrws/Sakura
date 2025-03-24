package cc.unknown.util.render;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.unknown.Sakura;

public class FontUtil {
    private static final Map<String, FontRenderer> fontRenderers = new ConcurrentHashMap<>();

    public static void initializeFonts() {
        try {
            Enumeration<URL> resources = FontUtil.class.getClassLoader().getResources("assets/minecraft/sakura/font");

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.toURI());

                if (file.isDirectory()) {
                    File[] fontFiles = file.listFiles((dir, name) -> name.endsWith(".ttf") || name.endsWith(".otf"));

                    if (fontFiles != null) {
                        for (File fontFile : fontFiles) {
                            getFontRenderer(fontFile.getName(), 16);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Sakura.instance.getLogger().error("Error loading fonts: " + e.getMessage());
        }

        Sakura.instance.getLogger().info("Fonts initialized successfully.");
    }

    public static FontRenderer getFontRenderer(String fontName, int size) {
        String key = fontName + size;

        return fontRenderers.computeIfAbsent(key, k -> {
            Font font = loadFont(fontName, size);
            if (font == null) return null;
            return new FontRenderer(font, true, true);
        });
    }

    private static Font loadFont(String fontName, int size) {
        try (InputStream fontStream = FontUtil.class.getClassLoader().getResourceAsStream("assets/minecraft/sakura/font/" + fontName)) {
            if (fontStream == null) {
                Sakura.instance.getLogger().error("Font file not found: " + fontName);
                return new Font("Arial", Font.PLAIN, size);
            }
            return Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            Sakura.instance.getLogger().error("Error loading font: " + fontName + " - " + e.getMessage());
            return new Font("Arial", Font.PLAIN, size);
        }
    }
}