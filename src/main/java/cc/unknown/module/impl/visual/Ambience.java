package cc.unknown.module.impl.visual;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.PacketEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.Render3DForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

@SuppressWarnings("rawtypes")
@ModuleInfo(name = "Ambience", category = Category.VISUALS)
public class Ambience extends Module {

	private final SliderValue time = new SliderValue("Time", this, 0, 0, 1, 0.01f);
	
	@Override
	public void onDisable() {
		clear();
	}
	
	@Kisoji
	public final Listener<Render3DForgeEvent> onRender3D = event -> mc.theWorld.setWorldTime((long) (time.getValue() * 22999));

	@Kisoji
	public final Listener<MotionEvent.Pre> onPreMotion = event -> clear();

	@Kisoji
	public final Listener<PacketEvent> onPacket = event -> {
		if (event.isOutgoing()) return;
		Packet packet = event.getPacket();
		
		if (packet instanceof S03PacketTimeUpdate) {
			event.setCancelled(true);
		} else if (packet instanceof S2BPacketChangeGameState) {
			S2BPacketChangeGameState wrapped = (S2BPacketChangeGameState) packet;
			if (wrapped.getGameState() == 1 || wrapped.getGameState() == 2) {
				event.setCancelled(true);
			}
		}
	};
	
	private void clear() {
		mc.theWorld.setRainStrength(0);
		mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.theWorld.getWorldInfo().setRainTime(0);
		mc.theWorld.getWorldInfo().setThunderTime(0);
		mc.theWorld.getWorldInfo().setRaining(false);
		mc.theWorld.getWorldInfo().setThundering(false);
	}
}