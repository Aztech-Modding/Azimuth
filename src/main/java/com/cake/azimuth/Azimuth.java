package com.cake.azimuth;

import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.cake.azimuth.registration.goggle.AzimuthGoggleStyles;
import com.cake.azimuth.registration.goggle.CreateGoggleComponents;
import com.cake.azimuth.registration.goggle.CreateGoggleStyles;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Azimuth.MODID)
public class Azimuth {
    public static final String MODID = "azimuth";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Azimuth(final IEventBus modEventBus, final ModContainer modContainer) {
        AzimuthConfigs.register(ModLoadingContext.get(), modContainer);

        CreateGoggleStyles.register();
        AzimuthGoggleStyles.register();
        CreateGoggleComponents.register();
    }

}
