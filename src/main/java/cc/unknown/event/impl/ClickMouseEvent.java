package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;

public class ClickMouseEvent extends CancellableEvent {
	private int button;
	
	public ClickMouseEvent(int button) {
		this.button = button;
	}

	public int getButton() {
		return button;
	}
}
