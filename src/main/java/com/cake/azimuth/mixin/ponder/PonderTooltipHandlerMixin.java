package com.cake.azimuth.mixin.ponder;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.cake.azimuth.ponder.new_tooltip.NewPonderTooltipManager;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin that appends a gold "✦ NEW" badge to the ponder progress bar tooltip
 * when an item has unwatched ponder scenes. Includes conflict detection to avoid
 * duplicate badges if another mod (such as Simulated) adds its own.
 * <p>
 * This system originated from the Simulated project by the Simulated Team. Full credit goes to them
 * for the original concept and implementation. Adapted and iterated for Azimuth.
 */
@Mixin(PonderTooltipHandler.class)
public class PonderTooltipHandlerMixin {
    @WrapOperation(method = "makeProgressBar", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/lang/LangBuilder;component()Lnet/minecraft/network/chat/MutableComponent;"))
    private static MutableComponent azimuth$addNewPonderBadge(final LangBuilder instance, final Operation<MutableComponent> original) {
        final MutableComponent component = original.call(instance);
        final ItemStack stack = PonderTooltipHandlerAccessor.getTrackingStack();

        if (stack != null && !NewPonderTooltipManager.hasWatchedAllScenes(stack.getItem())) {
            if (!NewPonderTooltipManager.hasNewPonderBadge(component)) {
                component.append(" ").append(
                        Component.translatable("azimuth.tooltip.new_ponder")
                                .withStyle(ChatFormatting.GOLD)
                );
            }
        }

        return component;
    }
}
