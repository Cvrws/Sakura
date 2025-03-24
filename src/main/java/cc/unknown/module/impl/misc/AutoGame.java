package cc.unknown.module.impl.misc;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ServerUtil;
import cc.unknown.util.client.StringUtil;
import cc.unknown.util.render.ChatUtil;
import cc.unknown.util.render.ColorUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(name = "AutoGame", category = Category.MISC)
public class AutoGame extends Module {
    
    @Kisoji
    public final Listener<PacketEvent> onPacket = event -> {
        Packet<?> packet = event.getPacket();
        if (event.isOutgoing()) return;
        
        String game = ServerUtil.getDetectedGame(mc.theWorld.getScoreboard());
        String command = "";
        
        if (packet instanceof S02PacketChat) {
            S02PacketChat wrapper = ((S02PacketChat) packet);
            String message = wrapper.getChatComponent().getFormattedText();

            if (StringUtil.containsAny(message, "Jugar de nuevo", "ha ganado")) {
            	switch (game) {
            	case "BedWars":
            		command = "/bw random";
            		break;
            	case "SkyWars":
            		command = "/sw random";
            		break;
            	case "SkyWars Speed":
            		command = "/sw random";
            		break;
            	case "TNTTag":
            		command = "/playagain";
            		break;
            	case "ArenaPvP":
            		command = "/leave";
            		break;
            	}
	                
            	if (!command.isEmpty()) {
            		ChatUtil.chat(command);
            		ChatUtil.display(ColorUtil.red +  "[S] " + ColorUtil.pink + "Joined a new game");
            	}
            }
        }
    };
}
