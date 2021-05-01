function initializeCoreMod() {
    return {
        'ChunkRenderDispatcher$ChunkRender#setOrigin': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$ChunkRender',
                'methodName': 'func_189562_a', // setOrigin
                'methodDesc': '(III)V'
            },
            'transformer': function (methodNode) {
                var Opcodes = Java.type("org.objectweb.asm.Opcodes");

                var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
                var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

                var code = methodNode.instructions;
                var instr = code.toArray();

                for (i in instr) {
                    var instruction = instr[i];

                    // Target call to "reset".
                    if (!(instruction instanceof MethodInsnNode) || (instruction.name !== "reset" && instruction.name !== "func_178585_h"))
                        continue;

                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 0));
                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ILOAD, 1));
                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ILOAD, 2));
                    code.insertBefore(instruction, new VarInsnNode(Opcodes.ILOAD, 3));

                    // Invoke AsmHandler#setOrigin with the ChunkRender, x, y, and z.
                    code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/chunkanimator/handler/AsmHandler", "setOrigin", "(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;III)V", false));
                    break;
                }

                return methodNode;
            }
        }
    }
}