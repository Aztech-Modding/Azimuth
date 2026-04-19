package com.cake.azimuth.lang;

import com.cake.azimuth.Azimuth;
import com.cake.azimuth.foundation.lang.AzimuthGeneratedLangEntry;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Scans {@link IncludeLangDefaults} annotations via NeoForge mod scan data
 * and registers discovered entries into {@link AzimuthGeneratedLangEntry} for datagen.
 */
public class LangDefaultCollector {

    public static void collectAll() {
        final ModList modList = ModList.get();
        if (modList == null) {
            return;
        }

        final List<ModFileScanData.AnnotationData> discovered = new ArrayList<>();
        for (final ModFileScanData scanData : modList.getAllScanData()) {
            scanData.getAnnotatedBy(IncludeLangDefaults.class, ElementType.TYPE)
                    .forEach(discovered::add);
            scanData.getAnnotatedBy(IncludeLangDefaults.class, ElementType.METHOD)
                    .forEach(discovered::add);
        }

        discovered.sort(Comparator.comparing((ModFileScanData.AnnotationData d) -> d.clazz().getClassName())
                                .thenComparing(ModFileScanData.AnnotationData::memberName));

        discovered.forEach(LangDefaultCollector::processAnnotation);
    }

    private static void processAnnotation(final ModFileScanData.AnnotationData data) {
        final Class<?> ownerClass = loadClass(data.clazz().getClassName());
        if (ownerClass == null) {
            return;
        }

        final List<IncludeLangDefaults> annotations = findAnnotations(ownerClass, data);
        for (final IncludeLangDefaults annotation : annotations) {
            final String modId = resolveModId(annotation, ownerClass);
            if (modId == null) {
                Azimuth.LOGGER.warn(
                        "Could not determine mod ID for @IncludeLangDefaults on {}#{} — "
                                + "set modid explicitly or ensure a @Mod class shares the package tree.",
                        data.clazz().getClassName(), data.memberName()
                );
                continue;
            }

            for (final LangDefault entry : annotation.value()) {
                final String resolvedKey = resolveKey(entry);
                if (resolvedKey == null) {
                    Azimuth.LOGGER.warn(
                            "Failed to resolve lang key '{}' in @IncludeLangDefaults on {}#{}.",
                            entry.key(), data.clazz().getClassName(), data.memberName()
                    );
                    continue;
                }
                AzimuthGeneratedLangEntry.registerEntry(modId, resolvedKey, entry.value());
            }
        }
    }

    private static List<IncludeLangDefaults> findAnnotations(final Class<?> ownerClass,
                                                             final ModFileScanData.AnnotationData data) {
        final List<IncludeLangDefaults> results = new ArrayList<>();

        if (data.targetType() == ElementType.TYPE) {
            final IncludeLangDefaults annotation = ownerClass.getAnnotation(IncludeLangDefaults.class);
            if (annotation != null) {
                results.add(annotation);
            }
        } else {
            for (final Method method : ownerClass.getDeclaredMethods()) {
                if (!method.getName().equals(data.memberName())) {
                    continue;
                }
                final IncludeLangDefaults annotation = method.getAnnotation(IncludeLangDefaults.class);
                if (annotation != null) {
                    results.add(annotation);
                    break;
                }
            }
        }

        return results;
    }

    private static String resolveModId(final IncludeLangDefaults annotation, final Class<?> ownerClass) {
        if (!annotation.modid().isEmpty()) {
            return annotation.modid();
        }
        return detectModIdFromPackage(ownerClass);
    }

    private static String resolveKey(final LangDefault entry) {
        if (!entry.format().isEmpty()) {
            return String.format(entry.format(), entry.key());
        }

        return entry.key();
    }

    private static String detectModIdFromPackage(final Class<?> clazz) {
        final String classPackage = clazz.getPackageName();
        String bestModId = null;
        int bestPrefixLength = 0;

        final ModList modList = ModList.get();
        if (modList == null) {
            return null;
        }

        final List<ModFileScanData.AnnotationData> modAnnotations = modList.getAllScanData().stream()
                .flatMap(sd -> sd.getAnnotatedBy(Mod.class, ElementType.TYPE))
                .toList();

        for (final ModFileScanData.AnnotationData modAnnotation : modAnnotations) {
            final Object modIdValue = modAnnotation.annotationData().get("value");
            if (!(modIdValue instanceof final String candidateModId)) {
                continue;
            }

            final String modClassName = modAnnotation.clazz().getClassName();
            final int lastDot = modClassName.lastIndexOf('.');
            if (lastDot < 0) {
                continue;
            }
            final String modPackage = modClassName.substring(0, lastDot);

            if (classPackage.startsWith(modPackage) && modPackage.length() > bestPrefixLength) {
                bestPrefixLength = modPackage.length();
                bestModId = candidateModId;
            }
        }

        return bestModId;
    }

    private static Class<?> loadClass(final String className) {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            return Class.forName(
                    className, false,
                    contextClassLoader != null ? contextClassLoader : LangDefaultCollector.class.getClassLoader()
            );
        } catch (final ClassNotFoundException e) {
            Azimuth.LOGGER.warn("Failed to load class {} for @IncludeLangDefaults processing.", className, e);
            return null;
        }
    }
}
