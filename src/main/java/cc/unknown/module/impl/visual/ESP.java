package cc.unknown.module.impl.visual;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertex3i;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.UpdatePlayerAnglesEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.Render3DForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.EnemyUtil;
import cc.unknown.util.client.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "ESP", category = Category.VISUALS)
public class ESP extends Module {    

	private final BoolValue colorTeams = new BoolValue("ColorTeams", this, false);
	private final BoolValue checkInvis = new BoolValue("ShowInvisibles", this, false);
	private final BoolValue redDamage = new BoolValue("RedOnDamage", this, false);
	
    public final SliderValue skeletalWidth = new SliderValue("Width", this, 0.5f, 0.1f, 5f, 0.1f);
	
    private final Map<EntityPlayer, float[][]> rotationMap = new HashMap<>();
    private static final float DEGREES_IN_RADIAN = 57.295776f;

    @Kisoji
    public final Listener<UpdatePlayerAnglesEvent> onUpdatePlayerAngles = event -> updatePlayerAngles(event.getEntityPlayer(), event.getModelBiped());

    @Kisoji
    public final Listener<Render3DForgeEvent> onRender2D = event -> {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer || PlayerUtil.unusedNames(player)) continue;
            if (player.deathTime > 0 || (player.isInvisible() && !checkInvis.get())) continue;

            float partialTicks = mc.timer.renderPartialTicks;
            int color = getPlayerColor(player);

