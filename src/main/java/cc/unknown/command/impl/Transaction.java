package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.handlers.TransactionHandler;

public final class Transaction extends Command {
    
    public Transaction() {
        super("transaction");
    }
    
    @Override
    public void execute(final String[] args) {
    	TransactionHandler.start();
    }
}