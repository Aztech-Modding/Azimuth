package com.cake.azimuth.testmod.datagen;

import com.cake.azimuth.goggle.component.GoggleLangRegistry;
import com.cake.azimuth.goggle.builder.GoggleBuilder;
import com.cake.azimuth.testmod.AzimuthTestMod;
import com.cake.azimuth.testmod.content.MagicTankBlockEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TestModGoggleLangVerificationProvider implements DataProvider {

    private static final List<String> REQUIRED_KEYS = List.of(
            "azimuth_test.tooltip.magic_stats",
            "azimuth_test.tooltip.mana_stored",
            "azimuth_test.tooltip.mana_bar",
            "azimuth_test.tooltip.mana_capacity",
            "azimuth_test.tooltip.sneak_hint",
            "azimuth_test.tooltip.unit.mana"
    );

    private final PackOutput output;

    public TestModGoggleLangVerificationProvider(final PackOutput output) {
        this.output = output;
    }

    @Override
    public @Nonnull CompletableFuture<?> run(@Nonnull final CachedOutput cachedOutput) {
        final MagicTankBlockEntity dummy = new MagicTankBlockEntity(BlockPos.ZERO, AzimuthTestMod.MAGIC_TANK.get().defaultBlockState());
        dummy.buildGoggleStructure(GoggleBuilder.datagen(AzimuthTestMod.MODID));

        GoggleLangRegistry.collectFromRegisteredBlockEntities();
        final Map<String, Map<String, String>> snapshot = GoggleLangRegistry.snapshot();
        final Map<String, String> testmodEntries = snapshot.getOrDefault(AzimuthTestMod.MODID, Map.of());

        final JsonArray missing = new JsonArray();
        for (final String key : REQUIRED_KEYS) {
            if (!testmodEntries.containsKey(key)) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalStateException("Goggle datagen verification failed; missing keys: " + missing);
        }

        final JsonObject report = new JsonObject();
        report.addProperty("modId", AzimuthTestMod.MODID);
        report.addProperty("entryCount", testmodEntries.size());

        final JsonArray verified = new JsonArray();
        REQUIRED_KEYS.forEach(verified::add);
        report.add("verifiedKeys", verified);

        final PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "goggle_validation");
        final Path outputPath = pathProvider.json(Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(AzimuthTestMod.MODID, "report")));
        return DataProvider.saveStable(cachedOutput, report, outputPath);
    }

    @Override
    public @Nonnull String getName() {
        return "Azimuth TestMod Goggle Lang Verification";
    }
}
