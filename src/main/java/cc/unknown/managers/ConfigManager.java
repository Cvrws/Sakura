package cc.unknown.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cc.unknown.Sakura;
import cc.unknown.util.client.ConfigUtil;
import cc.unknown.util.client.DragUtil;
import cc.unknown.util.client.ModuleUtil;

public final class ConfigManager {
	
    public final File CONFIG_DIRECTORY = new File(FileManager.DIRECTORY, "configs");

    public ConfigManager() {
        if (!CONFIG_DIRECTORY.exists()) {
            CONFIG_DIRECTORY.mkdir();
        }
    }

    private final ModuleUtil setting = new ModuleUtil("latest");
    private final DragUtil elements = new DragUtil("elements");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String currentConfig = "latest";

    public void init() {
        loadConfigs();
    }

    public boolean loadConfig(ConfigUtil config) {
        if (config == null) {
            Sakura.instance.getLogger().warn("Attempted to load a null configuration.");
            return false;
        }

        try (FileReader reader = new FileReader(config.getFile())) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
            config.loadConfig(jsonObject);
            Sakura.instance.getLogger().info("Loaded config: " + config.getName());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean saveConfig(ConfigUtil config) {
        if (config == null) {
            return false;
        }

        JsonObject jsonObject = config.saveConfig();
        String jsonString = gson.toJson(jsonObject);

        try (FileWriter writer = new FileWriter(config.getFile())) {
            writer.write(jsonString);
            Sakura.instance.getLogger().info("Saved config: " + config.getName());
            return true;
        } catch (IOException e) {
            Sakura.instance.getLogger().error("Failed to save config: " + config.getName(), e);
            return false;
        }
    }
    
    public void saveConfigs() {
        if (!saveConfig(setting)) {
            Sakura.instance.getLogger().warn("Failed to save setting config.");
        }
        if (!saveConfig(elements)) {
        	Sakura.instance.getLogger().warn("Failed to save elements config.");
        }
    }

    public void loadConfigs() {
        if (!loadConfig(setting)) {
            Sakura.instance.getLogger().warn("Failed to load setting config.");
        }
        
        if (!loadConfig(elements)) {
        	Sakura.instance.getLogger().warn("Failed to load elements config.");
        }
    }

	public String getCurrentConfig() {
		return currentConfig;
	}

	public void setCurrentConfig(String currentConfig) {
		this.currentConfig = currentConfig;
	}

	public ModuleUtil getSetting() {
		return setting;
	}

	public static Gson getGson() {
		return gson;
	}
}