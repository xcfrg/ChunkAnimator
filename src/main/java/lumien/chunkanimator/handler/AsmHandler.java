package lumien.chunkanimator.handler;

import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.util.math.BlockPos;

public class AsmHandler {

	private static final AnimationHandler handler = ChunkAnimator.INSTANCE.animationHandler;

	public static void preRenderChunk(ChunkRender renderChunk) {
		handler.preRender(renderChunk);
	}

	public static void setOrigin(ChunkRender renderChunk, int oX, int oY, int oZ) {
		handler.setOrigin(renderChunk, new BlockPos(oX, oY, oZ));
	}

}
