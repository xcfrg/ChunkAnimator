package lumien.chunkanimator.handler;

import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

public class AsmHandler
{
	public static void preRenderChunk(RenderChunk renderChunk)
	{
		ChunkAnimator.INSTANCE.animationHandler.preRender(renderChunk);
	}

	public static void setPosition(RenderChunk renderChunk, BlockPos position)
	{
		ChunkAnimator.INSTANCE.animationHandler.setPosition(renderChunk, position);
	}
}
