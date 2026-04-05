package com.cake.azimuth.mixin.ponder;

import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin for {@link PonderTooltipHandler} to expose the private tracking stack.
 * <p>
 * This system originated from the Simulated project by the Simulated Team. Full credit goes to them
 * for the original concept and implementation. Adapted and iterated for Azimuth.
 */
@Mixin(PonderTooltipHandler.class)
public interface PonderTooltipHandlerAccessor {
    @Accessor
    static ItemStack getTrackingStack() {
        return null;
    }
}
