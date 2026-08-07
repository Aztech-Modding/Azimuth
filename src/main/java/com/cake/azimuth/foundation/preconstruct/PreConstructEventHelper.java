package com.cake.azimuth.foundation.preconstruct;

import com.cake.azimuth.registration.CreateBlockEdits;
import it.unimi.dsi.fastutil.Pair;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreConstructEventHelper {

    private static boolean discoveredListeners = false;

    private static final Map<Class<? extends IPreConstructEvent>, List<MethodHandle>> listeners = new HashMap<>();

    private static void discoverListeners() {
        if (discoveredListeners) {
            return;
        }
        discoveredListeners = true;

        for (final ModFileScanData scanData : ModList.get().getAllScanData()) {
            for (final ModFileScanData.AnnotationData annotated : scanData.getAnnotatedBy(
                    AzPreConstructEventListener.class,
                    ElementType.METHOD
            ).toList()) {
                final Pair<MethodHandle, Class<? extends IPreConstructEvent>> registrator = resolveRegistratorMethod(annotated);
                listeners.computeIfAbsent(registrator.second(), k -> new ArrayList<>())
                        .add(registrator.first());
            }
        }
    }

    private static Pair<MethodHandle, Class<? extends IPreConstructEvent>> resolveRegistratorMethod(final ModFileScanData.AnnotationData annotationData) {
        // memberName is in the form "methodName(Ldescriptor/of/the/Event;)V". We resolve ONLY that
        // exact method. Deliberately avoid Class#getDeclaredMethods(): it resolves the signature
        // types of EVERY declared method, including unrelated synthetic lambdas that may reference
        // client-only classes (e.g. BakedModel) which do not exist on the dedicated server.
        final String memberName = annotationData.memberName();
        final int descriptorStart = memberName.indexOf('(');
        if (descriptorStart <= 0) {
            throw new IllegalStateException(
                    "Malformed @AzPreConstructEventListener member name '" + memberName + "' on "
                            + annotationData.clazz().getClassName() + "; expected 'name(descriptor)return'.");
        }
        final String methodName = memberName.substring(0, descriptorStart);
        final String descriptor = memberName.substring(descriptorStart);

        if (!descriptor.endsWith(")V")) {
            throw new IllegalStateException("Invalid @AzPreConstructEventListener " + describe(
                    annotationData) + "; expected void return type.");
        }

        final List<Class<?>> parameterTypes = parseParameterTypes(descriptor, annotationData);
        if (parameterTypes.size() != 1) {
            throw new IllegalStateException("Invalid @AzPreConstructEventListener " + describe(
                    annotationData) + "; expected exactly one parameter but found " + parameterTypes.size() + ".");
        }
        final Class<?> parameterType = parameterTypes.getFirst();
        if (!IPreConstructEvent.class.isAssignableFrom(parameterType)) {
            throw new IllegalStateException("Invalid @AzPreConstructEventListener " + describe(
                    annotationData) + "; expected parameter to be assignable from IPreConstructEvent.");
        }

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

        final MethodHandle registrator;
        try {
            registrator = MethodHandles.publicLookup().findStatic(
                    owner, methodName, MethodType.methodType(void.class, parameterType));
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException("Invalid @AzPreConstructEventListener " + describe(
                    annotationData) + "; expected a public static void method with descriptor " + descriptor + ".", e);
        }

        return Pair.of(registrator, (Class<? extends IPreConstructEvent>) parameterType);
    }

    /**
     * Parses the parameter types of a JVM method descriptor. Only the types named in the descriptor
     * itself are loaded; for @AzPreConstructEventListener methods that is the event type, which is
     * always safe to load on either dist.
     */
    private static @NotNull List<Class<?>> parseParameterTypes(final String descriptor,
                                                               final ModFileScanData.AnnotationData annotationData) {
        if (descriptor.charAt(0) != '(') {
            throw new IllegalStateException("Malformed method descriptor '" + descriptor + "' for " + describe(
                    annotationData) + ".");
        }

        final List<Class<?>> parameterTypes = new ArrayList<>();
        int index = 1;
        while (descriptor.charAt(index) != ')') {
            final int[] next = {index};
            parameterTypes.add(parseType(descriptor, next, annotationData));
            index = next[0];
            if (index >= descriptor.length()) {
                throw new IllegalStateException("Malformed method descriptor '" + descriptor + "' for " + describe(
                        annotationData) + "; unterminated parameter list.");
            }
        }
        return parameterTypes;
    }

    private static Class<?> parseType(final String descriptor, final int[] index,
                                      final ModFileScanData.AnnotationData annotationData) {
        int arrayDepth = 0;
        while (descriptor.charAt(index[0]) == '[') {
            arrayDepth++;
            index[0]++;
        }

        final char kind = descriptor.charAt(index[0]);
        final Class<?> baseType = switch (kind) {
            case 'B' -> {
                index[0]++;
                yield byte.class;
            }
            case 'C' -> {
                index[0]++;
                yield char.class;
            }
            case 'D' -> {
                index[0]++;
                yield double.class;
            }
            case 'F' -> {
                index[0]++;
                yield float.class;
            }
            case 'I' -> {
                index[0]++;
                yield int.class;
            }
            case 'J' -> {
                index[0]++;
                yield long.class;
            }
            case 'S' -> {
                index[0]++;
                yield short.class;
            }
            case 'Z' -> {
                index[0]++;
                yield boolean.class;
            }
            case 'L' -> {
                final int end = descriptor.indexOf(';', index[0]);
                if (end < 0) {
                    throw new IllegalStateException("Malformed method descriptor '" + descriptor + "' for " + describe(
                            annotationData) + "; unterminated reference type.");
                }
                final String className = descriptor.substring(index[0] + 1, end).replace('/', '.');
                index[0] = end + 1;
                yield loadType(className, annotationData, descriptor);
            }
            default -> throw new IllegalStateException("Malformed method descriptor '" + descriptor + "' for "
                    + describe(annotationData) + "; unexpected type kind '" + kind + "'.");
        };

        Class<?> type = baseType;
        for (int i = 0; i < arrayDepth; i++) {
            type = type.arrayType();
        }
        return type;
    }

    private static Class<?> loadType(final String className, final ModFileScanData.AnnotationData annotationData,
                                     final String descriptor) {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            return Class.forName(
                    className, false,
                    contextClassLoader != null ? contextClassLoader : CreateBlockEdits.class.getClassLoader()
            );
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Failed to load parameter type '" + className + "' from descriptor '" + descriptor
                            + "' of @AzPreConstructEventListener " + describe(annotationData) + ".",
                    e
            );
        }
    }

    private static String describe(final ModFileScanData.AnnotationData annotationData) {
        return annotationData.clazz().getClassName() + "#" + annotationData.memberName();
    }


    public static <T extends Event> void post(final T event) {
        discoverListeners();

        final List<MethodHandle> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null) {
            return;
        }

        for (final MethodHandle listener : eventListeners) {
            try {
                listener.invoke(event);
            } catch (final Throwable e) {
                throw new IllegalStateException(
                        "Failed to invoke @AzPreConstructEventListener " + listener + ".",
                        e
                );
            }
        }
    }

}
