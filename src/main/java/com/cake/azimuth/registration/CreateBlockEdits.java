package com.cake.azimuth.registration;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Registry for block edits to be applied to Create blocks during registration.
 * Registrators are discovered and invoked via NeoForge scan data during Create's AllBlocks static initialization.
 */
public class CreateBlockEdits {

    private static final Map<String, Consumer<BlockBuilder<?, CreateRegistrate>>> EDITS_BY_ID = new LinkedHashMap<>();
    private static RegistrationWindow registrationWindow = RegistrationWindow.NOT_STARTED;

    public static synchronized void bootstrapRegistrators() {
        if (registrationWindow != RegistrationWindow.NOT_STARTED) {
            return;
        }

        final ModList modList = ModList.get();
        if (modList == null) {
            throw new IllegalStateException("Cannot discover @CreateBlockEdits.Registrator methods before NeoForge ModList is available.");
        }

        registrationWindow = RegistrationWindow.OPEN;
        try {
            discoverRegistrators(modList).forEach(CreateBlockEdits::invokeRegistrator);
        } finally {
            registrationWindow = RegistrationWindow.CLOSED;
        }
    }

    public static synchronized void forBlock(final String id, final Consumer<BlockBuilder<?, CreateRegistrate>> edit) {
        if (registrationWindow != RegistrationWindow.OPEN) {
            throw new IllegalStateException("CreateBlockEdits.forBlock(...) can only be called from a @CreateBlockEdits.Registrator method while Create's AllBlocks are bootstrapping; current registration window is " + registrationWindow + ".");
        }

        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(edit, "edit");
        EDITS_BY_ID.merge(id, edit, (existing, additional) -> builder -> {
            existing.accept(builder);
            additional.accept(builder);
        });
    }

    public static Consumer<BlockBuilder<?, CreateRegistrate>> getEditForId(final String id) {
        if (registrationWindow != RegistrationWindow.CLOSED) {
            throw new IllegalStateException("CreateBlockEdits.getEditForId(...) was called before registrators were fully discovered; current registration window is " + registrationWindow + ".");
        }

        return EDITS_BY_ID.get(id);
    }

    private static List<ModFileScanData.AnnotationData> discoverRegistrators(final ModList modList) {
        return modList.getAllScanData().stream()
                .flatMap(scanData -> scanData.getAnnotatedBy(Registrator.class, ElementType.METHOD))
                .sorted(Comparator.comparing((ModFileScanData.AnnotationData data) -> data.clazz().getClassName())
                        .thenComparing(ModFileScanData.AnnotationData::memberName))
                .toList();
    }

    private static void invokeRegistrator(final ModFileScanData.AnnotationData annotationData) {
        final Method registratorMethod = resolveRegistratorMethod(annotationData);
        try {
            registratorMethod.invoke(null);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to invoke @CreateBlockEdits.Registrator method " + describe(annotationData) + ".", e);
        }
    }

    private static Method resolveRegistratorMethod(final ModFileScanData.AnnotationData annotationData) {
        final Class<?> owner;
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            owner = Class.forName(annotationData.clazz().getClassName(), false, contextClassLoader != null ? contextClassLoader : CreateBlockEdits.class.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load @CreateBlockEdits.Registrator owner " + describe(annotationData) + ".", e);
        }

        final List<Method> registrators = Arrays.stream(owner.getDeclaredMethods())
                .filter(method -> method.getName().equals(annotationData.memberName()))
                .filter(method -> method.isAnnotationPresent(Registrator.class))
                .toList();
        if (registrators.size() != 1) {
            throw new IllegalStateException("Expected exactly one annotated @CreateBlockEdits.Registrator method for " + describe(annotationData) + ", but found " + registrators.size() + ".");
        }

        final Method registratorMethod = registrators.get(0);
        if (!Modifier.isPublic(registratorMethod.getModifiers())
                || !Modifier.isStatic(registratorMethod.getModifiers())
                || registratorMethod.getParameterCount() != 0
                || registratorMethod.getReturnType() != Void.TYPE
                || !registratorMethod.getName().equals("register")) {
            throw new IllegalStateException("Invalid @CreateBlockEdits.Registrator method " + registratorMethod.toGenericString() + "; expected public static void register() with no arguments.");
        }

        return registratorMethod;
    }

    private static String describe(final ModFileScanData.AnnotationData annotationData) {
        return annotationData.clazz().getClassName() + "#" + annotationData.memberName();
    }

    /**
     * Marks a public static void register() method with no arguments to be invoked while Create's AllBlocks are bootstrapping.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Registrator {
    }

    private enum RegistrationWindow {
        NOT_STARTED,
        OPEN,
        CLOSED
    }

}
