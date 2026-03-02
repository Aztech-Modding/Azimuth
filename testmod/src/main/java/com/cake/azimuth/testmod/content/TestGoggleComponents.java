package com.cake.azimuth.testmod.content;

import com.cake.azimuth.goggle.builder.StatisticGoggleBuilder;
import com.cake.azimuth.goggle.component.GoggleBuilderHelper;
import com.cake.azimuth.goggle.component.GoggleComponent;
import com.cake.azimuth.goggle.style.BarChartGoggleBuilder;
import com.cake.azimuth.goggle.style.StatisticStyle;
import com.cake.azimuth.testmod.AzimuthTestMod;
import net.minecraft.ChatFormatting;

public class TestGoggleComponents {

    public static final GoggleBuilderHelper HELPER = new GoggleBuilderHelper(AzimuthTestMod.MODID);
    public static final GoggleComponent SECTION_MAGIC = HELPER.component("magic_stats", "Magical Stats:");
    public static final GoggleComponent STAT_MANA = HELPER.component("mana_stored", "Stored Mana:");

    public static final StatisticStyle<StatisticGoggleBuilder> MANA_UNITS =
            HELPER.measurementUnitStyle("mana", "Mana",
                    builder -> builder.getStatisticDisplay().withStatisticColor(ChatFormatting.DARK_PURPLE));

    public static final StatisticStyle<BarChartGoggleBuilder> MANA_BAR =
            HELPER.barChartStyle(20,
                    builder -> {
                        builder.getStatisticDisplay().withStatisticColor(ChatFormatting.DARK_PURPLE);
                        builder.getStatisticDisplay().withBarColor(0x8800FF);
                        builder.getStatisticDisplay().withBackgroundBarColor(0x220055);
                    });

    public static void register() {
    }
}
