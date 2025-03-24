package cc.unknown.event.impl.forge;

import cc.unknown.event.Event;

public class ForgeEvent<T extends net.minecraftforge.fml.common.eventhandler.Event> implements Event {

    private final T event;

    public ForgeEvent(T event) {
        this.event = event;
    }
    
    public T getEvent() {
        return event;
    }
}