package com.cake.azimuth.registration;

import com.cake.azimuth.foundation.preconstruct.PreConstructEventHelper;
import com.cake.azimuth.registration.event.RegisterCreateBlockEditsEvent;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registry for block edits to be applied to Create blocks during registration.
 * Registrators are discovered and invoked via NeoForge scan data during Create's AllBlocks static initialization.
 */
public class CreateBlockEdits {

    private static boolean registered = false;
    private static final Map<String, Consumer<BlockBuilder<?, CreateRegistrate>>> EDITS_BY_ID = new LinkedHashMap<>();
    private static final Map<String, NonNullBiFunction<? extends Block, Item.Properties, ? extends BlockItem>> ITEM_OVERRIDES = new LinkedHashMap<>();

    public static void bootstrapIfTheBootIsNotStrapped() {
        if (registered) {
            return;
        }
        registered = true;

        PreConstructEventHelper.post(new RegisterCreateBlockEditsEvent());
    }

    public static void forBlock(final String id, final Consumer<BlockBuilder<?, CreateRegistrate>> edit) {
        EDITS_BY_ID.merge(
                id, edit, (existing, additional) -> builder -> {
                    existing.accept(builder);
                    additional.accept(builder);
                }
        );
    }

    public static <T extends Block> void forBlockItem(final String id,
                                                      final NonNullBiFunction<T, Item.Properties, ? extends BlockItem> itemFactory) {
        if (ITEM_OVERRIDES.containsKey(id)) {
            throw new IllegalStateException("An item override for block '" + id + "' has already been registered.");
        }
        ITEM_OVERRIDES.put(id, itemFactory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> NonNullBiFunction<T, Item.Properties, ? extends BlockItem> getItemOverride(final String id) {
        return (NonNullBiFunction<T, Item.Properties, ? extends BlockItem>) ITEM_OVERRIDES.get(id);
    }

    public static Consumer<BlockBuilder<?, CreateRegistrate>> getEditForId(final String id) {
        return EDITS_BY_ID.get(id);
    }

    private enum RegistrationWindow {
        NOT_STARTED,
        OPEN,
        CLOSED
    }

}
