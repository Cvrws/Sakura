package cc.unknown.handlers;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.KeyInputEvent;
import cc.unknown.event.impl.forge.MouseForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.visual.FreeLook;
import cc.unknown.util.client.CPSMap;

public class KeyHandler {
	
	@Kisoji
	public final Listener<KeyInputEvent> onKey = event -> {
	    int key = Keyboard.getEventKey();

	    if (key != Keyboard.CHAR_NONE && Keyboard.getEventKeyState()) {
	        for (Module module : Sakura.instance.getModuleManager().getModules()) {
	            if (module.getKeyBind() == key) {
	                if (module instanceof FreeLook) {
	                    if (!module.isEnabled()) {
	                        module.toggle();
	                    }
	                } else {
	                    if (!Keyboard.isRepeatEvent()) {
	                        module.toggle();
	                    }
	                }
	                break;
	            }
	        }
	    } else if (key != Keyboard.CHAR_NONE && !Keyboard.getEventKeyState()) {
	        for (Module module : Sakura.instance.getModuleManager().getModules()) {
	            if (module instanceof FreeLook && module.getKeyBind() == key && module.isEnabled()) {
	                module.toggle();
	                break;
	            }
	        }
	    }
	};
	
	@Kisoji
	public final Listener<MouseForgeEvent> onMouse = event -> {
		if (event.getEvent().button != 0) return;
		if (event.getEvent().buttonstate) {
			CPSMap.addClick();
		}
	};
}
