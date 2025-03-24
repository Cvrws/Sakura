package cc.unknown.module.impl.combat;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.VelocityEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "Velocity", category = Category.COMBAT)
public class Velocity extends Module {

    private int ticks = 0;

    private final ModeValue mode = new ModeValue("Mode", this, "Normal", "Jump", "Normal");
    private final SliderValue chance = new SliderValue("Chance", this, 100, 0, 100);
    private final SliderValue horizontal = new SliderValue("Horizontal", this, 100, 0, 100, () -> mode.is("Normal"));
    private final SliderValue vertical = new SliderValue("Vertical", this, 100, 0, 100, () -> mode.is("Normal"));
    private final SliderValue tickDelay = new SliderValue("TickDelay", this, 2, 0, 10, () -> mode.is("Normal"));

    private final BoolValue liquidChecks = new BoolValue("LiquidChecks", this, true);
    private final BoolValue onlyTarget = new BoolValue("OnlyTarget", this, true);
    private final BoolValue onlyMove = new BoolValue("OnlyMove", this, true);
    private final BoolValue disableS = new BoolValue("DisableOnPressS", this, true);
    
    @Override
    public void onEnable() {
    	ticks = 0;
    }
    
    @Override
    public void onDisable() {
    	ticks = 0;
    }

    @Kisoji
    public final Listener<TickForgeEvent> onPreTick = event -> {
    	if (event.isPost()) return;
        if (mc.currentScreen != null || !mc.inGameHasFocus) return;
        if (isEnabled(NoClip.class)) return;
        if (mode.is("Normal")) ticks++;
    };

    @Kisoji
    public final Listener<VelocityEvent.Post> onPostVelocity = event -> {
        if (mode.is("Jump")) {
            if (!shouldApplyVelocity()) return;

            if (MathUtil.nextRandom(0, 100).intValue() < chance.getValue()) {
            	PlayerUtil.jump(true);
            }
        }
    };

    @Kisoji
    public final Listener<VelocityEvent> onPreUpdate = event -> {
        if (mode.is("Normal")) {
	    	if (ticks > tickDelay.getValue()) {
	            applyVelocityReduction(event);
	            ticks = 0;
	        }
	
	        if (!shouldApplyVelocity()) return;
	
	        if (MathUtil.nextRandom(0, 100).intValue() < chance.getValue() + 1) {
	            applyVelocityReduction(event);
	            ticks = 0;
	        }
        }
    };

    private boolean shouldApplyVelocity() {
        if (mc.thePlayer.maxHurtTime <= 0 || mc.thePlayer.hurtTime != mc.thePlayer.maxHurtTime) return false;

        if (onlyTarget.get() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
            return false;
        }

        if (onlyMove.get() && !MoveUtil.isMoving()) {
            return false;
        }

        if (liquidChecks.get() && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            return false;
        }

        if (disableS.get() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            return false;
        }

        return true;
    }

    private void applyVelocityReduction(VelocityEvent event) {
        event.setX(event.getX() * horizontal.getValue() / 100.0);
        event.setY(event.getY() * vertical.getValue() / 100.0);
        event.setZ(event.getZ() * horizontal.getValue() / 100.0);
    }
}
