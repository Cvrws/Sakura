package cc.unknown.event.impl.buz;

import java.io.IOException;

@FunctionalInterface
public interface Listener<T> {
    void call(T event) throws IOException;
}