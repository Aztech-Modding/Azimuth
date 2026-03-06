package com.cake.azimuth;

import com.cake.azimuth.behaviour.AzimuthSmartBlockEntityExtension;
import com.cake.azimuth.behaviour.SuperBlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class AzimuthEvents {

    @SubscribeEvent
    public static void onPlayerWillDestroy(final BlockEvent.BreakEvent destroyEvent) {
        final BlockPos pos = destroyEvent.getPos();
        final BlockEntity blockEntity = destroyEvent.getLevel().getBlockEntity(pos);
        if (blockEntity instanceof final AzimuthSmartBlockEntityExtension asbee) {
            for (final SuperBlockEntityBehaviour behaviour : asbee.azimuth$getSuperBehaviours()) {
                behaviour.onBlockBroken(destroyEvent);
                if (destroyEvent.isCanceled()) {
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemUseOnBlock(final PlayerInteractEvent.RightClickBlock event) {
        final BlockPos pos = event.getPos();
        final BlockEntity blockEntity = event.getLevel().getBlockEntity(pos);
        if (blockEntity instanceof final AzimuthSmartBlockEntityExtension asbee) {
            for (final SuperBlockEntityBehaviour behaviour : asbee.azimuth$getSuperBehaviours()) {
                behaviour.onItemUse(event);
                if (event.isCanceled()) {
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(final BlockEvent.EntityPlaceEvent event) {
        final BlockPos pos = event.getPos();
        final BlockEntity blockEntity = event.getLevel().getBlockEntity(pos);
        if (blockEntity instanceof final AzimuthSmartBlockEntityExtension asbee) {
            for (final SuperBlockEntityBehaviour behaviour : asbee.azimuth$getSuperBehaviours()) {
                behaviour.onBlockPlaced(event);
                if (event.isCanceled()) {
                    break;
                }
            }
        }
    }

}
