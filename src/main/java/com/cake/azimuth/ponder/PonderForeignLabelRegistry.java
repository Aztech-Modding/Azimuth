package com.cake.azimuth.ponder;

import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for mods that inject ponder scenes into Create.
 * Registered mods will have their label rendered below the ponder scene title
 * using the microfont renderer, attributing the scene to its source mod.
 * This happens when the ponder scene's namespace matches the registered mod ID, but the pondered item does not belong to that mod.
 */
public class PonderForeignLabelRegistry {

    private static final Map<String, String> REGISTERED_LABELS = new ConcurrentHashMap<>();

    /**
     * Register a mod as a ponder labeling mod.
     * When a ponder scene's namespace matches the given modId,
     * but the item to be pondered does not match that modId,
     * the label will be rendered below the scene title in microfont.
     *
     * @param modId The mod's namespace (e.g., "bits_n_bobs")
     * @param label The label text to display (e.g., "BITS N BOBS"). Will be uppercased for microfont rendering.
     */
    public static void register(final String modId, final String label) {
        REGISTERED_LABELS.put(modId, label.toUpperCase());
    }

    /**
     * Register a mod as a ponder labeling mod with a translatable component.
     * Use this for full localisation support — the component will be resolved
     * at render time using the current locale.
     *
     * @param modId The mod's namespace (e.g., "bits_n_bobs")
     * @param label The label component (e.g., Component.translatable("mymod.ponder.label"))
     */
    public static void register(final String modId, final Component label) {
        REGISTERED_LABELS.put(modId, label.getString().toUpperCase());
    }

    /**
     * Look up the label for a given scene namespace.
     *
     * @param namespace The ponder scene's namespace (from PonderScene.getNamespace())
     * @return The registered label, or empty if no label is registered for this namespace
     */
    public static Optional<String> getLabel(final String namespace) {
        return Optional.ofNullable(REGISTERED_LABELS.get(namespace));
    }

    /**
     * Check if a namespace has a registered ponder label.
     */
    public static boolean hasLabel(final String namespace) {
        return REGISTERED_LABELS.containsKey(namespace);
    }
}
