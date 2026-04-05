package com.cake.azimuth.content.ponder_coodinates;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PonderCoordinateRenderer extends SmartBlockEntityRenderer<PonderCoordinateBlockEntity> {

    public PonderCoordinateRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(final PonderCoordinateBlockEntity blockEntity,
                              final float partialTicks,
                              final PoseStack ms,
                              final MultiBufferSource buffer,
                              final int light,
                              final int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.scale(1 / 16f, -1 / 16f, 1 / 16f);

        final Font font = Minecraft.getInstance().font;

        font.drawInBatch(
                String.valueOf(blockEntity.getAxisPoint()), 0f, 0f, 0xFFFFFF, false, ms.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL, 0x00000000,
                LightTexture.FULL_BRIGHT
        );

        ms.pushPose();
        ms.scale(1 / 2f, 1 / 2f, 1 / 2f);
        ms.translate(-6, 0, 0);
        font.drawInBatch(
                blockEntity.getBlockState().getValue(BlockStateProperties.AXIS).name(),
                0f,
                0f,
                0xFFFFFF,
                false,
                ms.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0x00000000,
                LightTexture.FULL_BRIGHT
        );
        ms.popPose();

        TransformStack.of(ms)
                .rotateYDegrees(180);

        font.drawInBatch(
                String.valueOf(blockEntity.getAxisPoint()), 0f, 0f, 0xFFFFFF, false, ms.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL, 0x00000000,
                LightTexture.FULL_BRIGHT
        );

        ms.pushPose();
        ms.scale(1 / 2f, 1 / 2f, 1 / 2f);
        ms.translate(-6, 0, 0);
        font.drawInBatch(
                blockEntity.getBlockState().getValue(BlockStateProperties.AXIS).name(),
                0f,
                0f,
                0xFFFFFF,
                false,
                ms.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0x00000000,
                LightTexture.FULL_BRIGHT
        );
        ms.popPose();
        ms.popPose();
    }
}
