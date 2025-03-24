package cc.unknown.event.impl.forge;

import net.minecraftforge.client.event.EntityViewRenderEvent;

public class CameraSetupEvent extends ForgeEvent<EntityViewRenderEvent.CameraSetup> {
	public CameraSetupEvent(EntityViewRenderEvent.CameraSetup event) {
		super(event);
	}
}
