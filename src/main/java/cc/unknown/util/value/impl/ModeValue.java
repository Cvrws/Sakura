package cc.unknown.util.value.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.util.value.Value;

public class ModeValue extends Value {
    private int index;
    private final List<String> modes;

    public ModeValue(String name, Module module, Supplier<Boolean> visible, String current, String... modes) {
        super(name, module, visible);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(current);
    }

    public ModeValue(String name, Module module, String current, String... modes) {
        super(name, module, () -> true);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(current);
    }

    public boolean is(String mode) {
        return get().equals(mode);
    }

    public String get() {
        if (index < 0 || index >= modes.size()) {
            return modes.get(0);
        }
        return modes.get(index);
    }

    public void set(String mode) {
        int newIndex = modes.indexOf(mode);
        if (newIndex != -1) {
            this.index = newIndex;
        }
    }

    public void set(int mode) {
        if (mode >= 0 && mode < modes.size()) {
            this.index = mode;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (index >= 0 && index < modes.size()) {
            this.index = index;
        }
    }

    public String getMode() {
        if (index < 0 || index >= modes.size()) {
            index = 0;
        }
        return modes.get(index);
    }

    public void setMode(String mode) {
        int newIndex = modes.indexOf(mode);
        if (newIndex != -1) {
            this.index = newIndex;
        }
    }

    public List<String> getModes() {
        return modes;
    }
}