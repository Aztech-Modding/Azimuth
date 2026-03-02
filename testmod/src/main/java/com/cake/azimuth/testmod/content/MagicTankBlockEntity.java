package com.cake.azimuth.testmod.content;

import com.cake.azimuth.goggle.api.IBuildGoggleInformation;
import com.cake.azimuth.goggle.builder.GoggleBuilder;
import com.cake.azimuth.testmod.AzimuthTestMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MagicTankBlockEntity extends BlockEntity implements IBuildGoggleInformation {

    private boolean manaEnabled = true;
    private int mana = 50;
    private final double manaCapacity = 1000;
    private int ticks;

    public MagicTankBlockEntity(final BlockPos pos, final BlockState state) {
        super(AzimuthTestMod.MAGIC_TANK_BE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        ticks++;
        if (ticks % 100 == 0) {
            manaEnabled = !manaEnabled;
        }
        mana = (mana + 7) % 1000;
    }

    @Override
    public String getModId() {
        return AzimuthTestMod.MODID;
    }

    @Override
    public void buildGoggleStructure(final GoggleBuilder builder) {
        builder.conditional(() -> this.manaEnabled)
                .section(TestGoggleComponents.SECTION_MAGIC)
                .statistic(TestGoggleComponents.STAT_MANA, this.mana, TestGoggleComponents.MANA_UNITS);

        builder.section("mana_bar", "Mana Bar:")
                .statistic("mana_capacity", "Mana capacity:", this.mana, this.manaCapacity, TestGoggleComponents.MANA_BAR);

        builder.isNotSneaking()
                .label("sneak_hint", "Player isn't sneaky enough to see what I have to say");

        builder.isSneaking()
                .label("sneak_info", "I think hamburgers and cheeseburgers go well with fries. Or do they?");
    }
}
