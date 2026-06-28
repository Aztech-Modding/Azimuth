package com.cake.azimuth.foundation.preconstruct;

import com.cake.azimuth.registration.CreateBlockEdits;
import it.unimi.dsi.fastutil.Pair;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class PreConstructEventHelper {

    private static boolean discoveredListeners = false;

    private static final Map<Class<? extends IPreConstructEvent>, List<Method>> listeners = new HashMap<>();

    private static void discoverListeners() {
        if (!discoveredListeners) {
            discoveredListeners = true;
        }

        for (final ModFileScanData scanData : ModList.get().getAllScanData()) {
            for (final ModFileScanData.AnnotationData annotated : scanData.getAnnotatedBy(
                    AzPreConstructEventListener.class,
                    ElementType.METHOD
            ).toList()) {
                final Pair<Method, Class<? extends IPreConstructEvent>> registrator = resolveRegistratorMethod(annotated);
                listeners.computeIfAbsent(registrator.second(), k -> new ArrayList<>())
                        .add(registrator.first());
            }
        }
    }

    private static Pair<Method, Class<? extends IPreConstructEvent>> resolveRegistratorMethod(final ModFileScanData.AnnotationData annotationData) {
        final Class<?> owner;
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            owner = Class.forName(
                    annotationData.clazz().getClassName(),
                    false,
                    contextClassLoader != null ? contextClassLoader : CreateBlockEdits.class.getClassLoader()
            );
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Failed to load @AzPreConstructEventListener owner " + describe(annotationData) + ".",
                    e
            );
        }

        final List<Method> registrators = Arrays.stream(owner.getDeclaredMethods())
                .filter(method -> annotationData.memberName().startsWith(method.getName())) //Compare ignoring the ()V for params
                .filter(method -> method.isAnnotationPresent(AzPreConstructEventListener.class))
                .toList();
        if (registrators.size() != 1) {
            throw new IllegalStateException("Expected exactly one annotated @AzPreConstructEventListener method for " + describe(
                    annotationData) + ", but found " + registrators.size() + ".");
        }

        return resolveRegistratorMethodFromRegistrators(registrators);
    }

    private static @NotNull Pair<Method, Class<? extends IPreConstructEvent>> resolveRegistratorMethodFromRegistrators(
            final List<Method> registrators) {
        final Method registratorMethod = registrators.getFirst();
        if (!Modifier.isPublic(registratorMethod.getModifiers())
                || !Modifier.isStatic(registratorMethod.getModifiers())
                || registratorMethod.getParameterCount() != 1
                || registratorMethod.getReturnType() != Void.TYPE) {
            throw new IllegalStateException("Invalid @AzPreConstructEventListener " + registratorMethod.toGenericString() + "; expected public static void register() with no arguments.");
        }
        final Class<?> parameterType = registratorMethod.getParameters()[0].getType();
        if (!IPreConstructEvent.class.isAssignableFrom(parameterType)) {
            throw new IllegalStateException("Invalid @AzPreConstructEventListener " + registratorMethod.toGenericString() + "; expected parameter to be assignable from IPreConstructEvent.");
        }

        return Pair.of(registratorMethod, (Class<? extends IPreConstructEvent>) parameterType);
    }

    private static String describe(final ModFileScanData.AnnotationData annotationData) {
        return annotationData.clazz().getClassName() + "#" + annotationData.memberName();
    }


    public static <T extends Event> void post(final T event) {
        discoverListeners();

        final List<Method> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null) {
            return;
        }

        for (final Method listener : eventListeners) {
            try {
                listener.invoke(null, event);
            } catch (final ReflectiveOperationException e) {
                throw new IllegalStateException(
                        "Failed to invoke @AzPreConstructEventListener " + listener.toGenericString() + ".",
                        e
                );
            }
        }
    }

}
