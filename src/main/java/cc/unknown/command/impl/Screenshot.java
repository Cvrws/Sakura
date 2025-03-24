package cc.unknown.command.impl;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.util.client.FileTransferable;
import net.minecraft.util.ChatComponentText;

public final class Screenshot extends Command {

    public Screenshot() {
        super("screenshot");
    }
    
    @Override
    public void execute(final String[] args) {
        File directory = Sakura.instance.getScreenshotManager().SS_DIRECTORY;

        if (args.length < 1) {
            return;
        }

        String action = args[0];

        if (action.equalsIgnoreCase("open")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(directory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                warning("Not supported on this system.");
            }
            return;
        }

        if (args.length < 2) {
            return;
        }

        File file = new File(directory, args[1]);

        if (!file.exists()) {
            return;
        }

        switch (action.toLowerCase()) {
            case "copy":
                FileTransferable selection = new FileTransferable(file);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                break;

            case "del":
                if (file.delete()) {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(args[1] + " ha sido eliminado."));
                } else {
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("No se pudo eliminar " + args[1]));
                }
                break;
        }
    }

}
