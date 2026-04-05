package com.cake.azimuth.goggle.command;

import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class AzimuthClientCommands {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("azimuth")
                .requires(source -> source.hasPermission(0))
                .then(Commands.literal("tooltip_debug")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> {
                                    final boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                    AzimuthConfigs.client().tooltipBuilderDebug.set(enabled);
                                    context.getSource().sendSuccess(() -> Component.translatable(
                                            "azimuth.command.tooltip_debug.set",
                                            enabled
                                    ), false);
                                    return 1;
                                })));

        dispatcher.register(root);
    }
}
