package cc.unknown.command.impl;

import java.util.HashMap;

import com.mojang.realmsclient.gui.ChatFormatting;

import cc.unknown.command.Command;
import cc.unknown.handlers.AutoJoinHandler;
import cc.unknown.util.render.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class Join extends Command {
	final HashMap<String, Item> hashMap;

	public Join() {
		super("game");
		this.hashMap = new HashMap<>();
	}

	@Override
	public void execute(String[] args) {
	    if (args.length > 0 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("help"))) {
	        ChatUtil.display(getList());
	        return;
	    }

	    if (args.length < 2) {
	        warning("Uso: .game <modo> <lobby>");
	        return;
	    }

	    this.hashMap.put("sw", Items.bow);
	    this.hashMap.put("tsw", Items.arrow);
	    this.hashMap.put("bw", Items.bed);
	    this.hashMap.put("tnt", Items.gunpowder);
	    this.hashMap.put("pgames", Items.cake);
	    this.hashMap.put("arena", Items.diamond_sword);

	    String gameName = args[0];

	    if (!this.hashMap.containsKey(gameName)) {
	        warning("Modo inválido. Usa: .game list");
	        return;
	    }

	    if (!args[1].matches("\\d+")) {
	        warning("Número de lobby inválido.");
	        return;
	    }

	    int lobbyNumber = Integer.parseInt(args[1]);

	    if (lobbyNumber <= 0) {
	        warning("El lobby debe ser mayor a 0.");
	        return;
	    }

	    AutoJoinHandler.init(hashMap.get(gameName), lobbyNumber);
	}

    private String getList() {
        return "\n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "sw" + ChatFormatting.GRAY + " (Skywars)        \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "tsw" + ChatFormatting.GRAY + " (Team Skywars)  \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "tnt" + ChatFormatting.GRAY + " (Tnt Tag)       \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "bw" + ChatFormatting.GRAY + " (Bedwars)        \n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "pgames" + ChatFormatting.GRAY + " (Party Games)\n" +
                ChatFormatting.GREEN + " - " + ChatFormatting.WHITE + "arena" + ChatFormatting.GRAY + " (Arenapvp)    \n";
    }
}
