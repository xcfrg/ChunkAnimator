package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import lumien.chunkanimator.handler.WorldEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author lumien231
 */
@Mod(ChunkAnimator.MOD_ID)
public class ChunkAnimator {

	public static final String MOD_ID = "chunkanimator";

	public static ChunkAnimator INSTANCE;

	public AnimationHandler animationHandler;

	public ChunkAnimator() {
		INSTANCE = this;

		final ModLoadingContext loadingContext = ModLoadingContext.get();

		loadingContext.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		loadingContext.registerConfig(ModConfig.Type.CLIENT, ChunkAnimatorConfig.SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
	}

	/**
	 * Performs setup tasks that should only be run on the client.
	 *
	 * @param event The {@link FMLClientSetupEvent} instance.
	 */
	private void setupClient(final FMLClientSetupEvent event) {
		this.animationHandler = new AnimationHandler();

		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
	}

}
