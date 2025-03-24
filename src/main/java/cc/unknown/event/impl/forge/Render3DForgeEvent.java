package cc.unknown.event.impl.forge;

import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Render3DForgeEvent extends ForgeEvent<RenderWorldLastEvent> {
	public Render3DForgeEvent(RenderWorldLastEvent event) {
		super(event);
	}
}
