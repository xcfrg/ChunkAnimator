package lumien.chunkanimator.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import penner.easing.*;

import java.util.WeakHashMap;

public class AnimationHandler {

	public static int mode;
	public static int animationDuration;
	public static int easingFunction;
	public static boolean disableAroundPlayer;

	private final Minecraft mc = Minecraft.getInstance();
	private final WeakHashMap<ChunkRender, AnimationData> timeStamps = new WeakHashMap<>();

	private double horizon = 63;

	public void preRender(ChunkRender renderChunk) {
		final AnimationData animationData = timeStamps.get(renderChunk);

		if (animationData == null)
			return;

		long time = animationData.timeStamp;

		if (time == -1L) {
			time = System.currentTimeMillis();

			animationData.timeStamp = time;

			// Mode 4 Set Chunk Facing
			if (mode == 4) {
				BlockPos zeroedPlayerPosition = this.mc.player != null ? this.mc.player.getPosition() : BlockPos.ZERO;
				zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);

				BlockPos zeroedCenteredChunkPos = renderChunk.getPosition().add(8, -renderChunk.getPosition().getY(), 8);

				Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

				int difX = Math.abs(dif.getX());
				int difZ = Math.abs(dif.getZ());

				animationData.chunkFacing = difX > difZ ? (dif.getX() > 0 ? Direction.EAST : Direction.WEST) : (dif.getZ() > 0 ? Direction.SOUTH : Direction.NORTH);
			}
		}

		long timeDif = System.currentTimeMillis() - time;

		if (timeDif < animationDuration) {
			int chunkY = renderChunk.getPosition().getY();

			int animationMode = mode == 2 ? (chunkY < this.horizon ? 0 : 1) : mode;

			if (animationMode == 4)
				animationMode = 3;

			switch (animationMode) {
				case 0:
					GlStateManager.translated(0, -chunkY + getFunctionValue(timeDif, 0, chunkY, animationDuration), 0);
					break;
				case 1:
					GlStateManager.translated(0, 256 - chunkY - getFunctionValue(timeDif, 0, 256 - chunkY, animationDuration), 0);
					break;
				case 3:
					Direction chunkFacing = animationData.chunkFacing;

					if (chunkFacing != null) {
						Vec3i vec = chunkFacing.getDirectionVec();
						double mod = -(200 - getFunctionValue(timeDif, 0, 200, animationDuration));

						GlStateManager.translated(vec.getX() * mod, 0, vec.getZ() * mod);
					}
					break;
			}
		} else {
			this.timeStamps.remove(renderChunk);
		}
	}

	private float getFunctionValue(float t, float b, float c, float d) {
		switch (easingFunction) {
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

	public void setOrigin(ChunkRender renderChunk, BlockPos position) {
		if (this.mc.player == null)
			return;

		boolean flag = true;
		BlockPos zeroedPlayerPosition = this.mc.player.getPosition();
		zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
		BlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);

		if (disableAroundPlayer) {
			flag = zeroedPlayerPosition.distanceSq(zeroedCenteredChunkPos) > (64 * 64);
		}

		if (flag) {
			Direction chunkFacing = null;

			if (mode == 3) {
				Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);

				int difX = Math.abs(dif.getX());
				int difZ = Math.abs(dif.getZ());

				if (difX > difZ) {
					if (dif.getX() > 0) {
						chunkFacing = Direction.EAST;
					} else {
						chunkFacing = Direction.WEST;
					}
				} else {
					if (dif.getZ() > 0) {
						chunkFacing = Direction.SOUTH;
					} else {
						chunkFacing = Direction.NORTH;
					}
				}
			}

			timeStamps.put(renderChunk, new AnimationData(-1L, chunkFacing));
		} else {
			timeStamps.remove(renderChunk);
		}
	}

	public void setHorizon(double horizon) {
		this.horizon = horizon;
	}

	public void clear () {
		// These should be cleared by GC, but just in case.
		this.timeStamps.clear();
		// Reset void fog height to default.
		this.horizon = 63;
	}

	private static class AnimationData {
		public long timeStamp;

		public Direction chunkFacing;

		public AnimationData(long timeStamp, Direction chunkFacing) {
			this.timeStamp = timeStamp;
			this.chunkFacing = chunkFacing;
		}
	}

}
