package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import lumien.chunkanimator.lib.Reference;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(Reference.MOD_ID)
public class ChunkAnimator {

	public static ChunkAnimator INSTANCE;

	public AnimationHandler animationHandler;

	public ChunkAnimator() {
		INSTANCE = this;

		final ModLoadingContext loadingContext = ModLoadingContext.get();

		loadingContext.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		loadingContext.registerConfig(ModConfig.Type.CLIENT, ChunkAnimatorConfig.SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
	}

	private void setupClient(final FMLClientSetupEvent event) {
		this.animationHandler = new AnimationHandler();
	}

}
