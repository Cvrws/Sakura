package cc.unknown.event.impl.forge;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;

public class NameTagsForgeEvent extends ForgeEvent<RenderLivingEvent.Specials<EntityLivingBase>> {

    private final boolean isPre;

    public NameTagsForgeEvent(RenderLivingEvent.Specials<EntityLivingBase> event, boolean isPre) {
        super(event);
        this.isPre = isPre;
    }

    public boolean isPre() {
        return isPre;
    }

    public boolean isPost() {
        return !isPre;
    }
}