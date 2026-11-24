package fibbware.coderot;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.Random;

public class TimerRandom implements IRandomOffsetAlgorithm {

    static Random rand = new Random();

    static double multAdd = 1;
    static double multSub = 1;
    static double multMul = 1;
    static double multDiv = 1;
    static double multRem = 1;

    // TODO: this is not really thread safe but who cares we're breaking it anyways
    static long prevMillis = System.currentTimeMillis();

    public static double min;
    public static double max;
    public static long period;

    public static void Init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal("coderot_timer_range").then(Commands.argument("min", DoubleArgumentType.doubleArg()).then(Commands.argument("max", DoubleArgumentType.doubleArg()).executes(context -> {
                min = DoubleArgumentType.getDouble(context, "min");
                max = DoubleArgumentType.getDouble(context, "max");
                SaveDataFromCommand(context);
                context.getSource().sendSuccess(() -> Component.literal("Set random range to %f to %f.".formatted(min, max)), false);
                return Command.SINGLE_SUCCESS;
            }))));

            dispatcher.register(Commands.literal("coderot_timer_period").then(Commands.argument("period_ms", LongArgumentType.longArg()).executes(context -> {
                period = LongArgumentType.getLong(context, "period_ms");
                SaveDataFromCommand(context);
                context.getSource().sendSuccess(() -> Component.literal("Set random regen period to %d ms.".formatted(period)), false);
                return Command.SINGLE_SUCCESS;
            })));
        });
    }

    public static void InitServer(MinecraftServer server) {
        var savedData = SavedWorldData.GetSavedWorldData(server);
        min = savedData.timer_min;
        max = savedData.timer_max;
        period = savedData.timer_period;
    }

    static void SaveDataFromCommand(CommandContext<CommandSourceStack> context) {
        var savedData = SavedWorldData.GetSavedWorldData(context.getSource().getServer());
        savedData.timer_min = min;
        savedData.timer_max = max;
        savedData.timer_period = period;
        savedData.setDirty();
    }

    static void CheckIfNeedsToChangeOffsets() {
        long millis = System.currentTimeMillis();
        if (millis - prevMillis > period) {
            prevMillis = millis;

            multAdd = min + (max - min) * rand.nextDouble();
            multSub = min + (max - min) * rand.nextDouble();
            multMul = min + (max - min) * rand.nextDouble();
            multDiv = min + (max - min) * rand.nextDouble();
            multRem = min + (max - min) * rand.nextDouble();

            Coderot.LOGGER.info("new random multAdd: {}", multAdd);
            Coderot.LOGGER.info("new random multSub: {}", multSub);
            Coderot.LOGGER.info("new random multDiv: {}", multMul);
            Coderot.LOGGER.info("new random multMul: {}", multDiv);
            Coderot.LOGGER.info("new random multRem: {}", multRem);
        }
    }

    @Override
    public double OffsetAdd(double x) {
        CheckIfNeedsToChangeOffsets();
        return x * multAdd;
    }

    @Override
    public double OffsetSub(double x) {
        CheckIfNeedsToChangeOffsets();
        return x * multSub;
    }

    @Override
    public double OffsetMul(double x) {
        CheckIfNeedsToChangeOffsets();
        return x * multMul;
    }

    @Override
    public double OffsetDiv(double x) {
        CheckIfNeedsToChangeOffsets();
        return x * multDiv;
    }

    @Override
    public double OffsetRem(double x) {
        CheckIfNeedsToChangeOffsets();
        return x * multRem;
    }
}
