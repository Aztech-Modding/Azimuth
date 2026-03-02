package com.cake.azimuth.testmod.datagen;

import com.cake.azimuth.testmod.AzimuthTestMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AzimuthTestMod.MODID)
public class TestModDatagenEvents {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new TestModGoggleLangVerificationProvider(event.getGenerator().getPackOutput()));
    }
}
