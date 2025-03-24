package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.CameraSetupEvent;
import cc.unknown.event.impl.forge.KeyInputEvent;
import cc.unknown.event.impl.forge.MouseInputEvent;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(name = "FreeLook", category = Category.VISUALS, key = Keyboard.KEY_LMENU)
public class FreeLook extends Module {

	public boolean perspectiveEnabled = false;
    public float cameraPitch;
    public float cameraYaw;

    @Kisoji(value = Priority.LOW)
    public final Listener<TickForgeEvent> onTick = event -> {
    	if (event.isPost()) return;
        if (mc.thePlayer == null) return;

        if (!Keyboard.isKeyDown(Keyboard.KEY_LMENU) && perspectiveEnabled) {
            resetFreelook();
        }
    };

    @Kisoji(value = Priority.LOW)
    public final Listener<KeyInputEvent> onKeyInput = event -> handleFreelookKeyInput();
    
    @Kisoji(value = Priority.LOW)
    public final Listener<MouseInputEvent> onMouseInput = event -> handleFreelookKeyInput();
    
    @Kisoji(value = Priority.LOW)
    public final Listener<CameraSetupEvent> onCamera = event -> {
        if (perspectiveEnabled) {
            event.getEvent().yaw = cameraYaw;
            event.getEvent().pitch = cameraPitch;
        }
    };
    
    private void handleFreelookKeyInput() {
        if (mc.thePlayer == null) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) && !perspectiveEnabled) {
            enableFreelook();
        }
    }

    private void enableFreelook() {
        perspectiveEnabled = true;
        cameraPitch = mc.thePlayer.rotationPitch;
        cameraYaw = mc.thePlayer.rotationYaw + 180.0F;
        mc.gameSettings.thirdPersonView = 1;
    }

    private void resetFreelook() {
        perspectiveEnabled = false;
        mc.gameSettings.thirdPersonView = 0;
    }
}