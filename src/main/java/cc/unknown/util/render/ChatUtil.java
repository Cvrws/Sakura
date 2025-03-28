package cc.unknown.util.render;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.PacketUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;

public class ChatUtil implements Accessor {
	
    public static void display(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);
            mc.thePlayer.addChatMessage(new ChatComponentText(format));
        }
    }

    public static void chat(final Object message) {
        if (mc.thePlayer != null) {
            PacketUtil.send(new C01PacketChatMessage(message.toString()));
        }
    }
}