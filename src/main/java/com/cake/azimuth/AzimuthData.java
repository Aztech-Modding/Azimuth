package com.cake.azimuth;

import com.cake.azimuth.goggle.datagen.GoggleLangDataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.cake.azimuth.Azimuth.REGISTRATE;

@EventBusSubscriber(modid = Azimuth.MODID)
public class AzimuthData {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeClient(),
                new GoggleLangDataProvider(event.getGenerator().getPackOutput())
        );
    }

    public static void addRegistrateData() {
        REGISTRATE.addRawLang("azimuth.tooltip.new_ponder", "(New!)");
    }
}
