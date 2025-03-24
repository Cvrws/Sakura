package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.render.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public final class Name extends Command {

    public Name() {
        super("ign");
    }

    @Override
    public void execute(final String[] args) {
        final String name = mc.thePlayer.getName();

        GuiScreen.setClipboardString(name);
        ChatUtil.display("Copied your username to clipboard. (%s)", name);
    }
}
