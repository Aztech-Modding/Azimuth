package com.cake.azimuth.foundation.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@EventBusSubscriber
public class AzimuthConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static AzimuthClientConfig client;
    private static AzimuthCommonConfig common;
    private static AzimuthServerConfig server;

    public static AzimuthClientConfig client() {
        return client;
    }

    public static AzimuthCommonConfig common() {
        return common;
    }

    public static AzimuthServerConfig server() {
        return server;
    }

    public static ConfigBase byType(final ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    public static boolean tooltipBuilderDebugEnabled() {
        if (client() == null) {
            return false;
        }
        return client().tooltipBuilderDebug.get();
    }

    private static <T extends ConfigBase> T register(final Supplier<T> factory, final ModConfig.Type side) {
        final Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            final T config = factory.get();
            config.registerAll(Objects.requireNonNull(builder));
            return config;
        });

        final T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(final ModLoadingContext context, final ModContainer container) {
        client = register(AzimuthClientConfig::new, ModConfig.Type.CLIENT);
        common = register(AzimuthCommonConfig::new, ModConfig.Type.COMMON);
        server = register(AzimuthServerConfig::new, ModConfig.Type.SERVER);

        for (final Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
            container.registerConfig(pair.getKey(), pair.getValue().specification);
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        for (final ConfigBase config : CONFIGS.values()) {
            if (config.specification == event.getConfig().getSpec()) {
                config.onLoad();
            }
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        for (final ConfigBase config : CONFIGS.values()) {
            if (config.specification == event.getConfig().getSpec()) {
                config.onReload();
            }
        }
    }
}
