package cc.unknown.handlers;
import org.lwjgl.input.Mouse;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.ClickMouseEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.lists.SList;

public class CPSHandler implements Accessor {
	
	private static SList<Long> leftPresses = new SList<Long>();
	private static SList<Long> rightPresses = new SList<Long>();
	
	@Kisoji
	public final Listener<ClickMouseEvent> onRegisterCPS = event -> {		
		if(Mouse.getEventButtonState()) {
			if(event.getButton() == 0) {
				addLeftClicks();
			}
			
			if(event.getButton() == 1) {
				addRightClicks();
			}
		}
	};
	
	@Kisoji
	public final Listener<TickForgeEvent> onTick = event -> {
		if (event.isPre()) {
			leftPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
			rightPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
		}
	};

	public static int getLeftCps() {
		return leftPresses.size();
	}

	public static int getRightCps() {
		return rightPresses.size();
	}
	
	public static void addLeftClicks() {
		leftPresses.add(System.currentTimeMillis());
	}
	
	public static void addRightClicks() {
		rightPresses.add(System.currentTimeMillis());
	}
}
