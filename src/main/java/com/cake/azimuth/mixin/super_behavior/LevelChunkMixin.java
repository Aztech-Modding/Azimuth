package com.cake.azimuth.mixin.super_behavior;

import com.cake.azimuth.behaviour.AzimuthSmartBlockEntityExtension;
import com.cake.azimuth.behaviour.SuperBlockEntityBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This is cursed, fuck...
 *
 */
@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Shadow
    @Final
    Level level;

    @WrapOperation(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
    private void azimuth$beforeBlockEntityRemoved(final LevelChunk instance,
                                                  final BlockPos pos,
                                                  final Operation<Void> original,
                                                  @Local(argsOnly = true) final boolean isMoving) {
        final BlockEntity blockEntity = this.level.getBlockEntity(pos);
        if (blockEntity instanceof final AzimuthSmartBlockEntityExtension asbee) {
            for (final SuperBlockEntityBehaviour sbeb : asbee.azimuth$getSuperBehaviours()) {
                sbeb.removeFromLevel(isMoving);
            }
        }
        original.call(instance, pos);
    }

}
