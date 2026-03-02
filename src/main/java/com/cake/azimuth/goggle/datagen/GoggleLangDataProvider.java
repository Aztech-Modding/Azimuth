package com.cake.azimuth.goggle.datagen;

import com.cake.azimuth.goggle.component.GoggleLangRegistry;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nonnull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GoggleLangDataProvider implements DataProvider {

    private final PackOutput output;

    public GoggleLangDataProvider(final PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(@Nonnull final CachedOutput cache) {
        GoggleLangRegistry.collectFromRegisteredBlockEntities();
        final PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang");
        final Map<String, Map<String, String>> entriesByMod = GoggleLangRegistry.snapshot();
        final List<CompletableFuture<?>> futures = new ArrayList<>();

        entriesByMod.keySet().forEach(modId -> {
            final List<Map.Entry<String, String>> sortedEntries = new ArrayList<>();
            GoggleLangRegistry.provideLang(modId, (k, v) -> sortedEntries.add(Map.entry(k, v)));

            if (sortedEntries.isEmpty()) {
                return;
            }
            
            sortedEntries.sort(Map.Entry.comparingByKey());
            
            final JsonObject json = new JsonObject();
            sortedEntries.forEach(e -> json.addProperty(e.getKey(), e.getValue()));

            final Path path = pathProvider.json(Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(Objects.requireNonNull(modId), "en_us")));
            futures.add(DataProvider.saveStable(Objects.requireNonNull(cache), json, Objects.requireNonNull(path)));
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Azimuth Goggle Tooltip Language Entries";
    }
}
