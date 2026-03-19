package com.cake.azimuth.foundation.microfont;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Teeny tiny barely even readable font used for type annotations on pins / data
 */
public class Microfont {

    public static final FontSheet FONT = new FontSheet.Builder(
            ResourceLocation.fromNamespaceAndPath("azimuth", "textures/gui/microfont.png"),
            3, 4, 1, true
    ).addChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ<>/").build();

    public static void render(final GuiGraphics guiGraphics, final String text, final int x, final int y, final int color) {
        FONT.render(guiGraphics, text, x, y, color);
    }

    public static void renderHighlighted(final GuiGraphics guiGraphics, final String text, final int x, final int y, final int color, final int bgColor) {
        RenderSystem.enableBlend();
        guiGraphics.fill(x - 1, y - 1, x + calculateWidth(text) + 1, y + getCharHeight() + 1, bgColor);
        FONT.render(guiGraphics, text, x, y, color);
    }

    public static int calculateWidth(final String text) {
        return FONT.calculateWidth(text);
    }

    public static int getCharHeight() {
        return FONT.getCharHeight();
    }

    /**
     * Smart render that auto-detects charset compatibility.
     * If all characters are in the microfont charset, renders using crisp sprites.
     * If any character is unsupported, falls back to Minecraft's Font at 0.5x scale.
     */
    public static void renderSmart(final GuiGraphics guiGraphics, final Font mcFont, final String text, final int x, final int y, final int color) {
        if (FONT.canRender(text)) {
            FONT.render(guiGraphics, text, x, y, color);
        } else {
            renderFallback(guiGraphics, mcFont, text, x, y, color);
        }
    }

    private static void renderFallback(final GuiGraphics guiGraphics, final Font mcFont, final String text, final int x, final int y, final int color) {
        final PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(0.5f, 0.5f, 1f);
        guiGraphics.drawString(mcFont, text, 0, 0, color, false);
        poseStack.popPose();
    }

    public static int calculateSmartWidth(final Font mcFont, final String text) {
        if (FONT.canRender(text)) {
            return FONT.calculateWidth(text);
        }
        return (int) Math.ceil(mcFont.width(text) * 0.5f);
    }

    public static int getSmartHeight(final String text) {
        if (FONT.canRender(text)) {
            return FONT.getCharHeight();
        }
        return 5;
    }
}
