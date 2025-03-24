package cc.unknown.event.impl.forge;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickForgeEvent extends ForgeEvent<TickEvent.ClientTickEvent> {

    private final TickEvent.ClientTickEvent.Phase phase;

    public TickForgeEvent(TickEvent.ClientTickEvent event) {
        super(event);
        this.phase = event.phase;
    }

    public TickEvent.ClientTickEvent.Phase getPhase() {
        return phase;
    }

    public boolean isPre() {
        return phase == TickEvent.ClientTickEvent.Phase.START;
    }

    public boolean isPost() {
        return phase == TickEvent.ClientTickEvent.Phase.END;
    }
}