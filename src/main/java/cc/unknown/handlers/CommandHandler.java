package cc.unknown.handlers;

import java.util.Arrays;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.ChatEvent;
import cc.unknown.event.impl.buz.Listener;

public class CommandHandler {
	
	@Kisoji
	public final Listener<ChatEvent> onChat = event -> {
	    String message = event.message;

	    if (message.startsWith(".")) {
	        event.setCancelled(true);

	        String[] args = message.substring(1).split(" ");

	        if (args.length > 0) {
	            for (Command c : Sakura.instance.getCmdManager().getCommands()) {
	                if (args[0].equalsIgnoreCase(c.getPrefix())) {
	                    String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
	                    c.execute(commandArgs);
	                    return;
	                }
	            }
	        }
	    }
	};
}
