package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.FreeLookUtil;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "FreeLook", category = Category.VISUALS, key = Keyboard.KEY_LMENU)
public class FreeLook extends Module {

    private boolean freeLookingactivated;

    @Override
    public void onDisable() {
        freeLookingactivated = false;
        FreeLookUtil.freelooking = false;
        mc.gameSettings.thirdPersonView = 0;
    }

    @Kisoji(value = Priority.LOW)
    public final Listener<TickEvent> onTick = event -> {
        if (this.getKeyBind() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.getKeyBind())) {
            this.setEnabled(false);
            return;
        }

        if (mc.thePlayer.ticksExisted < 10) {
            stop();
        }
        
        if (Keyboard.isKeyDown(getKeyBind())) {
            if (!freeLookingactivated) {
                freeLookingactivated = true;
                FreeLookUtil.enable();
                FreeLookUtil.cameraYaw += 180;
                mc.gameSettings.thirdPersonView = 1;
            }
        } else if (freeLookingactivated) {
            stop();
        }
    };

    private void stop() {
        toggle();
        FreeLookUtil.freelooking = false;
        freeLookingactivated = false;
        mc.gameSettings.thirdPersonView = 0;
    }
}