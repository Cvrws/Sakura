package cc.unknown.util.client;

import java.awt.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.value.Value;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import cc.unknown.util.value.impl.TextValue;

public class ModuleUtil extends ConfigUtil {
    public ModuleUtil(String name) {
        super(name);
    }
    
    @Override
    public void loadConfig(JsonObject object) {
        for (Module module : Sakura.instance.getModuleManager().getModules()) {
            if (object.has(module.getName())) {

                JsonObject moduleObject = object.get(module.getName()).getAsJsonObject();

                if (moduleObject.has("State")) {
                    module.setEnabled(moduleObject.get("State").getAsBoolean());
                }

                if (moduleObject.has("Key")) {
                    module.setKeyBind(moduleObject.get("Key").getAsInt());
                }
                if (moduleObject.has("Hidden")) {
                    module.setHidden(moduleObject.get("Hidden").getAsBoolean());
                }

                if (moduleObject.has("Values")) {
                    JsonObject valuesObject = moduleObject.get("Values").getAsJsonObject();

                    for (Value value : module.getValues()) {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());
                            if (value instanceof SliderValue) {
                            	SliderValue sliderValue = (SliderValue) value;
                                sliderValue.setValue(theValue.getAsNumber().floatValue());
                            }
                            if (value instanceof BoolValue) {
                            	BoolValue boolValue = (BoolValue) value;
                                boolValue.set(theValue.getAsBoolean());
                            }
                            
                            if (value instanceof ModeValue) {
                            	ModeValue modeValue = (ModeValue) value;
                                modeValue.set(theValue.getAsString());
                            }
                            if (value instanceof MultiBoolValue) {
                            	MultiBoolValue multiBoolValue = (MultiBoolValue) value;
                            	
                                if (theValue.getAsString().isEmpty()) {
                                    multiBoolValue.getToggled().forEach(option -> option.set(false));
                                }
                                if (!theValue.getAsString().isEmpty()) {
                                    String[] strings = theValue.getAsString().split(", ");
                                    multiBoolValue.getToggled().forEach(option -> option.set(false));
                                    for (String string : strings) {
                                        multiBoolValue.getValues().stream().filter(setting -> setting.getName().equalsIgnoreCase(string)).forEach(boolValue -> boolValue.set(true));
                                    }
                                }
                            }
                            if (value instanceof ColorValue) {
                            	ColorValue colorValue = (ColorValue) value;
                                JsonObject colorValues = theValue.getAsJsonObject();
                                colorValue.set(ColorUtil.applyOpacity(new Color(colorValues.get("RGB").getAsInt()), colorValues.get("Alpha").getAsFloat()));
                            }
                            if (value instanceof TextValue) {
                            	TextValue textValue = (TextValue) value;
                                textValue.setText(theValue.getAsString());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        for (Module module : Sakura.instance.getModuleManager().getModules()) {
            JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("State", module.isEnabled());
            moduleObject.addProperty("Key", module.getKeyBind());
            moduleObject.addProperty("Hidden", module.isHidden());

            JsonObject valuesObject = new JsonObject();

            for (Value value : module.getValues()) {
                if (value instanceof SliderValue) {
                	SliderValue sliderValue = (SliderValue) value;
                    valuesObject.addProperty(value.getName(), sliderValue.get());
                }
                if (value instanceof BoolValue) {
                	BoolValue boolValue = (BoolValue) value;
                    valuesObject.addProperty(value.getName(), boolValue.get());
                }
                if (value instanceof ModeValue) {
                	ModeValue modeValue = (ModeValue) value;
                    valuesObject.addProperty(value.getName(), modeValue.get());
                }
                if (value instanceof MultiBoolValue) {
                	MultiBoolValue multiBoolValue = (MultiBoolValue) value;
                    valuesObject.addProperty(value.getName(), multiBoolValue.isEnabled());
                }
                if (value instanceof ColorValue) {
                	ColorValue colorValue = (ColorValue) value;
                    JsonObject colorValues = new JsonObject();
                    colorValues.addProperty("RGB", Color.HSBtoRGB(colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()));
                    colorValues.addProperty("Alpha", colorValue.getAlpha());
                    valuesObject.add(colorValue.getName(), colorValues);
                }
                if (value instanceof TextValue) {
                	TextValue textValue = (TextValue) value;
                    valuesObject.addProperty(value.getName(), textValue.getText());
                }
            }

            moduleObject.add("Values", valuesObject);
            object.add(module.getName(), moduleObject);
        }
        return object;
    }
}