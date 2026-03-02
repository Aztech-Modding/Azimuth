package com.cake.azimuth.testmod;

import com.cake.azimuth.testmod.content.TestGoggleComponents;
import com.cake.azimuth.testmod.content.MagicTankBlock;
import com.cake.azimuth.testmod.content.MagicTankBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(AzimuthTestMod.MODID)
public class AzimuthTestMod {

    public static final String MODID = "azimuth_test";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<Block> MAGIC_TANK = BLOCKS.register("magic_tank",
            () -> new MagicTankBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F)));

    public static final DeferredItem<BlockItem> MAGIC_TANK_ITEM = ITEMS.register("magic_tank",
            () -> new BlockItem(MAGIC_TANK.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagicTankBlockEntity>> MAGIC_TANK_BE =
            BLOCK_ENTITY_TYPES.register("magic_tank", () -> BlockEntityType.Builder
                    .of(MagicTankBlockEntity::new, MAGIC_TANK.get())
                    .build(null));

    public AzimuthTestMod(final IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        TestGoggleComponents.register();
    }
}
