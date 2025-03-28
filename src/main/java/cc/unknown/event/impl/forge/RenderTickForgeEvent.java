package cc.unknown.event.impl.forge;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderTickForgeEvent extends ForgeEvent<TickEvent.RenderTickEvent> {

    private final TickEvent.RenderTickEvent.Phase phase;

    public RenderTickForgeEvent(TickEvent.RenderTickEvent event) {
        super(event);
        this.phase = event.phase;
    }

    public TickEvent.RenderTickEvent.Phase getPhase() {
        return phase;
    }

    public boolean isPre() {
        return phase == TickEvent.RenderTickEvent.Phase.START;
    }

    public boolean isPost() {
        return phase == TickEvent.RenderTickEvent.Phase.END;
    }
}