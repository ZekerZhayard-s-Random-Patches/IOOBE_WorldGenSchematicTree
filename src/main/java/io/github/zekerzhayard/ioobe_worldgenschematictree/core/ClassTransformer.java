package io.github.zekerzhayard.ioobe_worldgenschematictree.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("com.matez.wildnature.worldgen.WorldGenSchematicTree")) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, ClassReader.EXPAND_FRAMES);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "generatePlatform") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/world/World;)V")) {
                    boolean found = false;
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            MethodInsnNode min = (MethodInsnNode) ain;
                            if (RemapUtils.checkClassName(min.owner, "com/matez/wildnature/worldgen/WorldGenSchematicTree") && RemapUtils.checkMethodName(min.owner, min.name, min.desc, "func_175903_a") && RemapUtils.checkMethodDesc(min.desc, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V")) {
                                found = true;
                            }
                        }
                        if (found && ain.getOpcode() == Opcodes.ICONST_0) {
                            mn.instructions.set(ain, new InsnNode(Opcodes.ICONST_M1));
                        }
                    }
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
