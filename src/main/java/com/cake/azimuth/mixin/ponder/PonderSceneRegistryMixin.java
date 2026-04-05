package com.cake.azimuth.mixin.ponder;

import com.cake.azimuth.ponder.new_tooltip.NewPonderTooltipManager;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin that marks the first ponder scene as watched when a scene registry entry
 * is compiled (i.e. when the player first opens ponder for an item).
 * <p>
 * This system originated from the Simulated project by the Simulated Team. Full credit goes to them
 * for the original concept and implementation. Adapted and iterated for Azimuth.
 */
@Mixin(PonderSceneRegistry.class)
public class PonderSceneRegistryMixin {
    @Inject(method = "compile(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/List;", at = @At("RETURN"))
    private void azimuth$markFirstSceneWatched(final ResourceLocation id, final CallbackInfoReturnable<List<PonderScene>> cir) {
        final List<PonderScene> scenes = cir.getReturnValue();
        if (scenes != null && !scenes.isEmpty()) {
            NewPonderTooltipManager.setSceneWatched(scenes.getFirst().getId());
        }
    }
}
