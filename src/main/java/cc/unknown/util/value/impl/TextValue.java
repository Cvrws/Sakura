package cc.unknown.util.value.impl;

import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.util.value.Value;

public class TextValue extends Value {
    private String text;
    private boolean onlyNumber;

    public TextValue(String name, Module module, String text, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.text = text;
        this.onlyNumber = false;
    }

    public TextValue(String name, Module module, String text) {
        super(name, module, () -> true);
        this.text = text;
    }

    public TextValue(String name, Module module, String text, boolean onlyNumber, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public TextValue(String name, Module module, String text, boolean onlyNumber) {
        super(name, module, () -> true);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public String get() {
        return text;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isOnlyNumber() {
		return onlyNumber;
	}

	public void setOnlyNumber(boolean onlyNumber) {
		this.onlyNumber = onlyNumber;
	}
}