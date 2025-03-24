package cc.unknown.event;

public enum EventBusPriority {
    LOWEST(Priority.LOWEST),
    VERY_LOW(Priority.VERY_LOW),
    LOW(Priority.LOW),
    MEDIUM(Priority.NORMAL),
    HIGH(Priority.HIGH),
    VERY_HIGH(Priority.VERY_HIGH),
    HIGHEST(Priority.HIGHEST);

    private final int value;

    EventBusPriority(Priority priority) {
        this.value = priority.getValue();
    }

	public int getValue() {
		return value;
	}
}