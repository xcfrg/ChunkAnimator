function initializeCoreMod() {
    return {
        'coremodone': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.WorldRenderer'
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
                    if (method.name === "renderBlockLayer" || method.name === "func_228441_a_")
                    {
                        var code = method.instructions;
                        var instr = code.toArray();
                        for (t in instr) 
                        {
                            var instruction = instr[t];
                            if (instruction instanceof MethodInsnNode && (instruction.name === "translate" || instruction.name === "func_227861_a_" || instruction.name === "translated" || instruction.name === "func_227670_b_"))
                            {
                                // OptiFine uses GlStateManger.translated instead of MatrixStack.translate
                                var optifine = instruction.name === "translated" || instruction.name === "func_227670_b_";

                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, optifine ? 14 : 12));

                            	if (!optifine) {
                                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                                    code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, "preRenderChunk", "(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;Lcom/mojang/blaze3d/matrix/MatrixStack;)V", false));
                                } else {
                                    code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, "preRenderChunk", "(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;)V", false));
                                }

                            }
                        }
                    }
                }

                return classNode;
            }
        }
    }
}