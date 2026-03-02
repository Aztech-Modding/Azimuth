package com.cake.azimuth.goggle.style;

import com.cake.azimuth.goggle.builder.StatisticGoggleBuilder;
import net.minecraft.network.chat.Component;

public class BarChartGoggleBuilder extends StatisticGoggleBuilder {

    private final int width;
    private final Component baseLine;

    public BarChartGoggleBuilder(final StatisticGoggleBuilder base, final int width) {
        super(base.getBuilder(), base.isActive(), base.getLineRef(), base.getStatLineRef(), base.getValue(), base.getMaxValue());
        this.width = width;
        this.baseLine = getStatisticDisplay().currentLineCopy();
        getStatisticDisplay().refreshLine();
    }

    @Override
    protected StatisticDisplay createDisplay() {
        return new BarChartDisplay();
    }

    @Override
    public BarChartDisplay getStatisticDisplay() {
        return (BarChartDisplay) super.getStatisticDisplay();
    }

    public class BarChartDisplay extends StatisticDisplay {
        private int barColor = 0xFF55FF;
        private int backgroundBarColor = 0x444444;

        public BarChartDisplay withBarColor(final int color) {
            this.barColor = color;
            refreshLine();
            return this;
        }

        public BarChartDisplay withBackgroundBarColor(final int color) {
            this.backgroundBarColor = color;
            refreshLine();
            return this;
        }

        @Override
        public void refreshLine() {
            if (!active) {
                return;
            }
            if (baseLine == null) {
                return;
            }
            replaceCurrentLine(baseLine.copy());
            final double ratio = normalizedRatio();
            final int filled = (int) Math.round(width * ratio);
            final int empty = Math.max(0, width - filled);

            final Component barPrimary = Component.literal(" " + "|".repeat(Math.max(0, filled)))
                    .withStyle(style -> style.withColor(barColor));
            appendValueSuffix(barPrimary);
            final Component barSecondary = Component.literal("|".repeat(empty))
                    .withStyle(style -> style.withColor(backgroundBarColor));
            appendValueSuffix(barSecondary);
        }
    }
}