package lumien.chunkanimator.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.WorldRenderer;

import javax.annotation.Nullable;

/**
 * This class acts as a "middle man" between Minecraft's transformed classes and
 * the {@link AnimationHandler}.
 *
 * @author lumien231
 */
@OnlyIn(Dist.CLIENT)
public class AsmHandler {

	private static final AnimationHandler HANDLER = ChunkAnimator.INSTANCE.animationHandler;

	/**
	 * Calls {@link AnimationHandler#setOrigin(ChunkRenderDispatcher.ChunkRender, BlockPos)} with
	 * the given parameters.
	 *
	 * <p>The {@link ChunkRenderDispatcher.ChunkRender} transformer invokes this method, inserting
	 * the call in {@link ChunkRenderDispatcher.ChunkRender#setOrigin(int, int, int)}.</p>
	 *
	 * @param renderChunk The {@link ChunkRenderDispatcher.ChunkRender} instance.
	 * @param originX The {@code x-coordinate} for the origin.
	 * @param originY The {@code y-coordinate} for the origin.
	 * @param originZ The {@code z-coordinate} for the origin.
	 */
	public static void setOrigin(ChunkRenderDispatcher.ChunkRender renderChunk, int originX, int originY, int originZ) {
		HANDLER.setOrigin(renderChunk, new BlockPos(originX, originY, originZ));
	}

	/**
	 * Calls {@link AnimationHandler#preRender(ChunkRenderDispatcher.ChunkRender, MatrixStack)}
	 * with the given parameters.
	 *
	 * <p>The {@link WorldRenderer} transformer invokes this method, inserting the call in
	 * {@link WorldRenderer#renderChunkLayer(RenderType, MatrixStack, double, double, double)}.</p>
	 *
	 * @param renderChunk The {@link ChunkRenderDispatcher.ChunkRender} instance.
	 * @param matrixStack The {@link MatrixStack}, or {@code null} if using OptiFine.
	 */
	public static void preRenderChunk(final ChunkRenderDispatcher.ChunkRender renderChunk, final @Nullable MatrixStack matrixStack) {
		HANDLER.preRender(renderChunk, matrixStack);
	}

}
