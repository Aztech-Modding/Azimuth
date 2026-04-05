package com.cake.azimuth.mixin.ponder;

import com.cake.azimuth.ponder.new_tooltip.NewPonderTooltipManager;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin that marks ponder scenes as watched when the player scrolls through
 * them in the Ponder UI.
 * <p>
 * This system originated from the Simulated project by the Simulated Team. Full credit goes to them
 * for the original concept and implementation. Adapted and iterated for Azimuth.
 */
@Mixin(PonderUI.class)
public class PonderUIMixin {
    @Shadow @Final private List<PonderScene> scenes;
    @Shadow private int index;

    @Inject(method = "scroll", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/foundation/PonderScene;begin()V"))
    private void azimuth$markSceneWatched(final boolean forward, final CallbackInfoReturnable<Boolean> cir) {
        NewPonderTooltipManager.setSceneWatched(this.scenes.get(this.index).getId());
    }
}
