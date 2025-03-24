package cc.unknown.handlers;

import org.lwjgl.opengl.Display;

import cc.unknown.Sakura;
import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.ChatGUIEvent;
import cc.unknown.event.impl.Render2DEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.ui.drag.Drag;

public class DragHandler {
	
    @Kisoji
    public final Listener<Render2DEvent> onRender2D = event -> { 
    	if(Display.isVisible()) {
    		for (Drag widget : Sakura.instance.getDragManager().getDragList()) {
                if (widget.shouldRender()) {
                    widget.updatePos(event.resolution);
                    widget.render(event.resolution);
                }
            }
        }
    };

    @Kisoji
    public final Listener<ChatGUIEvent> onChatGui = event -> {
        if (Display.isVisible()) {
            Drag draggingWidget = null;
            for (Drag widget : Sakura.instance.getDragManager().getDragList()) {
                if (widget.shouldRender() && widget.dragging) {
                    draggingWidget = widget;
                    break;
                }
            }

            for (Drag widget : Sakura.instance.getDragManager().getDragList()) {
                if (widget.shouldRender()) {
                    widget.onChatGUI(event.mouseX, event.mouseY, (draggingWidget == null || draggingWidget == widget), event.scaledResolution);
                    if (widget.dragging) draggingWidget = widget;
                }
            }
        }
    };
}
