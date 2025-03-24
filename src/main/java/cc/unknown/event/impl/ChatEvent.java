package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;

public class ChatEvent extends CancellableEvent {
	public String message;

	public ChatEvent(String message) {
		this.message = message;
	}

}