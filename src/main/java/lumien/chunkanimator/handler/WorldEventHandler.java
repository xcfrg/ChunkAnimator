package lumien.chunkanimator.handler;

import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author Harley O'Connor
 */
public final class WorldEventHandler {

    private static final AnimationHandler handler = ChunkAnimator.INSTANCE.animationHandler;

    @SubscribeEvent
    public void worldLoad (final WorldEvent.Load event) {
        if (!(event.getWorld() instanceof ClientWorld))
            return;

        handler.setVoidFogHeight(((ClientWorld) event.getWorld()).getWorldInfo().getVoidFogHeight());
    }

    @SubscribeEvent
    public void worldUnload (final WorldEvent.Unload event) {
        if (!(event.getWorld() instanceof ClientWorld))
            return;

        handler.clear();
    }

}
