package lumien.chunkanimator.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import lumien.chunkanimator.config.ChunkAnimatorConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import penner.easing.*;

import javax.annotation.Nullable;
import java.util.WeakHashMap;

/**
 * This class handles setting up and rendering the animations.
 *
 * @author lumien231
 */
@OnlyIn(Dist.CLIENT)
public class AnimationHandler {

	private final Minecraft mc = Minecraft.getInstance();
	private final WeakHashMap<ChunkRenderDispatcher.ChunkRender, AnimationData> timeStamps = new WeakHashMap<>();

	private double horizontalHeight = 63;

	public void preRender(final ChunkRenderDispatcher.ChunkRender renderChunk, @Nullable final MatrixStack matrixStack) {
		final AnimationData animationData = timeStamps.get(renderChunk);

		if (animationData == null)
			return;

		final int mode = ChunkAnimatorConfig.MODE.get();
		final int animationDuration = ChunkAnimatorConfig.ANIMATION_DURATION.get();

		long time = animationData.timeStamp;

		// If preRender hasn't been called on this chunk yet, prepare to start the animation.
		if (time == -1L) {
			time = System.currentTimeMillis();

			animationData.timeStamp = time;

			// If using mode 4, set chunkFacing.
			if (mode == 4) {
				animationData.chunkFacing = this.mc.player != null ?
						this.getChunkFacing(this.getZeroedPlayerPos(this.mc.player).subtract(this.getZeroedCenteredChunkPos(renderChunk.getOrigin()))) : Direction.NORTH;
			}
		}

		final long timeDif = System.currentTimeMillis() - time;

		if (timeDif < animationDuration) {
			final int chunkY = renderChunk.getOrigin().getY();
			final int animationMode = mode == 2 ? (chunkY < this.horizontalHeight ? 0 : 1) : mode == 4 ? 3 : mode;

			switch (animationMode) {
				case 0:
					this.translate(matrixStack, 0, -chunkY + this.getFunctionValue(timeDif, 0, chunkY, animationDuration), 0);
					break;
				case 1:
					this.translate(matrixStack, 0, 256 - chunkY - this.getFunctionValue(timeDif, 0, 256 - chunkY, animationDuration), 0);
					break;
				case 3:
					Direction chunkFacing = animationData.chunkFacing;

					if (chunkFacing != null) {
						final Vector3i vec = chunkFacing.getNormal();
						final double mod = -(200 - this.getFunctionValue(timeDif, 0, 200, animationDuration));

						this.translate(matrixStack, vec.getX() * mod, 0, vec.getZ() * mod);
					}
					break;
			}
		} else {
			this.timeStamps.remove(renderChunk);
		}
	}

	/**
	 * Translates with correct method, depending on whether OptiFine is installed ({@link MatrixStack}
	 * not used so set to null), or not.
	 *
	 * @param matrixStack The {@link MatrixStack} object, or null if OptiFine is loaded.
	 * @param x The x to translate by.
	 * @param y The y to translate by.
	 * @param z The z to translate by.
	 */
	@SuppressWarnings("deprecation")
	private void translate (@Nullable final MatrixStack matrixStack, final double x, final double y, final double z) {
		if (matrixStack == null) {
			GlStateManager._translated(x, y, z); // OptiFine still uses GlStateManager.
		} else {
			matrixStack.translate(x, y, z);
		}
	}

