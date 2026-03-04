package com.cake.azimuth.advancement;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import net.minecraft.resources.ResourceLocation;

public interface CreateAdvancementIdAccessor {

    String azimuth$getId();

    static ResourceLocation asId(final CreateAdvancement advancement) {
        final String id = ((CreateAdvancementIdAccessor) advancement).azimuth$getId();
        return Create.asResource(id);
    }
}
