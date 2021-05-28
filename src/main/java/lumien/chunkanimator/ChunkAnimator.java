package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ChunkAnimator.MOD_ID, name = ChunkAnimator.MOD_NAME, version = ChunkAnimator.MOD_VERSION, guiFactory = "lumien.chunkanimator.config.ChunkAnimatorGuiFactory", clientSideOnly = true)
public class ChunkAnimator {
    public static final String MOD_ID = "ChunkAnimator";
    public static final String MOD_NAME = "Chunk Animator";
    public static final String MOD_VERSION = "@VERSION@";
    @Instance(value = ChunkAnimator.MOD_ID)
    public static ChunkAnimator INSTANCE;

    public AnimationHandler animationHandler;

    public ChunkAnimatorConfig config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        animationHandler = new AnimationHandler();

        config = new ChunkAnimatorConfig();
        config.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.modID.equals(ChunkAnimator.MOD_ID)) {
            config.syncConfig();
        }
    }
}
