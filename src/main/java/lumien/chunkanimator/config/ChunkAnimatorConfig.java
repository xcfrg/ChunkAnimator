package lumien.chunkanimator.config;

import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ChunkAnimatorConfig
{
	// Animation Mode
	Property propertyMode;
	int mode;

	// Easing Function
	Property propertyEasingFunction;
	int easingFunction;

	// Animation Duration
	Property propertyAnimationDuration;
	int animationDuration;

	// Disable around player
	Property propertyDisableAroundPlayer;
	boolean disableAroundPlayer;

	Configuration config;

	public void preInit(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		propertyMode = config.get("settings", "Mode", 0, "How should the chunks be animated?\n 0: Chunks always appear from below\n 1: Chunks always appear from above\n 2: Chunks appear from below if they are lower than the Horizon and from above if they are higher than the Horizon\n 3: Chunks \"slide in\" from their respective cardinal direction (Relative to the Player)\n 4: Same as 3 but the cardinal direction of a chunk is determined slightly different (Just try both :D)");
		propertyEasingFunction = config.get("settings", "EasingFunction", 6,"The function that should be used to control the movement of chunks in ALL animation modes\nIf you want a visual comparison there is a link on the curseforge page\n0: Linear, 1: Quadratic, 2: Cubic, 3: Quartic, 4: Quintic, 5: Expo, 6: Sin, 7: Circle, 8: Back, 9: Bounce, 10: Elastic");
		propertyAnimationDuration = config.get("settings", "AnimationDuration", 1000, "How long should the animation last? (In milliseconds)");
		propertyDisableAroundPlayer = config.get("settings", "DisableAroundPlayer", false, "If enabled chunks that are next to the player will not animate");

		syncConfig();
	}

	public void syncConfig()
	{
		mode = propertyMode.getInt();
		easingFunction = propertyEasingFunction.getInt();
		animationDuration = propertyAnimationDuration.getInt();
		disableAroundPlayer = propertyDisableAroundPlayer.getBoolean();

		if (config.hasChanged())
		{
			config.save();
		}
	}

	public int getMode()
	{
		return mode;
	}

	public boolean disableAroundPlayer()
	{
		return disableAroundPlayer;
	}

	public int getAnimationDuration()
	{
		return animationDuration;
	}

	public String getString()
	{
		return config.toString();
	}

	public List<IConfigElement> getConfigElements()
	{
		return new ConfigElement(config.getCategory("settings")).getChildElements();
	}

	public int getEasingFunction()
	{
		return easingFunction;
	}
}
