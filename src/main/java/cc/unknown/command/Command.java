package cc.unknown.command;

import static cc.unknown.util.render.ColorUtil.gold;
import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;
import static cc.unknown.util.render.ColorUtil.reset;
import static cc.unknown.util.render.ColorUtil.yellow;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.ChatUtil;

public abstract class Command implements Accessor {

	private String prefix;
	
	public Command(String prefix) {
		this.prefix = prefix;
	}

	public void execute(String[] message) {}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void error(String error) {
		ChatUtil.display(yellow + "[" + red + "*" + yellow + "] " + reset + error); 
	}
	
	public void warning(String warn) {
		ChatUtil.display(yellow + "[" + gold + "*" + yellow + "] " + reset + warn);
	}
	
	public void success(String success) {
		ChatUtil.display(yellow + "[" + green + "*" + yellow + "] " + reset + success);
	}
}