package cc.unknown.event.impl.buz;

public interface Bus<T> {
    void register(final Object obj);
    void unregister(final Object obj);
    void handle(final T event);
}