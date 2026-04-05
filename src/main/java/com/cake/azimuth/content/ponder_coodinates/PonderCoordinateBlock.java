package com.cake.azimuth.content.ponder_coodinates;

import com.cake.azimuth.content.AzContent;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PonderCoordinateBlock extends Block implements IBE<PonderCoordinateBlockEntity> {

    public PonderCoordinateBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(final @NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AXIS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(BlockStateProperties.AXIS, context.getClickedFace().getAxis());
    }

    @Override
    protected boolean propagatesSkylightDown(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getCollisionShape(final BlockState state,
                                           final BlockGetter level,
                                           final BlockPos pos,
                                           final CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getInteractionShape(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected BlockState updateShape(final BlockState state,
                                     final Direction direction,
                                     final BlockState neighborState,
                                     final LevelAccessor level,
                                     final BlockPos pos,
                                     final BlockPos neighborPos) {
        this.withBlockEntityDo(level, pos, PonderCoordinateBlockEntity::updateAxisPoint);
        return state;
    }

    @Override
    public Class<PonderCoordinateBlockEntity> getBlockEntityClass() {
        return PonderCoordinateBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PonderCoordinateBlockEntity> getBlockEntityType() {
        return AzContent.PONDER_COORDINATE_BLOCK_ENTITY.get();
    }
}
