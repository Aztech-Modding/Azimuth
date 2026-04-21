package com.cake.azimuth.goggle.api;

import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.cake.azimuth.goggle.builder.GoggleBuilder;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Declarative wrapper around Create's goggle tooltip API.
 */
public interface IBuildGoggleInformation extends IHaveGoggleInformation {

    void buildGoggleStructure(GoggleBuilder builder);

    String getModId();

    @Override
    default boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        return this.addGoggleBuilderToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    default boolean addGoggleBuilderToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        final GoggleBuilder builder = new GoggleBuilder(tooltip, isPlayerSneaking, this.getModId());
        this.buildGoggleStructure(builder);
        if (AzimuthConfigs.tooltipBuilderDebugEnabled() && builder.hasAppendedData()) {
            builder.label("debug_enabled", "Tooltip builder debug enabled (Using live code lang)")
                    .withLabelFormatting(ChatFormatting.BOLD)
                    .withLabelFormatting(ChatFormatting.GOLD);
        }
        return builder.hasAppendedData();
    }

}