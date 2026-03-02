package com.cake.azimuth.goggle.builder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public class StatisticGoggleBuilder extends LabelGoggleBuilder {

    protected final Object value;
    protected final Object maxValue;
    protected final GoggleBuilder.LineRef statLineRef;
    protected final StatisticDisplay display = createDisplay();

    public StatisticGoggleBuilder(final GoggleBuilder builder,
                                  final boolean active,
                                  final GoggleBuilder.LineRef labelLineRef,
                                  final GoggleBuilder.LineRef statLineRef,
                                  final Object value,
                                  final Object maxValue) {
        super(builder, active, labelLineRef);
        this.statLineRef = statLineRef;
        this.value = value;
        this.maxValue = maxValue;
    }

    protected StatisticDisplay createDisplay() {
        return new StatisticDisplay();
    }

    public StatisticDisplay getStatisticDisplay() {
        return display;
    }

    public Object getValue() {
        return value;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public GoggleBuilder.LineRef getStatLineRef() {
        return statLineRef;
    }

    public class StatisticDisplay {

        public StatisticDisplay withStatisticColor(final ChatFormatting color) {
            if (active) {
                statLineRef.recolor(builder, color);
            }
            return this;
        }

        public StatisticDisplay withStatisticColor(final int rgb) {
            if (active) {
                statLineRef.recolor(builder, rgb);
            }
            return this;
        }

        public StatisticDisplay withStatisticColor(final TextColor color) {
            if (active) {
                statLineRef.recolor(builder, color);
            }
            return this;
        }

        public StatisticDisplay appendValueSuffix(final Component component) {
            if (active) {
                statLineRef.append(builder, component);
            }
            return this;
        }

        public void refreshLine() {
            if (active) {
                statLineRef.refresh(builder);
            }
        }

        public double normalizedRatio() {
            if (!(value instanceof Number first) || !(maxValue instanceof Number second)) {
                return 0D;
            }
            final double denominator = second.doubleValue();
            if (denominator == 0) {
                return 0D;
            }
            final double ratio = first.doubleValue() / denominator;
            return Math.max(0D, Math.min(1D, ratio));
        }

        public MutableComponent currentLineCopy() {
            return statLineRef.line.copy();
        }

        protected void replaceCurrentLine(final MutableComponent line) {
            statLineRef.line = line;
        }
    }
}