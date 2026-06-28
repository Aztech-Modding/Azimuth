package com.cake.azimuth.registration.event;

import com.cake.azimuth.foundation.preconstruct.IPreConstructEvent;
import com.cake.azimuth.registration.VisualWrapperInterest;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.Event;

import java.util.function.Predicate;

public class RegisterVisualWrapperInterestEvent extends Event implements IPreConstructEvent {

    public void registerInterest(final Predicate<BlockEntityType<?>> typePredicate) {
        VisualWrapperInterest.registerInterest(typePredicate);
    }

    public void registerInterest(final BlockEntityType<?> type) {
        VisualWrapperInterest.registerInterest(type);
    }


}
