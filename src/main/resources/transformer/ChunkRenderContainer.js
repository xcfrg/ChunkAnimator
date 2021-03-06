function initializeCoreMod() {
    return {
        'coremodone': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.AbstractChunkRenderContainer'
            },
            'transformer': function (classNode) {
            	var asmHandler = "lumien/chunkanimator/handler/AsmHandler";
            	
                var Opcodes = Java.type("org.objectweb.asm.Opcodes");

                var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
                var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

                var api = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                var methods = classNode.methods;

                for (m in methods) {
                    var method = methods[m];
                    if (method.name === "preRenderChunk" || method.name === "func_178003_a") {
                        var code = method.instructions;
                        var instr = code.toArray();
                        for (t in instr) {
                            var instruction = instr[t];

                            if (instruction instanceof MethodInsnNode && (instruction.name === "translatef")) {
                            	code.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 1));
                                code.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, "preRenderChunk", "(Lnet/minecraft/client/renderer/chunk/ChunkRender;)V", false));
                            }
                        }
                    }
                }

                return classNode;
            }
        }
    }
}