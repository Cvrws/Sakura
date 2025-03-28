package cc.unknown.event.impl.forge;

import cc.unknown.Sakura;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeEventListener {

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) { // client tick
	    Sakura.instance.getEventBus().handle(new ClientTickForgeEvent(event));
	}
	
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) { // render tick
		Sakura.instance.getEventBus().handle(new RenderTickForgeEvent(event));
	}
	
	@SubscribeEvent
	public void onMouse(MouseEvent event) { // mouse tick
		Sakura.instance.getEventBus().handle(new MouseForgeEvent(event));
	}
	
    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) { // render 3d
        Sakura.instance.getEventBus().handle(new Render3DForgeEvent(event));
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) { // keyboard input
    	Sakura.instance.getEventBus().handle(new KeyInputEvent(event));
    }
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) { // load world
    	Sakura.instance.getEventBus().handle(new WorldForgeEvent(event));
    }
    
    @SubscribeEvent
    public void onPreNametags(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) { // post render nametags
        Sakura.instance.getEventBus().handle(new NameTagsForgeEvent(event, true));
    }

    @SubscribeEvent
    public void onPostNametags(RenderLivingEvent.Specials.Post<EntityLivingBase> event) { // pre render nametags
        Sakura.instance.getEventBus().handle(new NameTagsForgeEvent(event, false));
    }
}
