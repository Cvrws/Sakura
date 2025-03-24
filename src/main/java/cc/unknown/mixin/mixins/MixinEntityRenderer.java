package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import cc.unknown.module.impl.visual.FreeLook;
import cc.unknown.util.Accessor;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements Accessor{
	@Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void updateCameraSmooth(float partialTicks, long time, CallbackInfo info, boolean flag, float sens, float adjustedSens, float x, float y, int invert, float delta) {
		if (getModule(FreeLook.class).perspectiveEnabled) {
			updateFreelookCamera(x, y, invert);
		}
	}

	@Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void updateCameraNormal(float partialTicks, long time, CallbackInfo info, boolean flag, float sens,
			float adjustedSens, float x, float y, int invert) {
		if (getModule(FreeLook.class).perspectiveEnabled) {
			updateFreelookCamera(x, y, invert);
		}
	}

	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.setAngles(FF)V"))
	private void preventPlayerRotation(EntityPlayerSP player, float x, float y) {
		if (!getModule(FreeLook.class).perspectiveEnabled) {
			player.setAngles(x, y);
		}
	}

	@ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 1))
	private float modifyCameraYaw(float originalYaw) {
		return getModule(FreeLook.class).perspectiveEnabled ? getModule(FreeLook.class).cameraYaw : originalYaw;
	}

	@ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 2))
	private float modifyCameraPitch(float originalPitch) {
		return getModule(FreeLook.class).perspectiveEnabled ? getModule(FreeLook.class).cameraPitch : originalPitch;
	}

	private void updateFreelookCamera(float x, float y, int invert) {
		getModule(FreeLook.class).cameraYaw += x / 8.0F;

		getModule(FreeLook.class).cameraPitch += (y * invert) / 8.0F;

		getModule(FreeLook.class).cameraPitch = Math.max(-90.0f, Math.min(90.0f, getModule(FreeLook.class).cameraPitch));
	}
}