            glPushMatrix();
            setupRenderState(new Color(color));
            drawSkeleton(player, partialTicks);
            restoreRenderState();
            glPopMatrix();
        }
    };

    private int getPlayerColor(EntityPlayer player) {
        String name = player.getName();
        if (redDamage.get() && player.hurtTime > 0) return new Color(255, 0, 0).getRGB();
        if (colorTeams.get()) return ColorUtil.getColorFromTags(player.getDisplayName().getFormattedText());
        if (FriendUtil.isFriend(name)) return new Color(0, 255, 0).getRGB();
        if (EnemyUtil.isEnemy(name)) return new Color(255, 0, 0).getRGB();
        return getModule(Interface.class).color(0);
    }

    private void setupRenderState(Color color) {
        GL11.glLineWidth(skeletalWidth.getValue());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        ColorUtil.glColor(color.getRGB());
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
    }

    private void restoreRenderState() {
    	GL11.glDepthMask(true);
    	GL11.glDisable(GL11.GL_BLEND);
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glDisable(GL11.GL_LINE_SMOOTH);
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    public void updatePlayerAngles(EntityPlayer entityPlayer, ModelBiped modelBiped) {
        rotationMap.put(entityPlayer, new float[][]{
                {modelBiped.bipedHead.rotateAngleX, modelBiped.bipedHead.rotateAngleY, modelBiped.bipedHead.rotateAngleZ},
                {modelBiped.bipedRightArm.rotateAngleX, modelBiped.bipedRightArm.rotateAngleY, modelBiped.bipedRightArm.rotateAngleZ},
                {modelBiped.bipedLeftArm.rotateAngleX, modelBiped.bipedLeftArm.rotateAngleY, modelBiped.bipedLeftArm.rotateAngleZ},
                {modelBiped.bipedRightLeg.rotateAngleX, modelBiped.bipedRightLeg.rotateAngleY, modelBiped.bipedRightLeg.rotateAngleZ},
                {modelBiped.bipedLeftLeg.rotateAngleX, modelBiped.bipedLeftLeg.rotateAngleY, modelBiped.bipedLeftLeg.rotateAngleZ}
        });
    }

    private void drawSkeleton(EntityPlayer player, float partialTicks) {
        float[][] entPos = rotationMap.get(player);
        if (entPos != null) {
            glPushMatrix();

            float x = (float) (interpolate(player.posX, player.prevPosX, partialTicks) - mc.getRenderManager().renderPosX);
            float y = (float) (interpolate(player.posY, player.prevPosY, partialTicks) - mc.getRenderManager().renderPosY);
            float z = (float) (interpolate(player.posZ, player.prevPosZ, partialTicks) - mc.getRenderManager().renderPosZ);
            glTranslated(x, y, z);

            boolean sneaking = player.isSneaking();

            float rotationYawHead = player.rotationYawHead;
            float renderYawOffset = player.renderYawOffset;
            float prevRenderYawOffset = player.prevRenderYawOffset;

            float xOff = interpolate(renderYawOffset, prevRenderYawOffset, partialTicks);
            float yOff = sneaking ? 0.6F : 0.75F;

            glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
            glTranslatef(0.0F, 0.0F, sneaking ? -0.235F : 0.0F);

            // draw limbs with rotation
            drawLimbs(entPos, yOff, sneaking, xOff, rotationYawHead);

            glPopMatrix();
        }
    }

    private void drawLimbs(float[][] entPos, float yOff, boolean sneaking, float xOff, float rotationYawHead) {
        // draw arms
        for (int i = 1; i <= 2; i++) {
            drawArm(entPos[i + 2], i == 1 ? -0.125F : 0.125F, yOff);
        }

        glTranslatef(0.0F, 0.0F, sneaking ? 0.25F : 0.0F);
        glPushMatrix();
        glTranslatef(0.0F, sneaking ? -0.05F : 0.0F, sneaking ? -0.01725F : 0.0F);

        // draw right and left arm
        for (int i = 1; i <= 2; i++) {
            drawLimb(entPos[i], i == 1 ? -0.375F : 0.375F, yOff + 0.55F);
        }

        // handle head position
        glRotatef(xOff - rotationYawHead, 0.0F, 1.0F, 0.0F);
        drawHead(entPos[0], yOff);

        glPopMatrix();

        // draw spine and other body parts
        drawSpine(yOff);
    }

    private void drawArm(float[] rotations, float xOffset, float yOff) {
        glPushMatrix();
        glTranslatef(xOffset, yOff, 0.0F);
        applyRotations(rotations);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, -yOff, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void drawLimb(float[] rotations, float xOffset, float yOff) {
        glPushMatrix();
        glTranslatef(xOffset, yOff, 0.0F);
        applyRotations(rotations);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, -0.5F, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void drawHead(float[] rotations, float yOff) {
        glPushMatrix();
        glTranslatef(0.0F, yOff + 0.55F, 0.0F);
        applyRotations(rotations);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, 0.3F, 0.0F);
        glEnd();
        glPopMatrix();
    }

    private void applyRotations(float[] rotations) {
        if (rotations[0] != 0.0F) {
            glRotatef(rotations[0] * DEGREES_IN_RADIAN, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[1] != 0.0F) {
            glRotatef(rotations[1] * DEGREES_IN_RADIAN, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[2] != 0.0F) {
            glRotatef(rotations[2] * DEGREES_IN_RADIAN, 0.0F, 0.0F, 1.0F);
        }
    }

    private void drawSpine(float yOff) {
        glPushMatrix();
        glTranslated(0.0F, yOff, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3f(-0.125F, 0.0F, 0.0F);
        glVertex3f(0.125F, 0.0F, 0.0F);
        glEnd();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0.0F, yOff, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3i(0, 0, 0);
        glVertex3f(0.0F, 0.55F, 0.0F);
        glEnd();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0.0F, yOff + 0.55F, 0.0F);
        glBegin(GL_LINE_STRIP);
        glVertex3f(-0.375F, 0.0F, 0.0F);
        glVertex3f(0.375F, 0.0F, 0.0F);
        glEnd();
        glPopMatrix();
    }
    
    private double interpolate(final double current, final double previous, final double multiplier) {
        return previous + (current - previous) * multiplier;
    }
    
    private float interpolate(final float current, final float previous, final float multiplier) {
        return previous + (current - previous) * multiplier;
    }
}