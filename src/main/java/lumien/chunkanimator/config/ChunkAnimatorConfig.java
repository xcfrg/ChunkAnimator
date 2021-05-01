package lumien.chunkanimator.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

/**
 * @author lumien231
 */
public class ChunkAnimatorConfig {

	/** The animation mode - controls how the chunks should be animated. */
	public static final IntValue MODE;

	/** The easing function - controls which easing function should be used. */
	public static final IntValue EASING_FUNCTION;

	/** The animation duration - controls how long the animation should last (in milliseconds). */
	public static final IntValue ANIMATION_DURATION;
	
	/** Disable around player - disables animation of chunks near the player. */
	public static final BooleanValue DISABLE_AROUND_PLAYER;

	public static final ForgeConfigSpec SPEC;

	static {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		MODE = builder.comment("How should the chunks be animated?\\n 0: Chunks always appear from below\\n 1: Chunks always appear from above\\n " +
				"2: Chunks appear from below if they are lower than the Horizon and from above if they are higher than the Horizon\\n " +
				"3: Chunks \\\"slide in\\\" from their respective cardinal direction (Relative to the Player)\\n " +
				"4: Same as 3 but the cardinal direction of a chunk is determined slightly different (Just try both :D)")
				.defineInRange("mode", 0, 0, 4);

		EASING_FUNCTION = builder.comment("The function that should be used to control the movement of chunks in ALL animation modes\\n" +
				"If you want a visual comparison there is a link on the curseforge page\\n0: " +
				"Linear, 1: Quadratic, 2: Cubic, 3: Quartic, 4: Quintic, 5: Expo, 6: Sin, 7: Circle, 8: Back, 9: Bounce, 10: Elastic")
				.defineInRange("easingFunction", 6, 0, 10);

		ANIMATION_DURATION = builder.comment("How long should the animation last? (In milliseconds)")
				.defineInRange("animationDuration", 1000, 0, Integer.MAX_VALUE);

		DISABLE_AROUND_PLAYER = builder.comment("If enabled chunks that are next to the player will not animate")
				.define("disableAroundPlayer", false);

		SPEC = builder.build();
	}

}
