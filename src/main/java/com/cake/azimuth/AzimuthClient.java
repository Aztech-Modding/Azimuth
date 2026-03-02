package com.cake.azimuth;

import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.cake.azimuth.goggle.command.AzimuthClientCommands;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Mod(value = Azimuth.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Azimuth.MODID, value = Dist.CLIENT)
public class AzimuthClient {
    public AzimuthClient(final ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(AzimuthClient::clientInit);
    }

    private static void clientInit() {
        BaseConfigScreen.setDefaultActionFor(Azimuth.MODID, base -> base
                .withButtonLabels("Client Settings", "Common Settings", "Server Settings")
                .withSpecs(AzimuthConfigs.client().specification, AzimuthConfigs.common().specification, AzimuthConfigs.server().specification)
        );
    }

    @SubscribeEvent
    static void onRegisterClientCommands(final RegisterClientCommandsEvent event) {
        AzimuthClientCommands.register(event.getDispatcher());
    }

    @EventBusSubscriber(modid = Azimuth.MODID, value = Dist.CLIENT)
    private static class ModBusEvents {
        @SubscribeEvent
        public static void onLoadComplete(final FMLLoadCompleteEvent event) {
            final ModContainer container = ModList.get()
                    .getModContainerById(Azimuth.MODID)
                    .orElseThrow(() -> new IllegalStateException("Azimuth mod container missing on LoadComplete"));
            final Supplier<IConfigScreenFactory> configScreen = () ->
                    (mc, previousScreen) -> new BaseConfigScreen(previousScreen, Azimuth.MODID);
            container.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
        }
    }
}
