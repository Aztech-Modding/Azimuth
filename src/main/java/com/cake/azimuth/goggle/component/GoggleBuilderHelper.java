package com.cake.azimuth.goggle.component;

import com.cake.azimuth.goggle.builder.StatisticGoggleBuilder;
import com.cake.azimuth.goggle.style.BarChartGoggleBuilder;
import com.cake.azimuth.goggle.style.StatisticStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Consumer;

public class GoggleBuilderHelper {

    private final String modId;

    public GoggleBuilderHelper(final String modId) {
        this.modId = modId;
    }

    public void provideLang(final java.util.function.BiConsumer<String, String> consumer) {
        GoggleLangRegistry.provideLang(this.modId, consumer);
    }

    public GoggleComponent component(final String keySuffix, final String defaultEnglish) {
        final GoggleComponent component = GoggleComponent.scoped(modId, keySuffix, defaultEnglish);
        GoggleLangRegistry.registerComponent(component);
        return component;
    }

    public GoggleComponent component(final String translationKey) {
        return GoggleComponent.absolute(translationKey);
    }

    public StatisticStyle<StatisticGoggleBuilder> measurementUnitStyle(final String keySuffix, final String defaultEnglish) {
        return measurementUnitStyle(keySuffix, defaultEnglish, null);
    }

    public StatisticStyle<StatisticGoggleBuilder> measurementUnitStyle(final String keySuffix,
                                                                       final String defaultEnglish,
                                                                       final Consumer<StatisticGoggleBuilder> defaultConfig) {
        final GoggleComponent unit = component("unit." + keySuffix, defaultEnglish);
        return measurementUnitStyle(unit, defaultConfig);
    }

    public StatisticStyle<StatisticGoggleBuilder> measurementUnitStyle(final GoggleComponent unit) {
        return measurementUnitStyle(unit, null);
    }

    public StatisticStyle<StatisticGoggleBuilder> measurementUnitStyle(final GoggleComponent unit,
                                                                       final Consumer<StatisticGoggleBuilder> defaultConfig) {
        return builder -> {
            final Component suffix = Objects.requireNonNull(Component.literal(" ")
                    .append(Objects.requireNonNull(builder.getBuilder().renderComponent(unit).copy().withStyle(ChatFormatting.DARK_GRAY))));
            builder.getStatisticDisplay().appendValueSuffix(suffix);
            if (defaultConfig != null) {
                defaultConfig.accept(builder);
            }
            return builder;
        };
    }

    public StatisticStyle<BarChartGoggleBuilder> barChartStyle(final int width) {
        return barChartStyle(width, null);
    }

    public StatisticStyle<BarChartGoggleBuilder> barChartStyle(final int width,
                                                               final Consumer<BarChartGoggleBuilder> defaultConfig) {
        final int resolvedWidth = Math.max(3, width);
        return builder -> {
            final BarChartGoggleBuilder chartBuilder = new BarChartGoggleBuilder(builder, resolvedWidth);
            if (defaultConfig != null) {
                defaultConfig.accept(chartBuilder);
            }
            chartBuilder.getStatisticDisplay().refreshLine();
            return chartBuilder;
        };
    }

    public String modId() {
        return modId;
    }

    public static int colorOf(final ChatFormatting formatting) {
        return Objects.requireNonNullElse(formatting.getColor(), 0xFFFFFF);
    }
}