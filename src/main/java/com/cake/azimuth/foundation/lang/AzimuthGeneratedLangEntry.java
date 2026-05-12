package com.cake.azimuth.foundation.lang;

import com.cake.azimuth.Azimuth;
import com.cake.azimuth.foundation.config.AzimuthConfigs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class AzimuthGeneratedLangEntry {

    private static final Map<String, Map<String, String>> ENTRIES_BY_MOD = new ConcurrentHashMap<>();

    public static void provideLang(final String modId, final BiConsumer<String, String> consumer) {
        final Map<String, String> entries = ENTRIES_BY_MOD.get(modId);
        if (entries != null) {
            entries.forEach(consumer);
        }
    }

    public static void registerEntry(final String modId, final String fullKey, final String defaultEnglish) {
        register(modId, fullKey, defaultEnglish);
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

    private static final Map<String, Long> CONFLICTS_SHOWN = new ConcurrentHashMap<>();

    private static void logConflict(final String modId,
                                    final String fullKey,
                                    final String defaultEnglish,
                                    final String existing) {
        final String conflictLog = String.format(
                "Conflicting goggle lang key %s for mod %s : %s vs %s",
                fullKey,
                modId,
                existing,
                defaultEnglish
        );
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