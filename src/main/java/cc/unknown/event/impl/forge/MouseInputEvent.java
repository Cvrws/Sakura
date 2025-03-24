package cc.unknown.event.impl.forge;

import net.minecraftforge.fml.common.gameevent.InputEvent;

public class MouseInputEvent extends ForgeEvent<InputEvent.MouseInputEvent> {
	public MouseInputEvent(InputEvent.MouseInputEvent event) {
		super(event);
	}
}
