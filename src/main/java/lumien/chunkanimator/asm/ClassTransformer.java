package lumien.chunkanimator.asm;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.accesstransformer.Target;

public class ClassTransformer implements ITransformer<ClassNode>
{
	Logger logger = LogManager.getLogger("ChunkAnimatorCore");

	final String asmHandler = "lumien/chunkanimator/handler/AsmHandler";

	public ClassTransformer()
	{
		logger.log(Level.DEBUG, "Starting Class Transformation");
	}

	private ClassNode patchRenderChunk(ClassNode classNode)
	{
		logger.log(Level.DEBUG, "Found RenderChunk Class: " + classNode.name);

		MethodNode setPosition = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals("setPosition"))
			{
				setPosition = mn;
				break;
			}
		}

		if (setPosition != null)
		{
			logger.log(Level.DEBUG, "- Found setOrigin");

			for (int i = 0; i < setPosition.instructions.size(); i++)
			{
				AbstractInsnNode ain;

				if ((ain = setPosition.instructions.get(i)) instanceof MethodInsnNode)
				{
					MethodInsnNode min = (MethodInsnNode) ain;
					if (min.name.equals(MCPNames.method("func_178585_h")))
					{
						InsnList toInsert = new InsnList();
						toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInsert.add(new VarInsnNode(Opcodes.ILOAD, 1));
						toInsert.add(new VarInsnNode(Opcodes.ILOAD, 2));
						toInsert.add(new VarInsnNode(Opcodes.ILOAD, 3));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, asmHandler, "setOrigin", "(Lnet/minecraft/client/renderer/chunk/RenderChunk;III)V", false));

						setPosition.instructions.insertBefore(min, toInsert);
						i+=5;
					}
				}
			}

			logger.log(Level.DEBUG, "- Patched setOrigin");
		}

		return classNode;
	}

	private ClassNode patchChunkRenderContainer(ClassNode classNode)
	{
		logger.log(Level.DEBUG, "Found ChunkRenderContainer Class: " + classNode.name);

		MethodNode preRenderChunk = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals("preRenderChunk"))
			{
				preRenderChunk = mn;
				break;
			}
		}

		if (preRenderChunk != null)
		{
			logger.log(Level.DEBUG, "- Found preRenderChunk");

			for (int i = 0; i < preRenderChunk.instructions.size(); i++)
			{
				AbstractInsnNode ain = preRenderChunk.instructions.get(i);

				if (ain instanceof MethodInsnNode)
				{
					MethodInsnNode min = (MethodInsnNode) ain;

					if (min.name.equals(MCPNames.method("func_179109_b")))
					{
						logger.log(Level.DEBUG, "- Patched preRenderChunk");

						InsnList toInsert = new InsnList();
						toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, asmHandler, "preRenderChunk", "(Lnet/minecraft/client/renderer/chunk/RenderChunk;)V", false));

						preRenderChunk.instructions.insert(min, toInsert);

						break;
					}
				}

			}
		}

		return classNode;
	}

	@Override
	public ClassNode transform(ClassNode input, ITransformerVotingContext context)
	{
		if (input.name.equals("net/minecraft/client/renderer/ChunkRenderContainer"))
		{
			return patchChunkRenderContainer(input);
		}
		else if (input.name.equals("net/minecraft/client/renderer/chunk/RenderChunk"))
		{
			return patchRenderChunk(input);
		}
		
		return null;
	}

	@Override
	public TransformerVoteResult castVote(ITransformerVotingContext context)
	{
		return TransformerVoteResult.YES;
	}

	@Override
	public Set<Target> targets()
	{
		Set<Target> targets = new LinkedHashSet<Target>();
		
		targets.add(Target.targetClass("net.minecraft.client.renderer.ChunkRenderContainer"));
		targets.add(Target.targetClass("net.minecraft.client.renderer.chunk.RenderChunk"));
		
		return targets;
	}
}
