package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Sakura;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.player.PlayerExt;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

	@Inject(method = "onUpdate()V", at = @At("HEAD"))
	private void runTickPre(CallbackInfo ci) {
        Interface cape = Sakura.instance.getModuleManager().getModule(Interface.class);
        if(cape.wavey.get()){
            PlayerExt.simulate((EntityPlayer) (Object) this);
        }
	}
}
