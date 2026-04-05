package com.cake.azimuth.content.ponder_coodinates;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class PonderCoordinateBlockEntity extends SmartBlockEntity {

    private int axisPoint = 0;

    public PonderCoordinateBlockEntity(final BlockEntityType<?> type,
                                       final BlockPos pos,
                                       final BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(final List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void initialize() {
        super.initialize();
        this.updateAxisPoint();
    }

    public int getAxisPoint() {
        return this.axisPoint;
    }

    public void updateAxisPoint() {
        //Find a block -ve to the blockstate axis, if matching axis this = +1
        final Direction.Axis axis = this.getBlockState().getValue(BlockStateProperties.AXIS);
        final BlockPos otherPos = this.getBlockPos().relative(axis, -1);
        final BlockState other = this.level.getBlockState(otherPos);
        this.axisPoint = 0;
        if (other.getBlock() instanceof PonderCoordinateBlock) {
            final BlockEntity entity = this.level.getBlockEntity(otherPos);
            if (entity instanceof final PonderCoordinateBlockEntity pcbe) {
                final int neededCoordinate = other.getValue(BlockStateProperties.AXIS) == axis ? pcbe.axisPoint + 1 : other.getValue(
                        BlockStateProperties.AXIS) == Direction.Axis.Y ? 0 : 1;
                if (this.axisPoint != neededCoordinate) {
                    this.axisPoint = neededCoordinate;
                    this.sendData();
                    //Look for block entity after to update
                    final BlockPos adjacentPos = this.getBlockPos().relative(axis, 1);
                    if (this.level.getBlockEntity(adjacentPos) instanceof final PonderCoordinateBlockEntity pcbe2) {
                        pcbe2.updateAxisPoint();
                    }
                }
            }
        }
    }

    @Override
    protected void write(final CompoundTag tag, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("AxisPoint", this.axisPoint);
    }

    @Override
    protected void read(final CompoundTag tag, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.axisPoint = tag.getInt("AxisPoint");
    }
}
