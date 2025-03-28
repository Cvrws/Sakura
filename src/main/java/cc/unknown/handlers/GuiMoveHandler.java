package cc.unknown.handlers;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.ClientTickForgeEvent;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.AstolfoGui;
import cc.unknown.util.Accessor;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiMoveHandler implements Accessor {
	private final KeyBinding[] moveKeys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindSneak };

	@Kisoji
	public final Listener<ClientTickForgeEvent> onTick = event -> {
		if (event.isPost()) return;
		if (getModule(ClickGUI.class).moveGui.get() && mc.currentScreen instanceof AstolfoGui) {
			for (KeyBinding bind : moveKeys) {
				bind.pressed = GameSettings.isKeyDown(bind);
			}
		}	
	};
}
