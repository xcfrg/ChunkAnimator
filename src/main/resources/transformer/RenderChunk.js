function initializeCoreMod() {
    return {
        'coremodone': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.chunk.RenderChunk'
            },
            'transformer': function (classNode) {
            	var asmHandler = "lumien/chunkanimator/handler/AsmHandler";
            	
                var Opcodes = Java.type("org.objectweb.asm.Opcodes");

                var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
                var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

                var api = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                var methods = classNode.methods;

                for (m in methods) 
                {
                    var method = methods[m];
                    if (method.name === "setPosition" || method.name === "func_189562_a") 
                    {
                        var code = method.instructions;
                        var instr = code.toArray();
                        for (t in instr) 
                        {
                            var instruction = instr[t];
                            if (instruction instanceof MethodInsnNode && (instruction.name === "stopCompileTask" || instruction.name === "func_178585_h")) 
                            {
                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 0));
                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ILOAD, 1));
                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ILOAD, 2));
                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ILOAD, 3));
                            	code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, "setOrigin", "(Lnet/minecraft/client/renderer/chunk/RenderChunk;III)V", false));
        						break;
                            }
                        }
                        break;
                    }
                }

                return classNode;
            }
        }
    }
}