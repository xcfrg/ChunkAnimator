package lumien.chunkanimator.asm;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

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

		MethodNode setOrigin = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(MCPNames.method("func_189562_a")))
			{
				setOrigin = mn;
				break;
			}
		}

		if (setOrigin != null)
		{
			logger.log(Level.DEBUG, "- Found setOrigin");

			for (int i = 0; i < setOrigin.instructions.size(); i++)
			{
				AbstractInsnNode ain;

				if ((ain = setOrigin.instructions.get(i)) instanceof MethodInsnNode)
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

						setOrigin.instructions.insertBefore(min, toInsert);
						i+=5;
					}
				}
			}

			logger.log(Level.DEBUG, "- Patched setOrigin");
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
