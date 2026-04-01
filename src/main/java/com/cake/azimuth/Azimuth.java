package com.cake.azimuth;

import com.cake.azimuth.content.AzContent;
import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.cake.azimuth.registration.goggle.AzimuthGoggleStyles;
import com.cake.azimuth.registration.goggle.CreateGoggleComponents;
import com.cake.azimuth.registration.goggle.CreateGoggleStyles;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Azimuth.MODID)
public class Azimuth {
    public static final String MODID = "azimuth";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public Azimuth(final IEventBus modEventBus, final ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        AzimuthConfigs.register(ModLoadingContext.get(), modContainer);

        CreateGoggleStyles.register();
        AzimuthGoggleStyles.register();
        CreateGoggleComponents.register();

        AzContent.register();
        AzimuthData.addRegistrateData();
    }

    public static ResourceLocation asResource(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
