package cc.unknown.event.impl.forge;

import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputEvent extends ForgeEvent<InputEvent.KeyInputEvent> {
	public KeyInputEvent(InputEvent.KeyInputEvent event) {
		super(event);
	}
}
