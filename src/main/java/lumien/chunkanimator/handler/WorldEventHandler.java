package lumien.chunkanimator.handler;

import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles {@link WorldEvent}s, updating {@link AnimationHandler} properties when the world
 * loads/unloads.
 *
 * @author Harley O'Connor
 */
@OnlyIn(Dist.CLIENT)
public final class WorldEventHandler {

    private static final AnimationHandler handler = ChunkAnimator.INSTANCE.animationHandler;

    @SubscribeEvent
    public void worldUnload (final WorldEvent.Unload event) {
        if (!(event.getWorld() instanceof ClientWorld))
            return;

        handler.clear();
    }

}
