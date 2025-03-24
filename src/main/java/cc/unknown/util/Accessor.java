package cc.unknown.util;

import com.google.gson.Gson;

import cc.unknown.Sakura;
import cc.unknown.managers.InjectManager;
import cc.unknown.managers.ModuleManager;
import cc.unknown.module.Module;
import cc.unknown.util.client.CustomLogger;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public interface Accessor {
    static Minecraft mc = Minecraft.getMinecraft();
    
    default Sakura getInstance() {
        return Sakura.instance;
    }
    
    default boolean isInGame() {
        return mc != null || mc.thePlayer != null || mc.theWorld != null;
    }
    
    default StopWatch getStopWatch() {
    	return new StopWatch();
    }
    
    default ModuleManager getModuleManager() {
    	return getInstance().getModuleManager();
    }
    
    default InjectManager getInjectManager() {
    	return getInstance().getInjectManager();
    }
    
    default String getPrefix() {
    	return "[" + ColorUtil.pink + "S" + ColorUtil.white + "] ";
    }
    
    default String getCustomPrefix(EnumChatFormatting color, String name, EnumChatFormatting color2) {
    	return "[" + color + name + color2 + "] ";
    }
    
    default CustomLogger getLogger() {
    	return Sakura.instance.getLogger();
    }

    default <T extends Module> T getModule(final Class<T> clazz) {
        return getInstance().getModuleManager().getModule(clazz);
    }

    default Gson getGSON() {
        return getInstance().getGSON();
    }

    default boolean isEnabled(@SuppressWarnings("unchecked") Class<? extends Module>... modules) {
        for (Class<? extends Module> module : modules) {
            if (getModule(module).isEnabled()) return true;
        }
        return false;
    }
}