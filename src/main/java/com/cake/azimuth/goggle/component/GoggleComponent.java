package com.cake.azimuth.goggle.component;

import org.jetbrains.annotations.Nullable;

/**
 * A reusable tooltip component reference.
 *
 * @param modId                  owning mod id for generated keys
 * @param keySuffix              key suffix under tooltip namespace
 * @param defaultEnglish         optional default english string for datagen
 * @param absoluteTranslationKey optional fully-qualified translation key override
 */
public record GoggleComponent(
        String modId,
        String keySuffix,
        @Nullable String defaultEnglish,
        @Nullable String absoluteTranslationKey
) {

    public static GoggleComponent scoped(final String modId, final String keySuffix, @Nullable final String defaultEnglish) {
        return new GoggleComponent(modId, keySuffix, defaultEnglish, null);
    }

    public static GoggleComponent absolute(final String absoluteTranslationKey) {
        return new GoggleComponent("", "", null, absoluteTranslationKey);
    }

    public String fullTranslationKey() {
        if (absoluteTranslationKey != null) {
            return absoluteTranslationKey;
        }
        return modId + ".tooltip." + keySuffix;
    }
}