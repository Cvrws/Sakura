package cc.unknown.managers;

import java.io.File;

import cc.unknown.Sakura;
import net.minecraft.client.Minecraft;

public class FileManager {

    public static final File DIRECTORY = new File(Minecraft.getMinecraft().mcDataDir, Sakura.NAME);

    public void init() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdir();
        }
    }
}