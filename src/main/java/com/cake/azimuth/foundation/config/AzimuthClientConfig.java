package com.cake.azimuth.foundation.config;

import net.createmod.catnip.config.ConfigBase;

public class AzimuthClientConfig extends ConfigBase {

    public final ConfigGroup tooltip = group(0, "tooltipBuilder", "Tooltip Builder");
    public final ConfigBool tooltipBuilderDebug = b(false, "tooltipBuilderDebug",
            "Render goggle tooltip translation keys literally for live updates.");

    @Override
    public String getName() {
        return "client";
    }
}
