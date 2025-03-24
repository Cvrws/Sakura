package cc.unknown.util.value;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Supplier;

import cc.unknown.module.Module;

public abstract class Value {
    private final String name;
    public Supplier<Boolean> visible;
    public Color color = Color.WHITE;

    public Value(String name, Module module, Supplier<Boolean> visible) {
        this.name = name;
        this.visible = visible;
        Optional.ofNullable(module).ifPresent(m -> m.addValue(this));
    }

    public Boolean canDisplay() {
        return this.visible.get();
    }

	public Supplier<Boolean> getVisible() {
		return visible;
	}


	public void setVisible(Supplier<Boolean> visible) {
		this.visible = visible;
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}


	public String getName() {
		return name;
	}
}