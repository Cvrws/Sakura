package cc.unknown.event.impl.buz;

public class CallSite<T> {
	public final Object owner;
    public final Listener<T> listener;
    public final int priority;
    
	public CallSite(Object owner, Listener<T> listener, int priority) {
		this.owner = owner;
		this.listener = listener;
		this.priority = priority;
	}

	public Object getOwner() {
		return owner;
	}

	public Listener<T> getListener() {
		return listener;
	}

	public int getPriority() {
		return priority;
	}
}
