package cc.unknown.util.player;

public enum MoveFix {
    OFF("Off"),
    SILENT("Silent"),
    STRICT("Strict");
	
    final String name;
    
    private MoveFix(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
    public String toString() {
    	return name;
    }
}