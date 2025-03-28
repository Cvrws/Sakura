package cc.unknown.event.impl.forge;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTickForgeEvent extends ForgeEvent<TickEvent.ClientTickEvent> {

    private final TickEvent.ClientTickEvent.Phase phase;

    public ClientTickForgeEvent(TickEvent.ClientTickEvent event) {
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