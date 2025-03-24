package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Sakura;
import cc.unknown.event.impl.Render2DEvent;
import cc.unknown.handlers.SpoofHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {

	@Shadow
	@Final
	protected Minecraft mc;

	@Shadow
	public abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_);

	@Overwrite
	protected void renderTooltip(ScaledResolution sr, float partialTicks) {
		if (mc.getRenderViewEntity() instanceof EntityPlayer) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/misc/vignette.png"));
			EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
			int i = sr.getScaledWidth() / 2;
			float f = zLevel;
			zLevel = -90.0F;
			drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
			drawTexturedModalRect(i - 91 - 1 + SpoofHandler.getSpoofedSlot() * 20, sr.getScaledHeight() - 22 - 1, 0, 22,
					24, 22);
			zLevel = f;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			RenderHelper.enableGUIStandardItemLighting();

			for (int j = 0; j < 9; ++j) {
				int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
				int l = sr.getScaledHeight() - 16 - 3;
				renderHotbarItem(j, k, l, partialTicks, entityplayer);
			}

			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();

		Sakura.instance.getEventBus().handle(new Render2DEvent(sr, partialTicks));
	}
	
	@Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
	private void StreamIndicator(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	private void bossHealth(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
	private void pumpkinOverlay(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderDemo", at = @At("HEAD"), cancellable = true)
	private void renderDemo(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
	private void renderPortal(CallbackInfo ci) {
		ci.cancel();
	}
}
