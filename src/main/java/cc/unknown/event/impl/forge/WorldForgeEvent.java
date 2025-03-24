package cc.unknown.event.impl.forge;

import net.minecraftforge.event.world.WorldEvent;

public class WorldForgeEvent extends ForgeEvent<WorldEvent.Load> {
	public WorldForgeEvent(WorldEvent.Load event) {
		super(event);
	}
}
