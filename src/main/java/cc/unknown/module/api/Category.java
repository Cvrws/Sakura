package cc.unknown.module.api;

public enum Category {
	COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    MISC("MISC"),
    VISUALS("Visuals"),
    WORLD("World");

    private final String name;

	private Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}