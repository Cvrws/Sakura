package cc.unknown.event;

public enum Priority {
    LOWEST(-1),
    VERY_LOW(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    VERY_HIGH(4),
    HIGHEST(5);

    private final int value;

	private Priority(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

