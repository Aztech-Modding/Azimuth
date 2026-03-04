package com.cake.azimuth.goggle.component;

import com.cake.azimuth.Azimuth;
import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.cake.azimuth.goggle.api.IBuildGoggleInformation;
import com.cake.azimuth.goggle.api.IDatagenGoggleInitializer;
import com.cake.azimuth.goggle.builder.GoggleBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class GoggleLangRegistry {

    private static final Map<String, Map<String, String>> ENTRIES_BY_MOD = new ConcurrentHashMap<>();


    public static void provideLang(final String modId, final BiConsumer<String, String> consumer) {
        final Map<String, String> entries = ENTRIES_BY_MOD.get(modId);
        if (entries != null) {
            entries.forEach(consumer);
        }
    }

    public static void registerComponent(final GoggleComponent component) {
        if (component.absoluteTranslationKey() != null || component.defaultEnglish() == null) {
            return;
        }
        register(component.modId(), component.fullTranslationKey(), component.defaultEnglish());
    }

    public static void registerAnonymous(final String modId, final String keySuffix, final String defaultEnglish) {
        register(modId, modId + ".tooltip." + keySuffix, defaultEnglish);
    }

    private static void register(final String modId, final String fullKey, final String defaultEnglish) {
        final Map<String, String> keys = ENTRIES_BY_MOD.computeIfAbsent(modId, unused -> new ConcurrentHashMap<>());
        final String existing = keys.putIfAbsent(fullKey, defaultEnglish);
        if (existing != null && !existing.equals(defaultEnglish) && AzimuthConfigs.tooltipBuilderDebugEnabled()) {
            logConflict(modId, fullKey, defaultEnglish, existing);
        }
    }

    public static Map<String, Map<String, String>> snapshot() {
        final Map<String, Map<String, String>> copy = new LinkedHashMap<>();
        ENTRIES_BY_MOD.forEach((mod, keys) -> copy.put(mod, new LinkedHashMap<>(keys)));
        return Collections.unmodifiableMap(copy);
    }

    public static void collectFromRegisteredBlockEntities() {
        for (final BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            final BlockEntity blockEntity = createForAnyValidState(type);

            if (!(blockEntity instanceof final IBuildGoggleInformation buildable)) {
                continue;
            }

            final GoggleBuilder builder = GoggleBuilder.datagen(buildable.getModId());
            if (blockEntity instanceof final IDatagenGoggleInitializer initializer) {
                initializer.onDatagenInit(builder);
            }

            try {
                buildable.buildGoggleStructure(builder);
            } catch (final Exception ex) {
                Azimuth.LOGGER.warn("Failed to datagen-scan goggle structure for {}", blockEntity.getType(), ex);
            }
        }
    }

    private static BlockEntity createForAnyValidState(final BlockEntityType<?> type) {
        for (final Block block : BuiltInRegistries.BLOCK) {
            final BlockState state = block.defaultBlockState();
            if (!type.isValid(state)) {
                continue;
            }
            try {
                final BlockEntity blockEntity = type.create(Objects.requireNonNull(BlockPos.ZERO), state);
                if (blockEntity != null) {
                    return blockEntity;
                }
            } catch (final Exception ignored) {
            }
        }
        return null;
    }

    private static final Map<String, Long> CONFLICTS_SHOWN = new ConcurrentHashMap<>();

    private static void logConflict(final String modId, final String fullKey, final String defaultEnglish, final String existing) {
        final String conflictLog = String.format("Conflicting goggle lang key %s for mod %s : %s vs %s", fullKey, modId, existing, defaultEnglish);
        if (CONFLICTS_SHOWN.containsKey(conflictLog)) {
            final Long lastShownTime = CONFLICTS_SHOWN.get(conflictLog);
            if (lastShownTime != null && System.currentTimeMillis() - lastShownTime < 30000L) {
                return; // Don't spam the log with the same conflict more than once per 30 seconds
            }
        }

        Azimuth.LOGGER.warn(conflictLog);
        CONFLICTS_SHOWN.put(conflictLog, System.currentTimeMillis());
    }

}