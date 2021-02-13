package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import lumien.chunkanimator.lib.Reference;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class ChunkAnimator
{
	public static ChunkAnimator INSTANCE;
	
	public AnimationHandler animationHandler;
	
	public ChunkAnimatorConfig config;
	
	public ChunkAnimator()
	{
		INSTANCE = this;
		
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ChunkAnimatorConfig.spec);
		modEventBus.register(ChunkAnimatorConfig.class);
		
		animationHandler = new AnimationHandler();
	}
}
