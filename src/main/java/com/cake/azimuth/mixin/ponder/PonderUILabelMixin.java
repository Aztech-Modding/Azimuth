package com.cake.azimuth.mixin.ponder;

import com.cake.azimuth.foundation.microfont.Microfont;
import com.cake.azimuth.ponder.PonderForeignLabelRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * Renders a microfont mod-attribution label just below the ponder scene title.
 * Only active on the single-scene / non-transitioning path, where the title is
 * drawn via ClientFontHelper.drawSplitString and the PoseStack origin sits at
 * the title's top-left corner.
 */
@Mixin(PonderUI.class)
public class PonderUILabelMixin {

    @Shadow
    @Final
    private List<PonderScene> scenes;

    @Shadow
    private ItemStack stack;

    @Inject(
            method = "renderSceneInformation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/lang/ClientFontHelper;drawSplitString(Lnet/minecraft/client/gui/GuiGraphics;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIII)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void azimuth$renderPonderModLabel1(
            final GuiGraphics graphics, final float fade, final float indexDiff,
            final PonderScene activeScene, final int tooltipColor, final CallbackInfo ci) {
        azimuth$renderSceneSourceHint(graphics, fade, activeScene);
    }

    @Inject(
            method = "renderSceneInformation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/lang/ClientFontHelper;drawSplitString(Lnet/minecraft/client/gui/GuiGraphics;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIII)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void azimuth$renderPonderModLabel2(
            final GuiGraphics graphics, final float fade, final float indexDiff,
            final PonderScene activeScene, final int tooltipColor, final CallbackInfo ci, final @Local(name = "otherIndex") int other) {
        final float absoluteIndexDiff = Math.abs(indexDiff);
        azimuth$renderSceneSourceHint(graphics, absoluteIndexDiff, scenes.get(other));
    }

    @Inject(
            method = "renderSceneInformation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/lang/ClientFontHelper;drawSplitString(Lnet/minecraft/client/gui/GuiGraphics;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIII)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    private void azimuth$renderPonderModLabel2(
            final GuiGraphics graphics, final float fade, final float indexDiff,
            final PonderScene activeScene, final int tooltipColor, final CallbackInfo ci) {
        final float absoluteIndexDiff = Math.abs(indexDiff);
        azimuth$renderSceneSourceHint(graphics, 1 - absoluteIndexDiff, activeScene);
    }

    @Unique
    private void azimuth$renderSceneSourceHint(final GuiGraphics graphics, final float fade, final PonderScene activeScene) {
        final String namespace = activeScene.getNamespace();
        final Optional<String> label = PonderForeignLabelRegistry.getLabel(namespace);
        if (label.isEmpty() && !BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().equals(namespace)) return;

        final String title = activeScene.getTitle();

        final int alpha = (int) (fade * 0x88);
        if (alpha < 4) return;
        final int color = (alpha << 24) | 0xffffff;
        final int x = Minecraft.getInstance().font.width(title) + 5;
        Microfont.renderSmart(graphics, Minecraft.getInstance().font, label.get(), x, 3, color);
    }
}
