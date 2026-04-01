package com.cake.azimuth.content;

import com.cake.azimuth.Azimuth;
import com.cake.azimuth.content.ponder_coodinates.PonderCoordinateBlock;
import com.cake.azimuth.content.ponder_coodinates.PonderCoordinateBlockEntity;
import com.cake.azimuth.content.ponder_coodinates.PonderCoordinateRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class AzContent {

    public static final BlockEntry<PonderCoordinateBlock> PONDER_COORDINATE = Azimuth.REGISTRATE.block(
                    "ponder_coordinate",
                    PonderCoordinateBlock::new
            )
            .properties(BlockBehaviour.Properties::noOcclusion)
            .blockstate((ctx, prov) -> prov.simpleBlock(
                    ctx.get(),
                    prov.models().getExistingFile(Azimuth.asResource(
                            "block/ponder_coordinate"))
            ))
            .item()
            .build()
            .register();

    public static final BlockEntityEntry<PonderCoordinateBlockEntity> PONDER_COORDINATE_BLOCK_ENTITY = Azimuth.REGISTRATE.blockEntity(
                    "ponder_coordinate",
                    PonderCoordinateBlockEntity::new
            )
            .validBlocks(PONDER_COORDINATE)
            .renderer(() -> PonderCoordinateRenderer::new)
            .register();

    public static void register() {
    }

}
