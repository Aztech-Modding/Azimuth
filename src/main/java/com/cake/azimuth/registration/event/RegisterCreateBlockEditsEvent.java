package com.cake.azimuth.registration.event;

import com.cake.azimuth.foundation.preconstruct.IPreConstructEvent;
import com.cake.azimuth.registration.CreateBlockEdits;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

public class RegisterCreateBlockEditsEvent extends Event implements IPreConstructEvent {

    public void forBlock(final String id, final Consumer<BlockBuilder<?, CreateRegistrate>> edit) {
        CreateBlockEdits.forBlock(id, edit);
    }

    public <T extends Block> void forBlockItem(final String id,
                                               final NonNullBiFunction<T, Item.Properties, ? extends BlockItem> itemFactory) {
        CreateBlockEdits.forBlockItem(id, itemFactory);
    }

}
