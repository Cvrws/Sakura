package cc.unknown.util.client;

import java.io.File;

import com.google.gson.JsonObject;

import cc.unknown.managers.FileManager;

public class ConfigUtil {
    private final File file;
    private final String name;

    public ConfigUtil(String name) {
        this.name = name;
        this.file = new File(FileManager.DIRECTORY + "/configs", name + ".json");
    }

    public void loadConfig(JsonObject object){

    }

    public JsonObject saveConfig(){
        return null;
    }

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}
}