package lumien.chunkanimator.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.BlockPos;

public class AsmHandler {

	private static final AnimationHandler handler = ChunkAnimator.INSTANCE.animationHandler;

	/**
	 * Special case for OptiFine, which doesn't use {@link MatrixStack} in chunk rendering.
	 *
	 * @param renderChunk The {@link ChunkRenderDispatcher.ChunkRender} object.
	 */
	public static void preRenderChunk(ChunkRenderDispatcher.ChunkRender renderChunk) {
		handler.preRender(renderChunk, null);
	}

	public static void preRenderChunk(ChunkRenderDispatcher.ChunkRender renderChunk, MatrixStack matrixStack) {
		handler.preRender(renderChunk, matrixStack);
	}

	public static void setOrigin(ChunkRenderDispatcher.ChunkRender renderChunk, int oX, int oY, int oZ) {
		handler.setOrigin(renderChunk, new BlockPos(oX, oY, oZ));
	}

}
