package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Sakura;
import cc.unknown.event.impl.UpdatePlayerAnglesEvent;
import cc.unknown.module.impl.visual.Interface;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mixin(ModelPlayer.class)
public abstract class MixinModelPlayer extends MixinModelBiped {

	@Inject(method = "setRotationAngles", at = @At("RETURN"))
	private void revertSwordAnimation(float p_setRotationAngles_1_, float p_setRotationAngles_2_, float p_setRotationAngles_3_, float p_setRotationAngles_4_, float p_setRotationAngles_5_, float p_setRotationAngles_6_, Entity p_setRotationAngles_7_, CallbackInfo callbackInfo) {
		if (p_setRotationAngles_7_ instanceof EntityPlayer) {
			Sakura.instance.getEventBus().handle(new UpdatePlayerAnglesEvent((EntityPlayer) p_setRotationAngles_7_, (ModelBiped) (Object) this));
		}
	}
	
    @Inject(method = "renderCape", at = @At("HEAD"), cancellable = true)
    public void renderCloak(float p_renderCape_1_, CallbackInfo ci) {
    	if(Sakura.instance.getModuleManager().getModule(Interface.class).wavey.get()) {
    		ci.cancel();
    	}
    }
}
