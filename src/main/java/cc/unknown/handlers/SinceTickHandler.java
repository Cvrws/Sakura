package cc.unknown.handlers;

import cc.unknown.event.Kisoji;
import cc.unknown.event.Priority;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.TickForgeEvent;
import cc.unknown.mixin.impl.IBlockPos;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.PlayerExt;
import cc.unknown.util.structure.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class SinceTickHandler implements Accessor {
	
	@Kisoji
	public final Listener<TickForgeEvent> onPreTick = event -> {
		if (event.isPost()) return;
		if (mc.thePlayer != null) {
			PlayerExt.lastMotionX = mc.thePlayer.motionX;
			PlayerExt.lastMotionY = mc.thePlayer.motionY;
			PlayerExt.lastMotionZ = mc.thePlayer.motionZ;
			PlayerExt.lastGround = mc.thePlayer.onGround;

			PlayerExt.lastMovementYaw = PlayerExt.movementYaw;
			PlayerExt.movementYaw = PlayerExt.velocityYaw = mc.thePlayer.rotationYaw;
		}
	};

	@Kisoji(value = Priority.VERY_LOW)
	public final Listener<PacketEvent> onPacket = event -> {
		if (mc == null || mc.theWorld == null || event.isCancelled())
			return;

		Packet<?> packet = event.getPacket();

		if (event.isIncoming()) {
			if (packet instanceof S12PacketEntityVelocity) {
				final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

				Entity entity = mc.theWorld.getEntityByID(wrapper.getEntityID());

				if (entity == null) {
					return;
				}

				PlayerExt.lastVelocityDeltaX = wrapper.motionX / 8000.0D;
				PlayerExt.lastVelocityDeltaY = wrapper.motionY / 8000.0D;
				PlayerExt.lastVelocityDeltaZ = wrapper.motionZ / 8000.0D;
				PlayerExt.ticksSinceVelocity = 0;
				if (wrapper.motionY / 8000.0D > 0.1 && Math.hypot(wrapper.motionZ / 8000.0D, wrapper.motionX / 8000.0D) > 0.2) {
					PlayerExt.ticksSincePlayerVelocity = 0;
				}
			}
		}

		if (event.isOutgoing()) {
			if (packet instanceof C08PacketPlayerBlockPlacement) {
				C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) packet;
				if (!((IBlockPos) wrapper.getPosition()).equalsVector(new Vector3d(-1, -1, -1))) {
					PlayerExt.ticksSincePlace = 0;
				}
			}

			if (packet instanceof C02PacketUseEntity) {
				C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;

				if (wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
					PlayerExt.ticksSinceAttack = 0;
				}
			}
			PlayerExt.ticksSinceAttack++;
		}
	};
}