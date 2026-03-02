package com.cake.azimuth.registration.goggle;

import com.cake.azimuth.goggle.builder.StatisticGoggleBuilder;
import com.cake.azimuth.goggle.component.GoggleBuilderHelper;
import com.cake.azimuth.goggle.style.BarChartGoggleBuilder;
import com.cake.azimuth.goggle.style.StatisticStyle;

public class AzimuthGoggleStyles {

    private static final GoggleBuilderHelper HELPER = new GoggleBuilderHelper("azimuth");

    public static final StatisticStyle<StatisticGoggleBuilder> CUG_GRAM =
            HELPER.measurementUnitStyle("cug_gram", "cg");

    public static final StatisticStyle<StatisticGoggleBuilder> CUG_GRAM_M2 =
            HELPER.measurementUnitStyle("cug_gram_m2", "cg/m²");

    public static final StatisticStyle<StatisticGoggleBuilder> CUG_GRAM_M3 =
            HELPER.measurementUnitStyle("cug_gram_m3", "cg/m³");

    public static final StatisticStyle<BarChartGoggleBuilder> PINK_BAR =
            HELPER.barChartStyle(20, b -> {
                b.getStatisticDisplay().withBarColor(0xFF66CC);
                b.getStatisticDisplay().withBackgroundBarColor(0x442233);
            });

    public static final StatisticStyle<BarChartGoggleBuilder> RED_GREEN_BAR =
            HELPER.barChartStyle(20, b -> {
                b.getStatisticDisplay().withBarColor(0x66CC66);
                b.getStatisticDisplay().withBackgroundBarColor(0x663333);
            });

    public static final StatisticStyle<BarChartGoggleBuilder> BLUE_BAR =
            HELPER.barChartStyle(20, b -> {
                b.getStatisticDisplay().withBarColor(0x6699FF);
                b.getStatisticDisplay().withBackgroundBarColor(0x223355);
            });

    public static void register() {
    }
}
