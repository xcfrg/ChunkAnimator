function initializeCoreMod() {
    return {
        'WorldRenderer#renderChunkLayer': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.WorldRenderer',
                'methodName': 'func_228441_a_', // renderChunkLayer
                'methodDesc': '(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/matrix/MatrixStack;DDD)V'
            },
            'transformer': function (methodNode) {
                var Opcodes = Java.type("org.objectweb.asm.Opcodes");

                var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
                var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
                var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode")
                var TypeInsnNode = Java.type("org.objectweb.asm.tree.TypeInsnNode")

                var matrixStack = "com/mojang/blaze3d/matrix/MatrixStack";

                var code = methodNode.instructions;
                var instr = code.toArray();

                for (i in instr) {
                    var instruction = instr[i];

                    if (!(instruction instanceof MethodInsnNode))
                        continue;

                    // This will be true if OptiFine is loaded, and the current instruction is GlStateManager.translated (OptiFine uses this instead of MatrixStack.translate).
                    var optifine = instruction.name === "_translated" || instruction.name === "func_227670_b_";

                    if (!optifine && instruction.name !== "translate" && instruction.name !== "func_227861_a_")
                        continue;

                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, optifine ? 14 : 12));

                    // If using OptiFine make MatrixStack parameter null (and cast to MatrixStack), otherwise load the MatrixStack parameter.
                    if (optifine) {
                        code.insertBefore(instruction, new InsnNode(Opcodes.ACONST_NULL));
                        code.insertBefore(instruction, new TypeInsnNode(Opcodes.CHECKCAST, matrixStack));
                    } else {
                        code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                    }

                    // Invoke AsmHandler#preRenderChunk with the ChunkRender and the MatrixStack (or null if using OptiFine).
                    code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/chunkanimator/handler/AsmHandler", "preRenderChunk",
                        "(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;L" + matrixStack + ";)V", false));
                    break;
                }

                return methodNode;
            }
        }
    }
}