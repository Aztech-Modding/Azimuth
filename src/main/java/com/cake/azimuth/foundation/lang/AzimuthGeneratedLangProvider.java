package com.cake.azimuth.foundation.lang;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AzimuthGeneratedLangProvider implements DataProvider {

    private final PackOutput output;

    public AzimuthGeneratedLangProvider(final PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@Nonnull final CachedOutput cache) {
        AzimuthGeneratedLangEntry.collectFromRegisteredBlockEntities();
        final PackOutput.PathProvider pathProvider = this.output.createPathProvider(
                PackOutput.Target.RESOURCE_PACK,
                "lang"
        );
        final Map<String, Map<String, String>> entriesByMod = AzimuthGeneratedLangEntry.snapshot();
        final List<CompletableFuture<?>> futures = new ArrayList<>();

        entriesByMod.keySet().forEach(modId -> {
            final List<Map.Entry<String, String>> sortedEntries = new ArrayList<>();
            AzimuthGeneratedLangEntry.provideLang(modId, (k, v) -> sortedEntries.add(Map.entry(k, v)));

            if (sortedEntries.isEmpty()) {
                return;
            }

            sortedEntries.sort(Map.Entry.comparingByKey());

            final JsonObject json = new JsonObject();
            sortedEntries.forEach(e -> json.addProperty(e.getKey(), e.getValue()));

            final Path path = pathProvider.json(Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(
                    Objects.requireNonNull(modId), "en_us")));
            futures.add(DataProvider.saveStable(Objects.requireNonNull(cache), json, Objects.requireNonNull(path)));
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Azimuth Goggle Tooltip Language Entries";
    }
}
