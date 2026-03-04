package com.cake.azimuth.foundation.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class AzimuthCommonConfig extends ConfigBase {

    public final ConfigGroup common = group(0, "common", "Common Settings");
    public final ConfigBool logDirtBlock = b(true, "logDirtBlock", "Whether to log the dirt block on common setup.");

    @Override
    public @NotNull String getName() {
        return "common";
    }
}
