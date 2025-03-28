package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.util.render.FreeLookUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V"))
	private void preventPlayerRotation(EntityPlayerSP player, float x, float y) {
		if (!FreeLookUtil.freelooking) {
			player.setAngles(x, y);
		}
	}

	@ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 1))
	private float modifyCameraYaw(float originalYaw) {
		return FreeLookUtil.freelooking ? FreeLookUtil.cameraYaw : originalYaw;
	}

	@ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 2))
	private float modifyCameraPitch(float originalPitch) {
		return FreeLookUtil.freelooking ? FreeLookUtil.cameraPitch : originalPitch;
	}
}