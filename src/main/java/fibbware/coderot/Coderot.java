package fibbware.coderot;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coderot implements ModInitializer {
    public static final String MOD_ID = "coderot";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MathModifier.stupid_unused_warning_hack();

        TimerRandom.Init();
        DistanceRandom.Init();

        LOGGER.info("Coderot initialized!");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SavedWorldData savedData = SavedWorldData.GetSavedWorldData(server);

            TimerRandom.InitServer(server);
            DistanceRandom.InitServer(server);

            switch (savedData.corruptorMode) {
                case "timer":
                    MathModifier.randomOffsetAlgoritm = new TimerRandom();
                    break;

                case "distance":
                    MathModifier.randomOffsetAlgoritm = new DistanceRandom();
                    break;

                case "none":
                    MathModifier.randomOffsetAlgoritm = null;
                    break;

                default:
                    LOGGER.warn("Unknown saved corruptor mode: {}", savedData.corruptorMode);
                    break;
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            MathModifier.randomOffsetAlgoritm = null;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal("coderot_get_mode").executes(context -> {
                MinecraftServer server = context.getSource().getServer();
                String modeString = SavedWorldData.GetSavedWorldData(server).corruptorMode;
                context.getSource().sendSuccess(() -> Component.literal("Mode set to: %s.".formatted(modeString)), false);

                if (MathModifier.randomOffsetAlgoritm instanceof TimerRandom) {
                    context.getSource().sendSuccess(() -> Component.literal("Range: %f - %f.".formatted(TimerRandom.min, TimerRandom.max)), false);
                    context.getSource().sendSuccess(() -> Component.literal("Period: %d.".formatted(TimerRandom.period)), false);
                } else if (MathModifier.randomOffsetAlgoritm instanceof DistanceRandom) {
                    context.getSource().sendSuccess(() -> Component.literal("Scale: %f.".formatted(DistanceRandom.scale)), false);
                }

                return Command.SINGLE_SUCCESS;
            }));

            dispatcher.register(Commands.literal("coderot_disable").executes(context -> {
                ModeSelectHelper(context, "none");
                context.getSource().sendSuccess(() -> Component.literal("Disabled coderot."), false);
                return Command.SINGLE_SUCCESS;
            }));

            dispatcher.register(Commands.literal("coderot_set_mode_timer").executes(context -> {
                ModeSelectHelper(context, "timer");
                context.getSource().sendSuccess(() -> Component.literal("Set coderot mode to timer."), false);
                return Command.SINGLE_SUCCESS;
            }));

            dispatcher.register(Commands.literal("coderot_set_mode_distance").executes(context -> {
                ModeSelectHelper(context, "distance");
                context.getSource().sendSuccess(() -> Component.literal("Set coderot mode to distance."), false);
                return Command.SINGLE_SUCCESS;
            }));
        });
    }

    static void ModeSelectHelper(CommandContext<CommandSourceStack> context, String name) {
        MinecraftServer server = context.getSource().getServer();

        IRandomOffsetAlgorithm algorithm;

        switch (name) {
            case "none":
                algorithm = null;
                break;

            case "timer":
                algorithm = new TimerRandom();
                break;

            case "distance":
                algorithm = new DistanceRandom();
                break;

            default:
                LOGGER.error("Unknown mode: {}", name);
                return;
        }

        MathModifier.randomOffsetAlgoritm = algorithm;
        var savedData = SavedWorldData.GetSavedWorldData(server);
        savedData.corruptorMode = name;
        savedData.setDirty();
    }
}
