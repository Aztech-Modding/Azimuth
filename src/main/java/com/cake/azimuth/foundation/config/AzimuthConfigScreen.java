package com.cake.azimuth.foundation.config;

import com.cake.azimuth.ponder.new_tooltip.NewPonderTooltipManager;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.gui.element.TextStencilElement;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class AzimuthConfigScreen extends BaseConfigScreen {

    public AzimuthConfigScreen(@Nullable Screen parent, String modID) {
        super(parent, modID);
    }

    @Override
    protected void init() {
        super.init();

        TextStencilElement clearText = new TextStencilElement(font, Component.literal("Clear Watched Ponders"))
                .centered(true, true);
        BoxWidget clearButton = new BoxWidget(width / 2 - 100, height / 2 - 15 + 60, 200, 16)
                .showingElement(clearText);
        clearText.withElementRenderer(BoxWidget.gradientFactory.apply(clearButton));
        clearButton.withCallback(() -> {
            NewPonderTooltipManager.clearAllWatched();
            clearButton.active = false;
            clearButton.updateGradientFromState();
        });
        clearButton.getToolTip().add(Component.literal("Resets all ponder scenes to unwatched, restoring ✦ NEW badges"));
        addRenderableWidget(clearButton);
    }
}
