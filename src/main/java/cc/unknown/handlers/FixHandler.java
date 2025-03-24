package cc.unknown.handlers;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.util.Accessor;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;

public class FixHandler implements Accessor {
	private boolean inGUI;
	
	/*
	 * Gui Close Fix
	 */
	@Kisoji
	public final Listener<MotionEvent.Pre> onPreMotion = event -> {
		if (mc.currentScreen == null && inGUI) {
			for (final KeyBinding bind : mc.gameSettings.keyBindings) {
				bind.pressed = GameSettings.isKeyDown(bind);
			}
		}

		inGUI = mc.currentScreen != null;
	};
	
	/*
	 * Particles Fix
	 */
    @Kisoji
    public final Listener<PacketEvent> onReceive = event -> {
        final Packet<?> packet = event.getPacket();
        if (!event.isIncoming()) return;
        if (packet instanceof S2APacketParticles) {
        	final S2APacketParticles wrapper = ((S2APacketParticles) packet);
        	
        	final double distance = mc.thePlayer.getDistanceSq(wrapper.getXCoordinate(), wrapper.getYCoordinate(), wrapper.getZCoordinate());
        	
        	if (distance >= 26) {
        		event.isCancel();
	        }
        }
    };
}
