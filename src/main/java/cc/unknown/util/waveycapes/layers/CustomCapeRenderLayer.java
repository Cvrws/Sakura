package cc.unknown.util.waveycapes.layers;

import cc.unknown.Sakura;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.player.PlayerExt;
import cc.unknown.util.waveycapes.CapeMovement;
import cc.unknown.util.waveycapes.CapeStyle;
import cc.unknown.util.waveycapes.WindMode;
import cc.unknown.util.waveycapes.config.Config;
import cc.unknown.util.waveycapes.simulation.StickSimulation;
import cc.unknown.util.waveycapes.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class CustomCapeRenderLayer implements LayerRenderer<AbstractClientPlayer> {
    static final int partCount = 16;
    private ModelRenderer[] customCape = new ModelRenderer[partCount];
    private final RenderPlayer playerRenderer;
    private final SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();
    
    public CustomCapeRenderLayer(RenderPlayer playerRenderer, ModelBase model) {
        this.playerRenderer = playerRenderer;
        buildMesh(model);
    }
    
    private void buildMesh(ModelBase model) {
        customCape = new ModelRenderer[partCount];
        for (int i = 0; i < partCount; i++) {
            ModelRenderer base = new ModelRenderer(model, 0, i);
            base.setTextureSize(64, 32);
            this.customCape[i] = base.addBox(-5.0F, (float)i, -1.0F, 10, 1, 1);
        }
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float paramFloat1, float paramFloat2, float deltaTick, float animationTick, float paramFloat5, float paramFloat6, float paramFloat7) {
    	try {
	    	if (!Sakura.instance.getModuleManager().getModule(Interface.class).wavey.get()) return;
	
	        if (!abstractClientPlayer.isInvisible() && (abstractClientPlayer.hasPlayerInfo() && abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE) && abstractClientPlayer.getLocationCape() != null || abstractClientPlayer.getName().equals(Minecraft.getMinecraft().getSession().getUsername()) && Sakura.instance.getModuleManager().getModule(Interface.class).isEnabled())) {
	            if (Config.capeMovement == CapeMovement.BASIC_SIMULATION) {
	                PlayerExt.updateSimulation(abstractClientPlayer, partCount);
	            }
	
	            if (abstractClientPlayer == Minecraft.getMinecraft().thePlayer) {
	            	if (abstractClientPlayer.getLocationCape() == null) {
	            		this.playerRenderer.bindTexture(null);
	            	}
	            	
	            	this.playerRenderer.bindTexture(abstractClientPlayer.getLocationCape());
	            }
	
	            if (Config.capeStyle == CapeStyle.SMOOTH) {
	                if (abstractClientPlayer == Minecraft.getMinecraft().thePlayer) {
	                	smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);
	                } else {
	                    smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);
	                }
	            } else {
	                ModelRenderer[] parts = customCape;
	                for (int part = 0; part < partCount; part++) {
	                    ModelRenderer model = parts[part];
	                    GlStateManager.pushMatrix();
	                    modifyPoseStack(abstractClientPlayer, deltaTick, part);
	                    model.render(0.0625F);
	                    GlStateManager.popMatrix();
	                }
	            }
	        }
    	} catch (NullPointerException ignored) {
    		
    	}
    }
    
    private void modifyPoseStack(AbstractClientPlayer abstractClientPlayer, float h, int part) {
        if(Config.capeMovement == CapeMovement.BASIC_SIMULATION) {
            modifyPoseStackSimulation(abstractClientPlayer, h, part);
            return;
        }
        modifyPoseStackVanilla(abstractClientPlayer, h, part);
    }
    
    private void modifyPoseStackSimulation(AbstractClientPlayer abstractClientPlayer, float delta, int part) {
        StickSimulation simulation = PlayerExt.stickSimulation;
        GlStateManager.translate(0.0D, 0.0D, 0.125D);
        
        float z = simulation.points.get(part).getLerpX(delta) - simulation.points.get(0).getLerpX(delta);
        if(z > 0) {
            z = 0;
        }
        float y = simulation.points.get(0).getLerpY(delta) - part - simulation.points.get(part).getLerpY(delta);
        
        float sidewaysRotationOffset = 0;
        float partRotation = (float) -Math.atan2(y, z);
        partRotation = Math.max(partRotation, 0);
        if(partRotation != 0)
            partRotation = (float) (Math.PI-partRotation);
        partRotation *= 57.2958;
        partRotation *= 2;
        
        float height = 0;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            GlStateManager.translate(0, 0.15F, 0);
        }

        float naturalWindSwing = getNatrualWindSwing(part);

        
        GlStateManager.rotate(6.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0, y/partCount, z/partCount);
        GlStateManager.translate(0, + (0.48/16) , - (0.48/16));
        GlStateManager.translate(0, part * 1f/partCount, (float) 0 /partCount);
        GlStateManager.translate(0, -part * 1f/partCount, (float) 0 /partCount);
        GlStateManager.translate(0, -(0.48/16), (0.48/16));
        
    }
    
    void modifyPoseStackVanilla(AbstractClientPlayer abstractClientPlayer, float h, int part) {
        GlStateManager.translate(0.0D, 0.0D, 0.125D);
        double d = Mth.lerp(h, abstractClientPlayer.prevChasingPosX, abstractClientPlayer.chasingPosX)
                - Mth.lerp(h, abstractClientPlayer.prevPosX, abstractClientPlayer.posX);
        double e = Mth.lerp(h, abstractClientPlayer.prevChasingPosY, abstractClientPlayer.chasingPosY)
                - Mth.lerp(h, abstractClientPlayer.prevPosY, abstractClientPlayer.posY);
        double m = Mth.lerp(h, abstractClientPlayer.prevChasingPosZ, abstractClientPlayer.chasingPosZ)
                - Mth.lerp(h, abstractClientPlayer.prevPosZ, abstractClientPlayer.posZ);
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float height = (float) e * 10.0F;
        height = MathHelper.clamp_float(height, -6.0F, 32.0F);
        float swing = (float) (d * o + m * p) * easeOutSine(1.0F/partCount*part)*100;
        swing = MathHelper.clamp_float(swing, 0.0F, 150.0F * easeOutSine(1F/partCount*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = MathHelper.clamp_float(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(h, abstractClientPlayer.prevCameraYaw, abstractClientPlayer.cameraYaw);
        height += Math.sin(Mth.lerp(h, abstractClientPlayer.prevDistanceWalkedModified, abstractClientPlayer.distanceWalkedModified) * 6.0F) * 32.0F * t;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            GlStateManager.translate(0, 0.15F, 0);
        }

        float naturalWindSwing = getNatrualWindSwing(part);
        
        GlStateManager.rotate(6.0F + swing / 2.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
    }
    
    float getNatrualWindSwing(int part) {
        if (Config.windMode == WindMode.WAVES) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / partCount;
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
    }

    
    private static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2f);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
   
}
