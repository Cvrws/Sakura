package cc.unknown.util.client;

import java.io.File;
import java.lang.reflect.Field;

import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;

public class ReflectUtil implements Accessor {	
    public static boolean isOptifineLoaded() {
        File modsFolder = new File(mc.mcDataDir, "run/mods");

        if (!modsFolder.exists() || !modsFolder.isDirectory()) {
            modsFolder = new File(mc.mcDataDir, "mods");
        }

        if (modsFolder.exists() && modsFolder.isDirectory()) {
            File[] modFiles = modsFolder.listFiles((dir, name) -> name.toLowerCase().contains("optifine") && name.endsWith(".jar"));

            if (modFiles != null && modFiles.length > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShaders() {
        try {
            Class<?> configClass = Class.forName("Config");
            return (boolean) configClass.getMethod("isShaders").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isZoomMode() {
    	try {
    		Class<?> configClass = Class.forName("Config");
    		return (boolean) configClass.getMethod("zoomMode").invoke(null);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return false;
    }

    public static void setGameSetting(Minecraft mc, String fieldName, boolean value) {
        try {
            Field field = mc.gameSettings.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(mc.gameSettings, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}