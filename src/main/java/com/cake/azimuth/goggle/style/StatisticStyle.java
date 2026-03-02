package com.cake.azimuth.goggle.style;

import com.cake.azimuth.goggle.builder.StatisticGoggleBuilder;

public interface StatisticStyle<T extends StatisticGoggleBuilder> {
    T apply(StatisticGoggleBuilder builder);
}