package cc.unknown.module.impl.combat;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.AttackForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.MoveUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

@ModuleInfo(name = "WTap", category = Category.COMBAT)
public class WTap extends Module {

    private EntityLivingBase target = null;
    
    @Kisoji
    public final Listener<AttackForgeEvent> onAttack = event -> {
        if (event.getEvent().entity instanceof EntityLivingBase) {
            target = (EntityLivingBase) event.getEvent().entity;
        }
    };

    @Kisoji
    public final Listener<MotionEvent.Pre> onPreMotion = event -> {
		if (mc.currentScreen != null || !mc.inGameHasFocus) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mc.objectMouseOver.entityHit;
        }

        if (target == null || target.isDead) {
            target = null;
            return;
        }
        
        if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
            int random = (int) MathUtil.getSafeRandom(0, 10);
            
            switch (mc.thePlayer.hurtTime) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 10:
                    mc.thePlayer.sprintingTicksLeft = random;
                    break;
                case 9:
                    mc.thePlayer.sprintingTicksLeft = 0;
                    break;
            }
        }
    };
}
