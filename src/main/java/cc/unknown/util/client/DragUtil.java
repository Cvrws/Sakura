package cc.unknown.util.client;

import com.google.gson.JsonObject;

import cc.unknown.Sakura;
import cc.unknown.ui.drag.Drag;

public class DragUtil extends ConfigUtil {
	public DragUtil(String name) {
		super(name);
	}

	@Override
	public void loadConfig(JsonObject object) {
	    if (Sakura.instance == null || Sakura.instance.getDragManager() == null || Sakura.instance.getDragManager().getDragList() == null) {
	        return;
	    }

	    for (Drag widget : Sakura.instance.getDragManager().getDragList()) {
	        if (widget == null || widget.name == null) continue;
	        
	        if (object.has(widget.name)) {
	            JsonObject obj = object.get(widget.name).isJsonObject() ? object.get(widget.name).getAsJsonObject() : null;
	            if (obj == null) continue;
	            
	            if (obj.has("x") && !obj.get("x").isJsonNull()) {
	                widget.x = obj.get("x").getAsFloat();
	            }
	            if (obj.has("y") && !obj.get("y").isJsonNull()) {
	                widget.y = obj.get("y").getAsFloat();
	            }
	        }
	    }
	}

	@Override
	public JsonObject saveConfig() {
		JsonObject object = new JsonObject();
		for (Drag widget : Sakura.instance.getDragManager().getDragList()) {
			JsonObject widgetObj = new JsonObject();
			widgetObj.addProperty("x", widget.x);
			widgetObj.addProperty("y", widget.y);
			object.add(widget.name, widgetObj);
		}
		return object;
	}
}