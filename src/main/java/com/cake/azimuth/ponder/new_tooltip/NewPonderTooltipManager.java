package com.cake.azimuth.ponder.new_tooltip;

import com.cake.azimuth.Azimuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages tracking of which ponder scenes a player has watched, and provides
 * a registration API for mods to associate items with their ponder scene IDs.
 * When an item has unwatched scenes, a gold "NEW" badge is appended to the
 * ponder progress bar tooltip.
 * <p>
 * This system originated from the Simulated project by the Simulated Team. Full credit goes to them
 * for the original concept and implementation. Adapted and iterated for Azimuth.
 */
public class NewPonderTooltipManager {
    private static final Codec<Set<ResourceLocation>> CODEC = ResourceLocation.CODEC.listOf().xmap(
            HashSet::new, set -> set.stream().toList());
    private static final HashMap<Item, Set<ResourceLocation>> NEW_PONDER_SCENES = new HashMap<>();
    private static volatile Set<ResourceLocation> WATCHED_PONDER_SCENES = null;
    private static final Object LOCK = new Object();

    private static Path filePath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("ponders_watched_azimuth.json");
    }

    public static RegisterBuilder forItems(final Item... items) {
        return new RegisterBuilder(items);
    }

    public static boolean hasWatchedAllScenes(final Item item) {
        load();
        if (NEW_PONDER_SCENES.containsKey(item)) {
            final Set<ResourceLocation> scenes = NEW_PONDER_SCENES.get(item);
            return WATCHED_PONDER_SCENES.containsAll(scenes);
        }
        return true;
    }

    public static void setSceneWatched(final ResourceLocation id) {
        load();
        if (WATCHED_PONDER_SCENES != null && !hasWatchedScene(id)) {
            WATCHED_PONDER_SCENES.add(id);
            save();
        }
    }

    public static boolean hasWatchedScene(final ResourceLocation id) {
        load();
        return WATCHED_PONDER_SCENES != null && WATCHED_PONDER_SCENES.contains(id);
    }

    public static void clearAllWatched() {
        load();
        if (WATCHED_PONDER_SCENES != null) {
            WATCHED_PONDER_SCENES.clear();
            save();
        }
    }

    /**
     * Checks whether a component already contains a "new ponder" badge from any mod.
     * This enables conflict detection — if another mod (e.g. Simulated) has already
     * appended a new ponder indicator, we skip adding ours to avoid duplication.
     *
     * @param component the tooltip component to check
     * @return true if a new ponder badge is already present
     */
    public static boolean hasNewPonderBadge(final MutableComponent component) {
        for (final Component sibling : component.getSiblings()) {
            if (sibling.getContents() instanceof final TranslatableContents translatable) {
                if (translatable.getKey().contains("new")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void save() {
        final DataResult<JsonElement> result = CODEC.encode(WATCHED_PONDER_SCENES, JsonOps.INSTANCE, new JsonArray());
        if (result.isError()) {
            Azimuth.LOGGER.warn("Failed to encode ponder watch data: {}", result.error().orElse(null));
            return;
        }

        try {
            final Path target = filePath();
            final Path temp = target.resolveSibling(target.getFileName() + ".tmp");
            final String data = result.getOrThrow().toString();
            Files.writeString(temp, data, StandardCharsets.UTF_8);
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (final IOException e) {
            Azimuth.LOGGER.warn("Failed to save ponder watch data", e);
        }
    }

    public static void load() {
        if (WATCHED_PONDER_SCENES != null) return;

        synchronized (LOCK) {
            if (WATCHED_PONDER_SCENES != null) return;

            final JsonElement json = getOrCreateFile();
            if (json == null) {
                WATCHED_PONDER_SCENES = new HashSet<>();
                return;
            }

            final DataResult<Set<ResourceLocation>> result = CODEC.parse(JsonOps.INSTANCE, json);

            result.ifSuccess(set -> WATCHED_PONDER_SCENES = new HashSet<>(set));
            result.ifError(error -> {
                Azimuth.LOGGER.warn("Failed to parse ponder watch data: {}", error.message());
                WATCHED_PONDER_SCENES = new HashSet<>();
            });
        }
    }

    private static JsonElement getOrCreateFile() {
        try {
            final Path path = filePath();

            if (Files.exists(path)) {
                return JsonParser.parseString(Files.readString(path));
            } else {
                Files.writeString(path, "[]");
                return new JsonArray();
            }
        } catch (final IOException e) {
            Azimuth.LOGGER.warn("Failed to access ponder watch file", e);
        }
        return null;
    }

    public record RegisterBuilder(Item... items) {
        /**
         * Register ponder scene IDs that should be tracked for the associated items.
         * When all registered scenes for an item have been watched, the "NEW" badge
         * will no longer appear on that item's tooltip.
         *
         * @param scenes set of scene IDs as set by
         *               {@link net.createmod.ponder.foundation.PonderSceneBuilder#title(String, String)}
         */
        public RegisterBuilder addScenes(final ResourceLocation... scenes) {
            final Set<ResourceLocation> sceneSet = new HashSet<>(List.of(scenes));
            for (final Item item : this.items) {
                NEW_PONDER_SCENES.computeIfAbsent(item, k -> new HashSet<>()).addAll(sceneSet);
            }
            return this;
        }
    }
}
