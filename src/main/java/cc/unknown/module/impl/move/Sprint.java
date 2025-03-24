package cc.unknown.module.impl.move;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.StrafeEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.value.impl.BoolValue;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(name = "Sprint", category = Category.MOVEMENT)
public class Sprint extends Module {

	private final BoolValue omni = new BoolValue("Omni", this, false);

	@Kisoji(value = Priority.LOW)
	public final Listener<StrafeEvent> onStrafe = event -> {
		mc.gameSettings.keyBindSprint.pressed = true;

		if (omni.get()) {
			MoveUtil.preventDiagonalSpeed();

			mc.thePlayer.setSprinting(MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally
					&& !mc.thePlayer.isSneaking() && !mc.thePlayer.isUsingItem());
		}
	};

	@Override
	public void onDisable() {
		mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
		mc.thePlayer.setSprinting(false);
	}
}