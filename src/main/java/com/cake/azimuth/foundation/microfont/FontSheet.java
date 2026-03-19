package com.cake.azimuth.foundation.microfont;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper of spritesheet based monospace fonts
 */
public class FontSheet {

    final ResourceLocation source;
    final int charW;
    final int charH;
    final int spacing;

    final Map<Character, Vector2i> sprites;

    public FontSheet(final ResourceLocation source, final int charW, final int charH, final int spacing, final Map<Character, Vector2i> sprites) {
        this.source = source;
        this.charW = charW;
        this.charH = charH;
        this.spacing = spacing;
        this.sprites = sprites;
    }

    public void render(final GuiGraphics guiGraphics, final String text, final int x, final int y, final int color) {
        final float a = ((color >> 24) & 0xFF) / 255f;
        final float r = ((color >> 16) & 0xFF) / 255f;
        final float g = ((color >> 8) & 0xFF) / 255f;
        final float b = (color & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
        if (a != 1) {
            RenderSystem.enableBlend();
        }
        int drawX = x;
        for (int i = 0; i < text.length(); i++) {
            final Vector2i sprite = this.sprites.get(text.charAt(i));
            if (sprite != null) {
                guiGraphics.blit(this.source, drawX, y, (float) sprite.x, (float) sprite.y, this.charW, this.charH, 256, 256);
            }
            drawX += this.charW + this.spacing;
        }
        if (a != 1) {
            RenderSystem.disableBlend();
        }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public int getCharHeight() {
        return this.charH;
    }

    public int calculateWidth(final String text) {
        return text.length() * (this.charW + this.spacing) - this.spacing;
    }

    /**
     * Check if this font sheet has a glyph for the given character.
     * Space characters are always considered renderable (they produce visual gaps).
     */
    public boolean hasGlyph(final char c) {
        return c == ' ' || this.sprites.containsKey(c);
    }

    /**
     * Check if this font sheet can render every character in the given text.
     * Returns true if all characters have sprite mappings (or are spaces).
     * Returns true for empty/null strings.
     */
    public boolean canRender(final String text) {
        if (text == null || text.isEmpty()) return true;
        for (int i = 0; i < text.length(); i++) {
            if (!hasGlyph(text.charAt(i))) return false;
        }
        return true;
    }

    public static class Builder {

        final ResourceLocation source;
        final int charW;
        final int charH;
        final int spacing;
        final boolean monocase;

        final Map<Character, Vector2i> sprites = new HashMap<>();

        int cursorX = 0;
        int cursorY = 0;

        public Builder(final ResourceLocation source, final int charW, final int charH, final int spacing, final boolean monocase) {
            this.source = source;
            this.charW = charW;
            this.charH = charH;
            this.spacing = spacing;
            this.monocase = monocase;
        }

        public Builder addChar(final char charToAdd) {
            final Vector2i pos = new Vector2i(this.cursorX, this.cursorY);
            this.sprites.put(charToAdd, pos);
            this.cursorX += this.charW;
            return this;
        }

        public Builder addChars(final String chars) {
            for (int i = 0; i < chars.length(); i++) {
                this.addChar(chars.charAt(i));
            }
            return this;
        }


        public Builder nextLine() {
            this.cursorX = 0;
            this.cursorY += this.charH;
            return this;
        }

        public FontSheet build() {
            return new FontSheet(this.source, this.charW, this.charH, this.spacing, Collections.unmodifiableMap(this.sprites));
        }
    }


}
