package com.cake.azimuth.testmod.content;

import com.cake.azimuth.testmod.AzimuthTestMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MagicTankBlock extends BaseEntityBlock {

    public static final MapCodec<MagicTankBlock> CODEC = simpleCodec(MagicTankBlock::new);

    public MagicTankBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return new MagicTankBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level,
                                                                  final @NotNull BlockState state,
                                                                  final @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, AzimuthTestMod.MAGIC_TANK_BE.get(),
                (lvl, pos, blockState, be) -> be.tick());
    }
}
