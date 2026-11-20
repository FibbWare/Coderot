package fibbware.coderot;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coderot implements ModInitializer {
	public static final String MOD_ID = "coderot";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Coderot initialize test!");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			// coderot_range min max
			dispatcher.register(Commands.literal("coderot_range")
				.then(Commands.argument("min", DoubleArgumentType.doubleArg())
					.then(Commands.argument("max", DoubleArgumentType.doubleArg())
						.executes(context -> {
							double min = DoubleArgumentType.getDouble(context, "min");
							double max = DoubleArgumentType.getDouble(context, "max");
							Rounder.SetRange(min, max);
							context.getSource().sendSuccess(() -> Component.literal("Set random range to %f to %f.".formatted(min, max)), false);
							return Command.SINGLE_SUCCESS;
			}))));

			// coderot_period value
			dispatcher.register(Commands.literal("coderot_period")
				.then(Commands.argument("period_ms", LongArgumentType.longArg())
					.executes(context -> {
						long val = LongArgumentType.getLong(context, "period_ms");
						Rounder.SetPeriod(val);
						context.getSource().sendSuccess(() -> Component.literal("Set random rengen period to %d ms.".formatted(val)), false);
						return Command.SINGLE_SUCCESS;
			})));

			// coderot_preset
			dispatcher.register(Commands.literal("coderot_preset")
				.executes(context -> {
					double min = 0.9999;
					double max = 1.0001;
					long period = 30000L;
					Rounder.SetRange(min, max);
					Rounder.SetPeriod(period);
					context.getSource().sendSuccess(() -> Component.literal("Set a decent preset for survival (%f - %f, %d ms).".formatted(min, max, period)), false);
					return Command.SINGLE_SUCCESS;
			}));

			// coderot_disable
			dispatcher.register(Commands.literal("coderot_disable")
					.executes(context -> {
						long period = Long.MAX_VALUE;
						Rounder.SetPeriod(period);
						context.getSource().sendSuccess(() -> Component.literal("Disabled coderot."), false);
						return Command.SINGLE_SUCCESS;
					}));
		});
	}
}
