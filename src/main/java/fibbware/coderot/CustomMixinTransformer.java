package fibbware.coderot;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;

import java.util.List;

public class CustomMixinTransformer implements IMixinTransformer{

    IMixinTransformer base;

    public CustomMixinTransformer(IMixinTransformer base) {
        this.base = base;
    }

    @Override public void audit(MixinEnvironment environment) { base.audit(environment); }
    @Override public List<String> reload(String mixinClass, ClassNode classNode) { return base.reload(mixinClass, classNode); }
    @Override public boolean computeFramesForClass(MixinEnvironment environment, String name, ClassNode classNode) { return base.computeFramesForClass(environment, name, classNode); }
    @Override public byte[] transformClass(MixinEnvironment environment, String name, byte[] classBytes) { return base.transformClass(environment, name, classBytes); }
    @Override public boolean transformClass(MixinEnvironment environment, String name, ClassNode classNode) { return base.transformClass(environment, name, classNode); };
    @Override public boolean couldTransformClass(MixinEnvironment environment, String name) { return base.couldTransformClass(environment, name); }
    @Override public byte[] generateClass(MixinEnvironment environment, String name) { return base.generateClass(environment, name); }
    @Override public boolean generateClass(MixinEnvironment environment, String name, ClassNode classNode) { return base.generateClass(environment, name, classNode); }
    @Override public IExtensionRegistry getExtensions() { return base.getExtensions(); }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        byte[] bytes = base.transformClassBytes(name, transformedName, basicClass);

        if (!transformedName.startsWith("net.minecraft.world.level.levelgen")) {
            return bytes;
        }

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        for (MethodNode method : node.methods) {
            String fullName = node.name + "/" + method.name;

            List<String> blacklist = List.of(
                "net/minecraft/world/level/levelgen/material/MaterialSystem/getBand",
                "net/minecraft/world/level/levelgen/structure/structures/MineshaftPieces$MineShaftCorridor/createChest",
                "net/minecraft/world/level/levelgen/feature/trunkplacers/FancyTrunkPlacer/makeLimb",
                "net/minecraft/world/level/levelgen/feature/trunkplacers/FancyTrunkPlacer/placeTrunk",
                "net/minecraft/world/level/levelgen/feature/stateproviders/NoiseProvider/getRandomState",
                "net/minecraft/world/level/levelgen/structure/structures/RuinedPortalStructure/findGenerationPoint",
                "net/minecraft/world/level/levelgen/structure/Structure/findValidGenerationPoint",
                "net/minecraft/world/level/levelgen/feature/OreFeature/place",
                "net/minecraft/world/level/levelgen/structure/structures/SwampHutPiece/postProcess"
            );

            if (blacklist.contains(fullName)) {
                System.out.println("[coderot] skipping: " + fullName);
                continue;
            }

            for (AbstractInsnNode insn : method.instructions.toArray()) {

                String methodName = null;
                switch (insn.getOpcode()) {
                    case Opcodes.FADD -> methodName = "fadd";
                    case Opcodes.FSUB -> methodName = "fsub";
                    case Opcodes.FMUL -> methodName = "fmul";
                    case Opcodes.FDIV -> methodName = "fdiv";
                    case Opcodes.FREM -> methodName = "frem";

                    case Opcodes.DADD -> methodName = "dadd";
                    case Opcodes.DSUB -> methodName = "dsub";
                    case Opcodes.DMUL -> methodName = "dmul";
                    case Opcodes.DDIV -> methodName = "ddiv";
                    case Opcodes.DREM -> methodName = "drem";
                }

                if (methodName == null)
                    continue;

                String methodDescriptor = methodName.startsWith("d") ? "(DD)D" : "(FF)F";
                method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "fibbware/coderot/Rounder", methodName, methodDescriptor, false));

                // System.out.println("[coderot] corrupting: " + fullName);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
