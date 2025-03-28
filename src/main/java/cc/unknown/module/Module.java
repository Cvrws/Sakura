package cc.unknown.module;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import cc.unknown.Sakura;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.lists.SList;
import cc.unknown.util.value.Value;
import cc.unknown.util.value.impl.SliderValue;

public abstract class Module implements Accessor {

    private final ModuleInfo moduleInfo;
    private final String name;
    private final Category category;
    private int keyBind;
    private final SList<Value> values = new SList<>();
    private boolean hidden;
    private boolean state;
    private boolean expanded;

    protected Module() {
        this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        Objects.requireNonNull(moduleInfo, "ModuleInfo annotation is missing on " + getClass().getName());
        this.name = moduleInfo.name();
        this.category = moduleInfo.category();
        this.keyBind = moduleInfo.key();
    }

    public void onEnable() { }
    public void onDisable() { }
    
    public boolean isEnabled() {
        return state;
    }

    public boolean isDisabled() {
        return !state;
    }

    public <M extends Module> boolean isEnabled(Class<M> module) {
        Module mod = Sakura.instance.getModuleManager().getModule(module);
        return mod != null && mod.isEnabled();
    }

    public <M extends Module> boolean isDisabled(Class<M> module) {
        Module mod = Sakura.instance.getModuleManager().getModule(module);
        return mod == null || mod.isDisabled();
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void setEnabled(boolean enabled) {
        if (this.state != enabled) {
            this.state = enabled;
            if (enabled) {
                enable();
            } else {
                disable();
            }
        }
    }

    private void enable() {
    	Sakura.instance.getEventBus().register(this);
    	try {
            onEnable();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void disable() {
        Sakura.instance.getEventBus().unregister(this);
        try {
            onDisable();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (mc.thePlayer != null) {
            e.printStackTrace();
        }
    }

    public <M extends Module> M getModule(Class<M> clazz) {
        return Sakura.instance.getModuleManager().getModule(clazz);
    }

    public void addValues(Value... settings) {
        values.addAll(Arrays.asList(settings));
    }

    public void addValue(Value value) {
        addValues(value);
    }

    public Value getValue(String valueName) {
        return values.stream()
                .filter(value -> value.getName().equalsIgnoreCase(valueName))
                .findFirst()
                .orElse(null);
    }

	public int getKeyBind() {
		return keyBind;
	}

	public void setKeyBind(int keyBind) {
		this.keyBind = keyBind;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public String getName() {
		return name;
	}

	public Category getCategory() {
		return category;
	}

	public SList<Value> getValues() {
		return values;
	}
	
	public void correctSliders(SliderValue c, SliderValue d) {
		if (c.getValue() > d.getValue()) {
			float p = c.getValue();
			c.setValue(d.getValue());
			d.setValue(p);
		}
	}
	
	public double ranModuleVal(SliderValue a, SliderValue b, Random r) {
		return a.getValue() == b.getValue() ? a.getValue() : a.getValue() + r.nextDouble() * (b.getValue() - a.getValue());
	}

}