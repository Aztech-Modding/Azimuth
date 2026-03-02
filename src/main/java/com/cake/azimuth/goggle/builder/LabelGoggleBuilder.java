package com.cake.azimuth.goggle.builder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public class LabelGoggleBuilder {

    protected final GoggleBuilder builder;
    protected final boolean active;
    protected final GoggleBuilder.LineRef lineRef;

    public LabelGoggleBuilder(final GoggleBuilder builder, final boolean active, final GoggleBuilder.LineRef lineRef) {
        this.builder = builder;
        this.active = active;
        this.lineRef = lineRef;
    }

    public GoggleBuilder getBuilder() {
        return builder;
    }

    public LabelGoggleBuilder withLabelFormatting(final ChatFormatting color) {
        if (active) {
            lineRef.recolor(builder, color);
        }
        return this;
    }

    public LabelGoggleBuilder withLabelColor(final int rgb) {
        if (active) {
            lineRef.recolor(builder, rgb);
        }
        return this;
    }

    public LabelGoggleBuilder withLabelColor(final TextColor color) {
        if (active) {
            lineRef.recolor(builder, color);
        }
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public GoggleBuilder.LineRef getLineRef() {
        return lineRef;
    }
}