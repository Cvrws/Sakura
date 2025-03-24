package cc.unknown.managers;

import cc.unknown.command.Command;
import cc.unknown.command.impl.*;
import cc.unknown.util.structure.lists.SList;

public final class CommandManager extends SList<Command> {

	private static final long serialVersionUID = 1L;

	public void init() {
        registerCommands(
            new Bind(),
            new Config(),
            new Friend(),
            new Help(),
            new Name(),
            new Join(),
            new Toggle(),
            new Transaction(),
            new Screenshot()
        );
    }

    private void registerCommands(Command... commands) {
    	this.addAll(commands);
    }

    public void add(final Command command) {
    	this.add(command);
    }

    public SList<Command> getCommands() {
        return this;
    }
}