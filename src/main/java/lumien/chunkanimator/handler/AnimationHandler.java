package lumien.chunkanimator.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import lumien.chunkanimator.config.ChunkAnimatorConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import penner.easing.*;

import java.util.WeakHashMap;

public class AnimationHandler
{
	WeakHashMap<ChunkRenderDispatcher.ChunkRender, AnimationData> timeStamps;

	public AnimationHandler()
	{
		timeStamps = new WeakHashMap<>();
	}

	@SuppressWarnings("deprecation") // OptiFine doesn't give us a choice in using GlStateManager.translated
	public void preRender(ChunkRenderDispatcher.ChunkRender renderChunk, MatrixStack matrixStack)
	{
		if (timeStamps.containsKey(renderChunk))
		{
			AnimationData animationData = timeStamps.get(renderChunk);
			long time = animationData.timeStamp;
			int mode = ChunkAnimatorConfig.mode.get();

			if (time == -1L)
			{
				time = System.currentTimeMillis();

				animationData.timeStamp = time;

				// Mode 4 Set Chunk Facing
				if (mode == 4)
				{
					BlockPos zeroedPlayerPosition = Minecraft.getInstance().player.getPosition();
					zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);

					BlockPos zeroedCenteredChunkPos = renderChunk.getPosition().add(8, -renderChunk.getPosition().getY(), 8);

					Vector3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

					int difX = Math.abs(dif.getX());
					int difZ = Math.abs(dif.getZ());

					Direction chunkFacing;

					if (difX > difZ)
					{
						if (dif.getX() > 0)
						{
							chunkFacing = Direction.EAST;
						}
						else
						{
							chunkFacing = Direction.WEST;
						}
					}
					else
					{
						if (dif.getZ() > 0)
						{
							chunkFacing = Direction.SOUTH;
						}
						else
						{
							chunkFacing = Direction.NORTH;
						}
					}

					animationData.chunkFacing = chunkFacing;
				}
			}

			long timeDif = System.currentTimeMillis() - time;

			int animationDuration = ChunkAnimatorConfig.animationDuration.get();

			if (timeDif < animationDuration)
			{
				int chunkY = renderChunk.getPosition().getY();
				double modY;

				if (mode == 2)
				{
					if (chunkY < Minecraft.getInstance().world.getWorldInfo().getVoidFogHeight())
					{
						mode = 0;
					}
					else
					{
						mode = 1;
					}
				}

				if (mode == 4)
				{
					mode = 3;
				}

				switch (mode) {
					case 0:
						// OptiFine uses GlStateManger instead of MatrixStack, so if OptiFine is enabled we set matrix stack to null and use that instead.
						if (matrixStack == null) {
							GlStateManager.translated(0, -chunkY + getFunctionValue(timeDif, 0, chunkY, animationDuration), 0);
						} else {
							matrixStack.translate(0, -chunkY + getFunctionValue(timeDif, 0, chunkY, animationDuration), 0);
						}
						break;
					case 1:
						if (matrixStack == null) {
							GlStateManager.translated(0, 256 - chunkY - getFunctionValue(timeDif, 0, 256 - chunkY, animationDuration), 0);
						} else {
							matrixStack.translate(0, 256 - chunkY - getFunctionValue(timeDif, 0, 256 - chunkY, animationDuration), 0);
						}
						break;
					case 3:
						Direction chunkFacing = animationData.chunkFacing;

						if (chunkFacing != null)
						{
							Vector3i vec = chunkFacing.getDirectionVec();
							double mod = -(200D - (200D / animationDuration * timeDif));

							mod = -(200 - getFunctionValue(timeDif, 0, 200, animationDuration));

							if (matrixStack == null) {
								GlStateManager.translated(vec.getX() * mod, 0, vec.getZ() * mod);
							} else {
								matrixStack.translate(vec.getX() * mod, 0, vec.getZ() * mod);
							}
						}
						break;
				}
			}
			else
			{
				timeStamps.remove(renderChunk);
			}
		}
	}

	private float getFunctionValue(float t, float b, float c, float d)
	{
		switch (ChunkAnimatorConfig.easingFunction.get())
		{
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

	public void setOrigin(ChunkRenderDispatcher.ChunkRender renderChunk, BlockPos position)
	{
		if (Minecraft.getInstance().player != null)
		{
			boolean flag = true;
			BlockPos zeroedPlayerPosition = Minecraft.getInstance().player.getPosition();
			zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
			BlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);

			if (ChunkAnimatorConfig.disableAroundPlayer.get())
			{
				flag = zeroedPlayerPosition.distanceSq(zeroedCenteredChunkPos) > (64 * 64);
			}

			if (flag)
			{
				Direction chunkFacing = null;

				if (ChunkAnimatorConfig.mode.get() == 3)
				{
					Vector3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

					int difX = Math.abs(dif.getX());
					int difZ = Math.abs(dif.getZ());

					if (difX > difZ)
					{
						if (dif.getX() > 0)
						{
							chunkFacing = Direction.EAST;
						}
						else
						{
							chunkFacing = Direction.WEST;
						}
					}
					else
					{
						if (dif.getZ() > 0)
						{
							chunkFacing = Direction.SOUTH;
						}
						else
						{
							chunkFacing = Direction.NORTH;
						}
					}
				}

				AnimationData animationData = new AnimationData(-1L, chunkFacing);
				timeStamps.put(renderChunk, animationData);
			}
			else
			{
				timeStamps.remove(renderChunk);
			}
		}

	}

	private static class AnimationData
	{
		public long timeStamp;

		public Direction chunkFacing;

		public AnimationData(long timeStamp, Direction chunkFacing)
		{
			this.timeStamp = timeStamp;
			this.chunkFacing = chunkFacing;
		}
	}
}
