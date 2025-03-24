package cc.unknown.managers;

import java.io.File;

public class ScreenshotManager {

    public final File SS_DIRECTORY = new File(FileManager.DIRECTORY, "screenshots");

    public void init() {
        if (!SS_DIRECTORY.exists()) {
        	SS_DIRECTORY.mkdir();
        }
    }
}