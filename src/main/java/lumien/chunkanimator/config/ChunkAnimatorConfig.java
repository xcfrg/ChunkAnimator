package lumien.chunkanimator.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ChunkAnimatorConfig
{

	// Animation Mode
	public static IntValue mode;

	// Easing Function
	public static IntValue easingFunction;

	// Animation Duration
	public static IntValue animationDuration;
	
	// Disable Around Player
	public static BooleanValue disableAroundPlayer;

	public ChunkAnimatorConfig(ForgeConfigSpec.Builder builder)
	{
		mode = builder.comment("How should the chunks be animated?\\n 0: Chunks always appear from below\\n 1: Chunks always appear from above\\n 2: Chunks appear from below if they are lower than the Horizon and from above if they are higher than the Horizon\\n 3: Chunks \\\"slide in\\\" from their respective cardinal direction (Relative to the Player)\\n 4: Same as 3 but the cardinal direction of a chunk is determined slightly different (Just try both :D)").defineInRange("mode", 0, 0, 4);
		easingFunction = builder.comment("The function that should be used to control the movement of chunks in ALL animation modes\\nIf you want a visual comparison there is a link on the curseforge page\\n0: Linear, 1: Quadratic, 2: Cubic, 3: Quartic, 4: Quintic, 5: Expo, 6: Sin, 7: Circle, 8: Back, 9: Bounce, 10: Elastic").defineInRange("easingFunction", 6, 0, 10);
		animationDuration = builder.comment("How long should the animation last? (In milliseconds)").defineInRange("animationDuration", 1000, 0, Integer.MAX_VALUE);
		disableAroundPlayer = builder.comment("If enabled chunks that are next to the player will not animate").define("disableAroundPlayer", false);
	}

	public void preInit(FMLCommonSetupEvent event)
	{
		
	}
	
    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) 
    {

    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent)
    {

    }
	
	public static final ForgeConfigSpec spec;
    public static final ChunkAnimatorConfig CONFIG;
    static {
        final Pair<ChunkAnimatorConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ChunkAnimatorConfig::new);
        spec = specPair.getRight();
        CONFIG = specPair.getLeft();
    }
}
