package cc.unknown.module.impl.combat;

import java.util.Arrays;

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
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;

@ModuleInfo(name = "Velocity", category = Category.COMBAT)
public class Velocity extends Module {

    private int ticks = 0;

    private final ModeValue mode = new ModeValue("Mode", this, "Normal", "Jump", "Normal");
    private final SliderValue chance = new SliderValue("Chance", this, 0.9f, 0f, 1f, 0.1f);
    private final SliderValue horizontal = new SliderValue("Horizontal", this, 0.9f, 0f, 1f, 0.1f, () -> mode.is("Normal"));
    private final SliderValue vertical = new SliderValue("Vertical", this, 0.9f, 0f, 1f, 0.1f, () -> mode.is("Normal"));
    private final SliderValue tickDelay = new SliderValue("TickDelay", this, 2, 0, 10, 1, () -> mode.is("Normal"));
    
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("LiquidCheck", true),
			new BoolValue("OnlyTarget", true),
			new BoolValue("OnlyMove", true),
			new BoolValue("DisableOnPressS", true)));

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
        if (isEnabled(NoClip.class)) return;
        
        ticks++;
    };

    @Kisoji
    public final Listener<VelocityEvent> onPostVelocity = event -> {
        if (mode.is("Jump")) {
            mc.thePlayer.setJumping(mc.thePlayer.onGround);
            System.out.println("pepegrillo");
        }
    };

    @Kisoji
    public final Listener<VelocityEvent> onPreUpdate = event -> {
        if (!mode.is("Normal")) return;
        
        if (ticks > tickDelay.getValue()) {
            applyVelocityReduction(event);
            ticks = 0;
        }

        if (!shouldApplyVelocity()) return;

        if (MathUtil.nextRandom(0, 100).intValue() < (chance.getValue() * 100)) {
            applyVelocityReduction(event);
        }
    };

    private boolean shouldApplyVelocity() {
        if (conditionals.isEnabled("OnlyTarget") && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
            return false;
        }

        if (conditionals.isEnabled("OnlyMove") && !MoveUtil.isMoving()) {
            return false;
        }

        if (conditionals.isEnabled("LiquidCheck") && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            return false;
        }

        if (conditionals.isEnabled("DisableOnPressS") && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            return false;
        }

        return true;
    }

    private void applyVelocityReduction(VelocityEvent event) {
        double h = horizontal.getValue() * 100;
        double v = vertical.getValue() * 100;

        event.setX(event.getX() * (h / 100.0));
        event.setY(event.getY() * (v / 100.0));
        event.setZ(event.getZ() * (h / 100.0));
    }
}