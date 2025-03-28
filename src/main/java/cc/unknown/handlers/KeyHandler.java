package cc.unknown.handlers;

import org.lwjgl.input.Keyboard;

import cc.unknown.Sakura;
import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.ClickMouseEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.KeyInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.visual.FreeLook;
import cc.unknown.util.Accessor;

public class KeyHandler implements Accessor {

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
	public final Listener<ClickMouseEvent> onClick = event -> {
        if (mc.gameSettings.keyBindTogglePerspective.isPressed()) {
            mc.gameSettings.thirdPersonView = (mc.gameSettings.thirdPersonView + 1) % 3;
            mc.renderGlobal.setDisplayListEntitiesDirty();
        }
	};
}
