package com.cake.azimuth.goggle.api;

import com.cake.azimuth.goggle.builder.GoggleBuilder;

/**
 * Optional hook for block entities that need mock any extra state before datagen scans their goggle structure.
 */
public interface IDatagenGoggleInitializer {

    void onGoggleDatagenInit(GoggleBuilder builder);
}