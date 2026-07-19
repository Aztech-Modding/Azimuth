package com.cake.azimuth.ponder;

import java.util.Locale;
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
     * Register a mod as a ponder labelling mod with a title.
     *
     * @param modId The mod's namespace (e.g., "bits_n_bobs")
     * @param label The label component of the canonical name of the mod (e.g., "Bits n Bobs"). Will be uppercased for microfont rendering.
     */
    public static void register(final String modId, final String label) {
        REGISTERED_LABELS.put(modId, label.toUpperCase(Locale.ROOT));
    }


    /**
     * Register a mod as a ponder labelling mod with a title (derived from mod id).
     *
     * @param modId The mod's namespace (e.g., "bits_n_bobs")
     */
    public static void register(final String modId) {
        REGISTERED_LABELS.put(modId, modId.replace("_", " "));
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