	/**
	 * Gets the function value for the given parameters based on {@link ChunkAnimatorConfig#EASING_FUNCTION}.
	 *
	 * @param t The first function argument.
	 * @param b The second function argument.
	 * @param c The third function argument.
	 * @param d The fourth function argument.
	 * @return The return value of the function.
	 */
	private float getFunctionValue(final float t, @SuppressWarnings("SameParameterValue") final float b, final float c, final float d) {
		switch (ChunkAnimatorConfig.EASING_FUNCTION.get()) {
			case 0: // Linear
				return Linear.easeOut(t, b, c, d);
			case 1: // Quadratic Out
				return Quad.easeOut(t, b, c, d);
			case 2: // Cubic Out
				return Cubic.easeOut(t, b, c, d);
			case 3: // Quartic Out
				return Quart.easeOut(t, b, c, d);
			case 4: // Quintic Out
				return Quint.easeOut(t, b, c, d);
			case 5: // Expo Out
				return Expo.easeOut(t, b, c, d);
			case 6: // Sin Out
				return Sine.easeOut(t, b, c, d);
			case 7: // Circle Out
				return Circ.easeOut(t, b, c, d);
			case 8: // Back
				return Back.easeOut(t, b, c, d);
			case 9: // Bounce
				return Bounce.easeOut(t, b, c, d);
			case 10: // Elastic
				return Elastic.easeOut(t, b, c, d);
		}

		return Sine.easeOut(t, b, c, d);
	}

	public void setOrigin(final ChunkRenderDispatcher.ChunkRender renderChunk, final BlockPos position) {
		if (this.mc.player == null)
			return;

		final BlockPos zeroedPlayerPos = this.getZeroedPlayerPos(this.mc.player);
		final BlockPos zeroedCenteredChunkPos = this.getZeroedCenteredChunkPos(position);

		if (!ChunkAnimatorConfig.DISABLE_AROUND_PLAYER.get() || zeroedPlayerPos.distSqr(zeroedCenteredChunkPos) > (64 * 64)) {
			timeStamps.put(renderChunk, new AnimationData(-1L, ChunkAnimatorConfig.MODE.get() == 3 ?
					this.getChunkFacing(zeroedPlayerPos.subtract(zeroedCenteredChunkPos)) : null));
		} else {
			timeStamps.remove(renderChunk);
		}
	}

	/**
	 * Gets the given player's position, setting their {@code y-coordinate} to {@code 0}.
	 *
	 * @param player The {@link ClientPlayerEntity} instance.
	 * @return The zeroed {@link BlockPos}.
	 */
	private BlockPos getZeroedPlayerPos (final ClientPlayerEntity player) {
		final BlockPos playerPos = player.blockPosition();
		return playerPos.offset(0, -playerPos.getY(), 0);
	}

	/**
	 * Gets the given {@link BlockPos} for the chunk, setting its {@code y-coordinate} to
	 * {@code 0} and offsetting its {@code x} and {@code y-coordinate} to by {@code 8}.
	 *
	 * @param position The {@link BlockPos} of the chunk.
	 * @return The zeroed, centered {@link BlockPos}.
	 */
	private BlockPos getZeroedCenteredChunkPos(final BlockPos position) {
		return position.offset(8, -position.getY(), 8);
	}

	/**
	 * Gets the direction the chunk is facing based on the given {@link Vector3i}
	 * from the relevant position to the chunk.
	 *
	 * @param dif The {@link Vector3i} distance from the relevant position to the chunk.
	 * @return The {@link Direction} of the chunk relative to the {@code dif}.
	 */
	private Direction getChunkFacing(final Vector3i dif) {
		int difX = Math.abs(dif.getX());
		int difZ = Math.abs(dif.getZ());

		return difX > difZ ? dif.getX() > 0 ? Direction.EAST : Direction.WEST : dif.getZ() > 0 ? Direction.SOUTH : Direction.NORTH;
	}

	public void setHorizontalHeight(final double horizontalHeight) {
		this.horizontalHeight = horizontalHeight;
	}

	public void clear () {
		// These should be cleared by GC, but just in case.
		this.timeStamps.clear();
		// Reset void fog height to default.
		this.horizontalHeight = 63;
	}

	private static class AnimationData {
		public long timeStamp;
		public Direction chunkFacing;

		public AnimationData(final long timeStamp, final Direction chunkFacing) {
			this.timeStamp = timeStamp;
			this.chunkFacing = chunkFacing;
		}
	}

}
