package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.util.player.PlayerExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(ModelBiped.class)
@SideOnly(Side.CLIENT)
public abstract class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public int heldItemRight;

    @Shadow
    public ModelRenderer bipedHead;
    
    @Shadow
    public abstract void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn);

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float p_setRotationAngles_1_, float p_setRotationAngles_2_, float p_setRotationAngles_3_, float p_setRotationAngles_4_, float p_setRotationAngles_5_, float p_setRotationAngles_6_, Entity p_setRotationAngles_7_, CallbackInfo callbackInfo) {
        if (heldItemRight == 3) bipedRightArm.rotateAngleY = 0F;
        
        final EntityPlayerSP entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (p_setRotationAngles_7_ == entityPlayer) {
            this.bipedHead.rotateAngleX = (PlayerExt.prevRenderPitchHead + (PlayerExt.renderPitchHead - PlayerExt.prevRenderPitchHead) * Minecraft.getMinecraft().timer.renderPartialTicks) / (180.0F / (float) Math.PI);
        }
    }
}