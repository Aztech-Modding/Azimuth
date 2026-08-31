package com.cake.azimuth.behaviour.render;

import com.cake.azimuth.behaviour.SuperBlockEntityBehaviour;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class BlockEntityBehaviourRenderer<T extends SmartBlockEntity> {

    @SuppressWarnings("unchecked")
    public void castRenderSafe(final SuperBlockEntityBehaviour behaviour,
                               final SmartBlockEntity blockEntity,
                               final float partialTicks,
                               final PoseStack ms,
                               final MultiBufferSource buffer,
                               final int light,
                               final int overlay) {
        //TODO: hard requirement for the class match, generics are being erased
        try {
            this.renderSafe(behaviour, (T) blockEntity, partialTicks, ms, buffer, light, overlay);
        } catch (final ClassCastException e) {
            throw new ClassCastException(
                    "BlockEntityBehaviourRenderer expected a block entity of a certain type, but got " +
                            blockEntity.getClass() +
                            ", which was not within the bounds of this (" + this + ") renderer!" +
                            " If possible, try find a way to exclude this block from the behaviour (like adding the forbidden tag for its use as a chain drive component for chain behaviours in bits n bobs)");
        }
    }

    public void renderSafe(final SuperBlockEntityBehaviour behaviour,
                           final T blockEntity,
                           final float partialTicks,
                           final PoseStack ms,
                           final MultiBufferSource buffer,
                           final int light,
                           final int overlay) {
    }

}
