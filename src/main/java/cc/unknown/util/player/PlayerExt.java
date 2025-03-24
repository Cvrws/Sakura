package cc.unknown.util.player;

import cc.unknown.util.waveycapes.simulation.StickSimulation;
import cc.unknown.util.waveycapes.simulation.StickSimulation.Point;
import cc.unknown.util.waveycapes.simulation.StickSimulation.Stick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class PlayerExt {

	public static StickSimulation stickSimulation = new StickSimulation();

	public static double lastVelocityDeltaX, lastVelocityDeltaY, lastVelocityDeltaZ;
	public static int ticksSinceVelocity, ticksSincePlayerVelocity, ticksSincePlace, ticksSinceAttack;
	public static double lastMotionX, lastMotionY, lastMotionZ;
	public static boolean lastGround;
	public static float movementYaw, velocityYaw, lastMovementYaw;
	public static float renderPitchHead, prevRenderPitchHead;
	public static int onGroundTicks, offGroundTicks;

    public static void updateSimulation(EntityPlayer abstractClientPlayer, int partCount) {
    	
        StickSimulation simulation = stickSimulation;
        boolean dirty = false;
        
        if(simulation.points.size() != partCount) {
        	
            simulation.points.clear();
            simulation.sticks.clear();
            
            for (int i = 0; i < partCount; i++) {
                Point point = new Point();
                point.position.y = -i;
                point.locked = i == 0;
                simulation.points.add(point);
                if(i > 0) {
                    simulation.sticks.add(new Stick(simulation.points.get(i-1), point, 1f));
                }
            }
            dirty = true;
        }
        
        if(dirty) {
            for(int i = 0; i < 10; i++) {
                simulate(abstractClientPlayer);
            }
        }
    }
    
    public static void simulate(EntityPlayer abstractClientPlayer) {
    	
        StickSimulation simulation = stickSimulation;
        
        if(simulation.points.isEmpty()) {
            return;
        }
        
        simulation.points.get(0).prevPosition.copy(simulation.points.get(0).position);
        double d = abstractClientPlayer.chasingPosX
                - abstractClientPlayer.posX;
        double m = abstractClientPlayer.chasingPosZ
                - abstractClientPlayer.posZ;
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float heightMul = 6f;
        double fallHack = MathHelper.clamp_double((simulation.points.get(0).position.y - (abstractClientPlayer.posY*heightMul)), 0d, 1d); 
        
        simulation.points.get(0).position.x += (d * o + m * p) + fallHack;
        simulation.points.get(0).position.y = (float) (abstractClientPlayer.posY*heightMul + (abstractClientPlayer.isSneaking() ? -4 : 0));
        simulation.simulate();
    }
}
