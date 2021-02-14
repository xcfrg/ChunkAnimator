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

                var translatedMethod = "translated";
                var translatedMethodObf = "func_227670_b_";
                var preRenderChunkMethod = "preRenderChunk";

                var chunkRenderParam = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender";
                var matrixStackParam = "Lcom/mojang/blaze3d/matrix/MatrixStack";

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
                            if (instruction instanceof MethodInsnNode && (instruction.name === "translate" || instruction.name === "func_227861_a_" || instruction.name === translatedMethod || instruction.name === translatedMethodObf))
                            {
                                // OptiFine uses GlStateManger.translated instead of MatrixStack.translate
                                var optifine = instruction.name === translatedMethod || instruction.name === translatedMethodObf;

                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, optifine ? 14 : 12));

                            	if (!optifine) {
                                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                                }

                                code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, preRenderChunkMethod, "(" + chunkRenderParam + (!optifine ? ";" + matrixStackParam : "") + ";)V", false));
                            }
                        }
                    }
                }

                return classNode;
            }
        }
    }
}