package cc.unknown.event.impl.buz;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.GameEvent;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.util.Accessor;

@SuppressWarnings("unchecked")
public final class EventBus<T> implements Bus<T>, Accessor {
	private final Map<Type, List<CallSite<T>>> callSiteMap;
	private final Map<Type, List<Listener<T>>> listenerCache;
	private final ConcurrentLinkedQueue<Function<T, Boolean>> customListeners = new ConcurrentLinkedQueue<>();
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public EventBus() {
		callSiteMap = new HashMap<>();
		listenerCache = new HashMap<>();
	}

	@Override
	public void register(final Object subscriber) {
		try {
			for (final Field field : subscriber.getClass().getDeclaredFields()) {
				final Kisoji annotation = field.getAnnotation(Kisoji.class);
				if (annotation != null) {
					final Type eventType = ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0];

					if (!field.isAccessible())
						field.setAccessible(true);
					try {
						final Listener<T> listener = (Listener<T>) LOOKUP.unreflectGetter(field)
								.invokeWithArguments(subscriber);

						final int priority = annotation.value().getValue();

						final List<CallSite<T>> callSites;
						final CallSite<T> callSite = new CallSite<T>(subscriber, listener, priority);

						if (this.callSiteMap.containsKey(eventType)) {
							callSites = this.callSiteMap.get(eventType);
							callSites.add(callSite);
							callSites.sort((o1, o2) -> o2.priority - o1.priority);
						} else {
							callSites = new ArrayList<>(1);
							callSites.add(callSite);
							this.callSiteMap.put(eventType, callSites);
						}
					} catch (Throwable exception) {
						exception.printStackTrace();
					}
				}
			}

			this.populateListenerCache();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void unregister(final Object subscriber) {
		for (List<CallSite<T>> callSites : this.callSiteMap.values()) {
			callSites.removeIf(eventCallSite -> eventCallSite.owner == subscriber);
		}

		this.populateListenerCache();
	}

	@Override
	public void handle(final T event) {
		try {
			if ((mc.theWorld == null || mc.getNetHandler() == null || (!mc.getNetHandler().doneLoadingTerrain && !(event instanceof PacketEvent && ((PacketEvent) event).isIncoming()))) && !(event instanceof GameEvent)) {
			    return;
			}
			
			final List<Listener<T>> listeners = listenerCache.getOrDefault(event.getClass(),
					Collections.emptyList());

			int i = 0;
			final int listenersSize = listeners.size();

			while (i < listenersSize) {
				listeners.get(i++).call(event);
			}

			if (!this.customListeners.isEmpty()) {
				this.customListeners.removeIf(listener -> listener.apply(event));
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void populateListenerCache() {
		final Map<Type, List<CallSite<T>>> callSiteMap = this.callSiteMap;
		final Map<Type, List<Listener<T>>> listenerCache = this.listenerCache;

		for (final Type type : callSiteMap.keySet()) {
			final List<CallSite<T>> callSites = callSiteMap.get(type);
			final int size = callSites.size();
			final List<Listener<T>> listeners = new ArrayList<>(size);

			for (int i = 0; i < size; i++)
				listeners.add(callSites.get(i).listener);

			listenerCache.put(type, listeners);
		}
	}

	public void registerCustom(final Function<T, Boolean> listener) {
		this.customListeners.add(listener);
	}

	public Map<Type, List<CallSite<T>>> getCallSiteMap() {
		return callSiteMap;
	}

	public Map<Type, List<Listener<T>>> getListenerCache() {
		return listenerCache;
	}

	public ConcurrentLinkedQueue<Function<T, Boolean>> getCustomListeners() {
		return customListeners;
	}

	public static MethodHandles.Lookup getLookup() {
		return LOOKUP;
	}

}