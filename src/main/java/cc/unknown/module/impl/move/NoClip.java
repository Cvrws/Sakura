package cc.unknown.module.impl.move;

import java.awt.Color;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.BlockAABBEvent;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.PushOutOfBlockEvent;
import cc.unknown.event.impl.Render2DEvent;
import cc.unknown.event.impl.UpdateEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.handlers.RotationHandler;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.client.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.MoveFix;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.structure.Vector2f;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(name = "NoClip", category = Category.MOVEMENT)
public class NoClip extends Module {

	private final SliderValue rotSpeed = new SliderValue("RotationSpeed", this, 2, 1, 10, 1);
	private final BoolValue nokb = new BoolValue("NoKbWhileNotMoving", this, false);
	private final BoolValue noSwing = new BoolValue("NoSwing", this, false);
	private final BoolValue spoof = new BoolValue("SpoofSlot", this, true);

	private int lastSlot;
	
	@Override
	public void onEnable() {
		lastSlot = -1;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.noClip = false;
		mc.thePlayer.inventory.currentItem = lastSlot;
		SpoofHandler.stopSpoofing();
	}
	
	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isIncoming()) {
			if (event.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) event.getPacket();
				if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
					
					if (nokb.get() && PlayerUtil.insideBlock() && MoveUtil.isMoving()) {
					    wrapper.motionX *= 0 / 100;
					    wrapper.motionY *= 0 / 100;
					    wrapper.motionZ *= 0 / 100;
	
					    event.setPacket(wrapper);
					}
				}
			}
		}
	};

	@Kisoji
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (PlayerUtil.insideBlock()) {
			event.setBoundingBox(null);

			if (!(event.getBlock() instanceof BlockAir) && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(),
						z = event.getBlockPos().getZ();

				if (y < mc.thePlayer.posY) {
					event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
				}
			}
		}
	};

	@Kisoji
	public final Listener<PushOutOfBlockEvent> onPushOutOfBlock = event -> event.setCancelled(true);

	@Kisoji
	public final Listener<UpdateEvent.Pre> onPreUpdate = event -> {
		if (lastSlot == -1) {
			lastSlot = mc.thePlayer.inventory.currentItem;
		}

		mc.thePlayer.noClip = true;

		/*
		 * if (isEnabled(Scaffold.class) || getModule(KillAura.class).target != null)
		 * return;
		 */

		final int slot = InventoryUtil.findBlock();

		if (slot == -1 || PlayerUtil.insideBlock()) {
			return;
		}

		mc.thePlayer.inventory.currentItem = slot;
		
		if (spoof.get())
			SpoofHandler.startSpoofing(lastSlot);
		
		RotationHandler.setRotations(new Vector2f(mc.thePlayer.rotationYaw, 90), rotSpeed.getValue(), MoveFix.SILENT);

		if (RotationHandler.rotations.y >= 89
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
				&& mc.thePlayer.posY == mc.objectMouseOver.getBlockPos().up().getY()) {
			mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, PlayerUtil.getItemStack(),
					mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);

			if (noSwing.get()) {
				PacketUtil.send(new C0APacketAnimation());
			} else {
				mc.thePlayer.swingItem();
			}
		}
	};

	@Kisoji
	public final Listener<Render2DEvent> onRender2D = event -> {

		final String name = "presiona shift";
		FontUtil.getFontRenderer("comfortaa.ttf", 17).drawCenteredString(name,
				event.resolution.getScaledWidth() / 2F,
				event.resolution.getScaledHeight() - 89.5F, new Color(0, 0, 0, 200).hashCode());
		FontUtil.getFontRenderer("comfortaa.ttf", 17).drawCenteredString(name,
				event.resolution.getScaledWidth() / 2F, event.resolution.getScaledHeight() - 90,
				getModule(Interface.class).color());
	};
}