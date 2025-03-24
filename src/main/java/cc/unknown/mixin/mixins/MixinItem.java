package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.handlers.RotationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

@Mixin(Item.class)
public class MixinItem {
    @Redirect(method = "getMovingObjectPositionFromPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;rotationYaw:F"))
    private float hookCurrentRotationYaw(EntityPlayer instance) {
		if (instance.getGameProfile() != Minecraft.getMinecraft().thePlayer.getGameProfile() && RotationHandler.rotations != null) {
			return RotationHandler.rotations.x;
		}

        return RotationHandler.rotations.getX();
    }

    @Redirect(method = "getMovingObjectPositionFromPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;rotationPitch:F"))
    private float hookCurrentRotationPitch(EntityPlayer instance) {
		if (instance.getGameProfile() != Minecraft.getMinecraft().thePlayer.getGameProfile() && RotationHandler.rotations != null) {
			return RotationHandler.rotations.y;
		}

        return RotationHandler.rotations.getY();
    }
}
