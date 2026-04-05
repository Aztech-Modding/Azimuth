package com.cake.azimuth.ponder.new_tooltip;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class NewPonderTooltipManager {

    public static Builder forItems(final Item... items) {
        return new Builder();
    }

    public static class Builder {

        public Builder addScenes(final ResourceLocation... scenes) {
            return this;
        }
    }
}
