package cc.unknown.util.client;

import java.io.File;

import cc.unknown.Sakura;

public class LoaderUtil {
    private static final String[] SLINKY_DLLS = {"slinky_library.dll", "slinkyhook.dll"};

    public static void loadSlinkyDLLs() {
        for (String dllName : SLINKY_DLLS) {
            injectDlls(new File(Sakura.instance.getInjectManager().DLL_DIRECTORY, dllName));
        }
    }

    public static void loadVapeDLL() {
        injectDlls(new File(Sakura.instance.getInjectManager().DLL_DIRECTORY, "vape.dll"));
    }

    private static void injectDlls(File dllFile) {
        try {
            if (!dllFile.exists()) {
                return;
            }

            System.load(dllFile.getAbsolutePath());
            System.out.println("DLL Cargada: " + dllFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}