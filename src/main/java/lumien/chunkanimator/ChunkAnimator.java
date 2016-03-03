package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import lumien.chunkanimator.lib.Reference;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Reference.MOD_ID,name=Reference.MOD_NAME,version = Reference.MOD_VERSION,guiFactory = "lumien.chunkanimator.config.ChunkAnimatorGuiFactory",clientSideOnly=true)
public class ChunkAnimator
{
	@Instance(value = Reference.MOD_ID)
	public static ChunkAnimator INSTANCE;
	
	public AnimationHandler animationHandler;
	
	public ChunkAnimatorConfig config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		animationHandler = new AnimationHandler();
		
		config = new ChunkAnimatorConfig();
		config.preInit(event);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.modID.equals(Reference.MOD_ID))
		{
			config.syncConfig();
		}
	}
}
