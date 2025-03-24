package cc.unknown.managers;

import java.io.File;

public class InjectManager {
	
    public final File DLL_DIRECTORY = new File(FileManager.DIRECTORY, "dlls");

    public void init() {
        if (!DLL_DIRECTORY.exists()) {
        	DLL_DIRECTORY.mkdir();
        }
    }
}