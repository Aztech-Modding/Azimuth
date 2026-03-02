package com.cake.azimuth.registration.goggle;

import com.cake.azimuth.goggle.builder.StatisticGoggleBuilder;
import com.cake.azimuth.goggle.component.GoggleBuilderHelper;
import com.cake.azimuth.goggle.component.GoggleComponent;
import com.cake.azimuth.goggle.style.StatisticStyle;
import net.minecraft.ChatFormatting;

public class CreateGoggleStyles {

    private static final GoggleBuilderHelper HELPER = new GoggleBuilderHelper("create");

    public static final GoggleComponent UNIT_SU = HELPER.component("create.generic.unit.stress");
    public static final GoggleComponent UNIT_RPM = HELPER.component("create.generic.unit.rpm");
    public static final GoggleComponent UNIT_MB = HELPER.component("create.generic.unit.millibuckets");

    public static final StatisticStyle<StatisticGoggleBuilder> SU =
            HELPER.measurementUnitStyle(UNIT_SU, builder -> builder.getStatisticDisplay().withStatisticColor(ChatFormatting.AQUA));

    public static final StatisticStyle<StatisticGoggleBuilder> RPM =
            HELPER.measurementUnitStyle(UNIT_RPM, builder -> builder.getStatisticDisplay().withStatisticColor(ChatFormatting.AQUA));

    public static final StatisticStyle<StatisticGoggleBuilder> MB =
            HELPER.measurementUnitStyle(UNIT_MB, builder -> builder.getStatisticDisplay().withStatisticColor(ChatFormatting.GOLD));

    public static void register() {
    }
}
