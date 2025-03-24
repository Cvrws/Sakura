package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;

import cc.unknown.Sakura;
import cc.unknown.event.impl.LookEvent;
import cc.unknown.util.structure.Vector2f;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.Vec3;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

	@Override
	public Vec3 getLook(final float partialTicks) {
		float yaw = this.rotationYaw;
		float pitch = this.rotationPitch;

		LookEvent lookEvent = new LookEvent(new Vector2f(yaw, pitch));
		Sakura.instance.getEventBus().handle(lookEvent);
		yaw = lookEvent.getRotation().x;
		pitch = lookEvent.getRotation().y;

		return this.getVectorForRotation(pitch, yaw);
	}
}
