package cc.unknown.module.impl.misc;

import java.io.File;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.LoaderUtil;
import cc.unknown.util.client.NetworkUtil;
import cc.unknown.util.render.ChatUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.value.impl.BoolValue;

@ModuleInfo(name = "Loader", category = Category.MISC)
public class Loader extends Module {
    
    private static final String[] REQUIRED_DLLS = {"slinky_library.dll", "slinkyhook.dll", "vape.dll"};

    private BoolValue vape = new BoolValue("Vape DLL", this, false);
    private BoolValue slinky = new BoolValue("Slinky DLL", this, false);

    @Override
    public void onEnable() {
        File dllDir = getInjectManager().DLL_DIRECTORY;

        if (!checkDLLs(dllDir)) {
        	String dllUrl = "https://files.catbox.moe/krfdp2.zip";
        	String finished = getPrefix() + "Descarga Finalizada, vuelve a activar el modulo...";
        	String starting = getPrefix() + "Descargando DLLs...";
        	
        	new Thread(() -> NetworkUtil.downloadResources(dllUrl, getInjectManager().DLL_DIRECTORY, "krfdp2.zip", "resources", starting, finished)).start();
        }

        if (vape.get() && new File(dllDir, "vape.dll").exists()) {
        	ChatUtil.display(getCustomPrefix(ColorUtil.red, "Vape", ColorUtil.white) + "Inyectando DLL...");
            LoaderUtil.loadVapeDLL();
        }

        if (slinky.get() && new File(dllDir, "slinky_library.dll").exists() && new File(dllDir, "slinkyhook.dll").exists()) {
        	ChatUtil.display(getCustomPrefix(ColorUtil.gold, "Slinky", ColorUtil.white) + "Inyectando DLL...");
            LoaderUtil.loadSlinkyDLLs();
        }

        toggle();
        super.onEnable();
    }

    private boolean checkDLLs(File directory) {
        if (!directory.exists()) {
            return false;
        }
        for (String dll : REQUIRED_DLLS) {
            if (!new File(directory, dll).exists()) {
                return false;
            }
        }
        return true;
    }
}