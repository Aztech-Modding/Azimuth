package com.cake.azimuth.registration;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

/**
 * Registry for block edits to be applied to Create blocks during registration.
 * Registrators are discovered and invoked via NeoForge scan data during Create's AllBlocks static initialization.
 */
public class CreateBlockEdits {

    private static final Map<String, Consumer<BlockBuilder<?, CreateRegistrate>>> EDITS_BY_ID = new LinkedHashMap<>();
    private static final Map<String, NonNullBiFunction<? extends Block, Item.Properties, ? extends BlockItem>> ITEM_OVERRIDES = new LinkedHashMap<>();
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

    public static synchronized <T extends Block> void forBlockItem(final String id, final NonNullBiFunction<T, Item.Properties, ? extends BlockItem> itemFactory) {
        if (registrationWindow != RegistrationWindow.OPEN) {
            throw new IllegalStateException("CreateBlockEdits.forBlockItem(...) can only be called from a @CreateBlockEdits.Registrator method while Create's AllBlocks are bootstrapping; current registration window is " + registrationWindow + ".");
        }

        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(itemFactory, "itemFactory");
        if (ITEM_OVERRIDES.containsKey(id)) {
            throw new IllegalStateException("An item override for block '" + id + "' has already been registered.");
        }
        ITEM_OVERRIDES.put(id, itemFactory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> NonNullBiFunction<T, Item.Properties, ? extends BlockItem> getItemOverride(final String id) {
        return (NonNullBiFunction<T, Item.Properties, ? extends BlockItem>) ITEM_OVERRIDES.get(id);
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

        final String methodSimpleName = annotationData.memberName().contains("(")
                ? annotationData.memberName().substring(0, annotationData.memberName().indexOf('('))
                : annotationData.memberName();

        final List<Method> registrators = Arrays.stream(owner.getDeclaredMethods())
                .filter(method -> annotationData.memberName().startsWith(method.getName())) //Compare ignoring the ()V for params
                .filter(method -> method.isAnnotationPresent(Registrator.class))
                .toList();
        if (registrators.size() != 1) {
            throw new IllegalStateException("Expected exactly one annotated @CreateBlockEdits.Registrator method for " + describe(annotationData) + ", but found " + registrators.size() + ".");
        }

        return resolveRegistratorMethodFromRegistrators(registrators);
    }

    private static @NotNull Method resolveRegistratorMethodFromRegistrators(final List<Method> registrators) {
        final Method registratorMethod = registrators.getFirst();
        if (!Modifier.isPublic(registratorMethod.getModifiers())
                || !Modifier.isStatic(registratorMethod.getModifiers())
                || registratorMethod.getParameterCount() != 0
                || registratorMethod.getReturnType() != Void.TYPE) {
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
