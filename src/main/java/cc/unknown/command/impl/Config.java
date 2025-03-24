package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.util.client.ModuleUtil;

public final class Config extends Command {

    public Config() {
        super("cfg");
    }
    
    private enum Action {
        LOAD, SAVE, LIST, CREATE, REMOVE, FOLDER, CURRENT;

        public static Action fromString(String action) {
            try {
                return Action.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    @Override
    public void execute(final String[] args) {
        if (args.length < 2) {
            error("Uso: .cfg <list/folder/current/save/remove/create/load> [nombre]");
            return;
        }

        Action action = Action.fromString(args[1]);
        if (action == null) {
            error("Acción inválida. Usa: .cfg list/folder/current/save/remove/create/load");
            return;
        }

        if (action == Action.LIST || action == Action.FOLDER || action == Action.CURRENT) {
            if (args.length > 2) {
                error("Uso incorrecto. El comando '.cfg " + args[1] + "' no requiere argumentos adicionales.");
                return;
            }
        } else {
            if (args.length < 3) {
                error("Uso: .cfg " + args[1].toLowerCase() + " <nombre>");
                return;
            }
        }

        String configName = args.length > 2 ? args[2] : null;

        switch (action) {
            case LIST:
                handleList();
                break;
            case FOLDER:
                handleOpenFolder();
                break;
            case CURRENT:
                handleCurrent();
                break;
            case LOAD:
                handleLoad(configName);
                break;
            case SAVE:
                handleSave(configName);
                break;
            case CREATE:
                handleCreate(configName);
                break;
            case REMOVE:
                handleRemove(configName);
                break;
            default:
                error("Acción desconocida.");
        }
    }

    private void handleList() {
        String[] configs = getConfigList();
        if (configs.length == 0) {
            warning("No hay configuraciones guardadas.");
        } else {
            success("Configuraciones disponibles: " + String.join(", ", configs));
        }
    }

    private void handleOpenFolder() {
        File directory = Sakura.instance.getCfgManager().CONFIG_DIRECTORY;
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(directory);
                success("Se ha abierto la carpeta de configuraciones.");
            } catch (IOException e) {
                error("No se pudo abrir la carpeta.");
                e.printStackTrace();
            }
        } else {
            warning("Abrir carpetas no es compatible en este sistema.");
        }
    }

    private void handleCurrent() {
        String currentConfig = Sakura.instance.getCfgManager().getCurrentConfig();
        if (currentConfig != null) {
            success("Configuración actual: " + currentConfig);
        } else {
            warning("No hay ninguna configuración cargada.");
        }
    }

    private void handleLoad(String configName) {
        ModuleUtil cfg = new ModuleUtil(configName);
        if (Sakura.instance.getCfgManager().loadConfig(cfg)) {
            Sakura.instance.getCfgManager().setCurrentConfig(configName);
            success("Configuración cargada: " + configName);
        } else {
            warning("No se pudo cargar la configuración: " + configName);
        }
    }

    private void handleSave(String configName) {
        ModuleUtil cfg = new ModuleUtil(configName);
        if (Sakura.instance.getCfgManager().saveConfig(cfg)) {
            success("Configuración guardada: " + configName);
        } else {
            error("Error al guardar la configuración: " + configName);
        }
    }

    private void handleCreate(String configName) {
        File configFile = new File(Sakura.instance.getCfgManager().CONFIG_DIRECTORY, configName + ".json");
        try {
            if (configFile.createNewFile()) {
                Sakura.instance.getCfgManager().setCurrentConfig(configName);
                success("Configuración creada y establecida como actual: " + configName);
                handleSave(configName);
            } else {
                warning("La configuración ya existe: " + configName);
            }
        } catch (IOException e) {
            error("No se pudo crear la configuración: " + configName);
            e.printStackTrace();
        }
    }

    private void handleRemove(String configName) {
        File configFile = new File(Sakura.instance.getCfgManager().CONFIG_DIRECTORY, configName + ".json");
        if (configFile.exists()) {
            if (configFile.delete()) {
                success("Configuración eliminada: " + configName);
            } else {
                warning("No se pudo eliminar la configuración: " + configName);
            }
        } else {
            error("La configuración no existe: " + configName);
        }
    }

    private String[] getConfigList() {
        File directory = Sakura.instance.getCfgManager().CONFIG_DIRECTORY;
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) {
            return new String[0];
        }
        String[] configs = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            configs[i] = files[i].getName().replaceFirst("\\.json$", "");
        }
        return configs;
    }
}
