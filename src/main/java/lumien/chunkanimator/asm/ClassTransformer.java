package lumien.chunkanimator.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class ClassTransformer implements IClassTransformer
{
	Logger logger = LogManager.getLogger("ChunkAnimatorCore");

	final String asmHandler = "lumien/chunkanimator/handler/AsmHandler";

	public ClassTransformer()
	{
		logger.log(Level.DEBUG, "Starting Class Transformation");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("net.minecraft.client.renderer.ChunkRenderContainer"))
		{
			return patchChunkRenderContainer(basicClass);
		}
		else if (transformedName.equals("net.minecraft.client.renderer.chunk.RenderChunk"))
		{
			return patchRenderChunk(basicClass);
		}

		return basicClass;
	}

	private byte[] patchRenderChunk(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found RenderChunk Class: " + classNode.name);

		MethodNode setPosition = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(MCPNames.method("func_178576_a")))
			{
				setPosition = mn;
				break;
			}
		}

		if (setPosition != null)
		{
			logger.log(Level.DEBUG, "- Found setPosition");

			InsnList toInsert = new InsnList();
			toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
			toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
			toInsert.add(new MethodInsnNode(INVOKESTATIC, asmHandler, "setPosition", "(Lnet/minecraft/client/renderer/chunk/RenderChunk;Lnet/minecraft/util/BlockPos;)V", false));

			setPosition.instructions.insert(toInsert);
			
			logger.log(Level.DEBUG, "- Patched setPosition");
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchChunkRenderContainer(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found ChunkRenderContainer Class: " + classNode.name);

		MethodNode preRenderChunk = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(MCPNames.method("func_178003_a")))
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

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchDummyClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found Dummy Class: " + classNode.name);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
}
