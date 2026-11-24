package fibbware.coderot;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class DistanceRandom implements IRandomOffsetAlgorithm {

    public static double scale = 1;

    static double mult = 1; // TODO: should be thread local and change based on chunk coords

    public static void Init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal("coderot_distance_scale").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> {
                scale = DoubleArgumentType.getDouble(context, "scale");
                SaveDataFromCommand(context);
                context.getSource().sendSuccess(() -> Component.literal("Set distance scale to %f.".formatted(scale)), false);
                return Command.SINGLE_SUCCESS;
            })));
        });
    }

    public static void InitServer(MinecraftServer server) {
        var savedData = SavedWorldData.GetSavedWorldData(server);
        scale = savedData.distance_scale;
    }

    static void SaveDataFromCommand(CommandContext<CommandSourceStack> context) {
        var savedData = SavedWorldData.GetSavedWorldData(context.getSource().getServer());
        savedData.distance_scale = scale;
        savedData.setDirty();
    }

    @Override
    public double OffsetAdd(double x) {
        return x * mult * scale;
    }

    @Override
    public double OffsetSub(double x) {
        return x * mult * scale;
    }

    @Override
    public double OffsetMul(double x) {
        return x * mult * scale;
    }

    @Override
    public double OffsetDiv(double x) {
        return x * mult * scale;
    }

    @Override
    public double OffsetRem(double x) {
        return x * mult * scale;
    }
}
