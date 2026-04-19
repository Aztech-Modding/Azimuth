package com.cake.azimuth.goggle.builder;

import com.cake.azimuth.foundation.config.AzimuthConfigs;
import com.cake.azimuth.foundation.lang.AzimuthGeneratedLangEntry;
import com.cake.azimuth.goggle.component.GoggleComponent;
import com.cake.azimuth.goggle.style.StatisticStyle;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record GoggleBuilder(
        List<Component> tooltip,
        boolean isPlayerSneaking,
        int initialTooltipSize,
        String modId,
        boolean datagen,
        int indentLevel,
        boolean active,
        GoggleBuilder parent
) {

    public GoggleBuilder(final List<Component> tooltip, final boolean isPlayerSneaking, final String modId) {
        this(tooltip, isPlayerSneaking, tooltip.size(), modId, false, 0, true, null);
    }

    public static GoggleBuilder datagen(final String modId) {
        return new GoggleBuilder(new ArrayList<>(), false, 0, modId, true, 0, true, null);
    }

    public GoggleBuilder section(final GoggleComponent component) {
        if (!this.active) {
            return this;
        }
        final LineRef lineRef = this.appendLine(
                this.renderComponent(component).copy().withStyle(ChatFormatting.WHITE),
                this.indentLevel
        );
        lineRef.refresh(this);
        return this.withIndent(1);
    }

    public GoggleBuilder section(final String keySuffix, final String defaultEnglish) {
        final GoggleComponent component = this.anonymousComponent(keySuffix, defaultEnglish);
        return this.section(component);
    }

    public LabelGoggleBuilder label(final GoggleComponent component) {
        if (!this.active) {
            return new LabelGoggleBuilder(this, false, LineRef.empty());
        }
        final LineRef lineRef = this.appendLine(
                this.renderComponent(component).copy().withStyle(ChatFormatting.GRAY),
                this.indentLevel
        );
        lineRef.refresh(this);
        return new LabelGoggleBuilder(this, true, lineRef);
    }

    public LabelGoggleBuilder label(final String keySuffix, final String defaultEnglish) {
        return this.label(this.anonymousComponent(keySuffix, defaultEnglish));
    }

    public StatisticGoggleBuilder statistic(final GoggleComponent component, final Object value) {
        return this.statistic(component, value, null);
    }

    public <T extends StatisticGoggleBuilder> T statistic(final GoggleComponent component,
                                                          final Object value,
                                                          final StatisticStyle<T> style) {
        return this.statisticInternal(component, value, null, style);
    }

    public <T extends StatisticGoggleBuilder> T statistic(final GoggleComponent component,
                                                          final Object current,
                                                          final Object max,
                                                          final StatisticStyle<T> style) {
        return this.statisticInternal(component, current, max, style);
    }

    public StatisticGoggleBuilder statistic(final String keySuffix, final String defaultEnglish, final Object value) {
        return this.statistic(this.anonymousComponent(keySuffix, defaultEnglish), value, null);
    }

    public <T extends StatisticGoggleBuilder> T statistic(final String keySuffix,
                                                          final String defaultEnglish,
                                                          final Object value,
                                                          final StatisticStyle<T> style) {
        return this.statistic(this.anonymousComponent(keySuffix, defaultEnglish), value, style);
    }

    public <T extends StatisticGoggleBuilder> T statistic(final String keySuffix,
                                                          final String defaultEnglish,
                                                          final Object current,
                                                          final Object max,
                                                          final StatisticStyle<T> style) {
        return this.statistic(this.anonymousComponent(keySuffix, defaultEnglish), current, max, style);
    }

    public GoggleBuilder withRenderCondition(final boolean condition) {
        final boolean newActive = this.active && (this.datagen || condition);
        return new GoggleBuilder(
                this.tooltip,
                this.isPlayerSneaking,
                this.initialTooltipSize,
                this.modId,
                this.datagen,
                this.indentLevel,
                newActive,
                this
        );
    }

    public GoggleBuilder conditional(final Supplier<Boolean> condition) {
        return this.withRenderCondition(condition.get());
    }

    public GoggleBuilder isSneaking() {
        return this.withRenderCondition(this.isPlayerSneaking);
    }

    public GoggleBuilder isNotSneaking() {
        return this.withRenderCondition(!this.isPlayerSneaking);
    }

    public GoggleBuilder endConditional() {
        return this.parent != null ? this.parent : this;
    }

    public GoggleBuilder withIndent(final int level) {
        return new GoggleBuilder(
                this.tooltip, this.isPlayerSneaking, this.initialTooltipSize,
                this.modId, this.datagen, level, this.active,
                this.parent
        );
    }

    public List<Component> getRawTooltip() {
        return this.tooltip;
    }

    public boolean hasAppendedData() {
        return this.tooltip.size() > this.initialTooltipSize;
    }

    public MutableComponent renderComponent(final GoggleComponent component) {
        final String key = Objects.requireNonNull(component.fullTranslationKey());
        if (AzimuthConfigs.tooltipBuilderDebugEnabled()) {
            return component.defaultEnglish() != null ? Component.literal(component.defaultEnglish()) : Component.translatable(
                    key);
        }
        return Component.translatable(key);
    }

    private GoggleComponent anonymousComponent(final String keySuffix, final String defaultEnglish) {
        AzimuthGeneratedLangEntry.registerAnonymous(this.modId, keySuffix, defaultEnglish);
        return GoggleComponent.scoped(this.modId, keySuffix, defaultEnglish);
    }

    private LineRef appendLine(final MutableComponent content, final int indent) {
        final MutableComponent wrapped = this.wrapForGoggles(content, indent);
        this.tooltip.add(wrapped);
        return new LineRef(this.tooltip.size() - 1, indent, content);
    }

    private MutableComponent wrapForGoggles(final Component content, final int indent) {
        if (this.datagen) {
            return Component.literal(" ".repeat(Math.max(0, 4 + indent))).append(content.copy());
        }
        final List<MutableComponent> temp = new ArrayList<>(1);
        Lang.builder(Objects.requireNonNull(this.modId)).add(Objects.requireNonNull(content)).forGoggles(temp, indent);
        return temp.getFirst();
    }

    @SuppressWarnings("unchecked")
    private <T extends StatisticGoggleBuilder> T statisticInternal(final GoggleComponent component,
                                                                   final Object value,
                                                                   final Object maxValue,
                                                                   final StatisticStyle<T> style) {
        if (!this.active) {
            return (T) new StatisticGoggleBuilder(this, false, LineRef.empty(), LineRef.empty(), value, maxValue);
        }

        final MutableComponent label = this.renderComponent(component).copy().withStyle(ChatFormatting.GRAY);
        final LineRef labelLineRef = this.appendLine(label, this.indentLevel);

        final MutableComponent valueComponent = this.toValueComponent(value).withStyle(ChatFormatting.AQUA);
        final MutableComponent lineComponent = Component.empty().append(Objects.requireNonNull(valueComponent));

        if (maxValue != null) {
            lineComponent.append(Objects.requireNonNull(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY)));
            lineComponent.append(Objects.requireNonNull(this.toValueComponent(maxValue).withStyle(ChatFormatting.DARK_GRAY)));
        }

        final LineRef statLineRef = this.appendLine(lineComponent, this.indentLevel + 1);
        final StatisticGoggleBuilder statisticBuilder = new StatisticGoggleBuilder(
                this,
                true,
                labelLineRef,
                statLineRef,
                value,
                maxValue
        );
        if (style == null) {
            return (T) statisticBuilder;
        }
        return style.apply(statisticBuilder);
    }

    private MutableComponent toValueComponent(final Object value) {
        if (value instanceof final MutableComponent mutable) {
            return mutable.copy();
        }
        if (value instanceof final Component component) {
            return component.copy();
        }
        if (value instanceof final ItemStack stack) {
            final MutableComponent itemLine = Component.literal(stack.getCount() + "x ");
            itemLine.append(Objects.requireNonNull(stack.getHoverName().copy()));
            return itemLine;
        }
        if (value instanceof final Boolean bool) {
            return Component.literal(bool ? "Yes" : "No");
        }
        if (value instanceof final Double d) {
            return Component.literal(Objects.requireNonNull(LangNumberFormat.format(d)));
        }
        if (value instanceof final Float f) {
            return Component.literal(Objects.requireNonNull(LangNumberFormat.format(f)));
        }
        if (value instanceof final Number number) {
            return Component.literal(Objects.requireNonNull(LangNumberFormat.format(number.doubleValue())));
        }
        return Component.literal(Objects.requireNonNull(String.valueOf(value)));
    }

    public static class LineRef {
        public final int tooltipIndex;
        public final int indent;
        public MutableComponent line;

        public LineRef(final int tooltipIndex, final int indent, final MutableComponent line) {
            this.tooltipIndex = tooltipIndex;
            this.indent = indent;
            this.line = line;
        }

        public static LineRef empty() {
            return new LineRef(-1, 0, Component.empty());
        }

        public boolean exists() {
            return this.tooltipIndex >= 0;
        }

        public void refresh(final GoggleBuilder builder) {
            if (!this.exists()) {
                return;
            }
            builder.tooltip().set(this.tooltipIndex, builder.wrapForGoggles(this.line, this.indent));
        }

        public void recolor(final GoggleBuilder builder, final Style style) {
            if (!this.exists()) {
                return;
            }
            this.line = this.line.copy().withStyle(existing -> existing.applyTo(Objects.requireNonNull(style)));
            this.refresh(builder);
        }

        public void recolor(final GoggleBuilder builder, final ChatFormatting color) {
            this.recolor(builder, Style.EMPTY.applyFormat(Objects.requireNonNull(color)));
        }

        public void recolor(final GoggleBuilder builder, final int rgb) {
            this.recolor(builder, Style.EMPTY.withColor(TextColor.fromRgb(rgb)));
        }

        public void recolor(final GoggleBuilder builder, final TextColor color) {
            this.recolor(builder, Style.EMPTY.withColor(color));
        }

        public void append(final GoggleBuilder builder, final Component component) {
            this.line.append(Objects.requireNonNull(component));
            this.refresh(builder);
        }
    }
}