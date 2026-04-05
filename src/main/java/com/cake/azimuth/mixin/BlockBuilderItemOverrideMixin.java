package com.cake.azimuth.mixin;

import com.cake.azimuth.registration.CreateBlockEdits;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBuilder.class, remap = false)
public abstract class BlockBuilderItemOverrideMixin<T extends Block, P> {

    @Inject(method = "item()Lcom/tterrag/registrate/builders/ItemBuilder;", at = @At("HEAD"), cancellable = true, remap = false)
    private void azimuth$applyItemOverride(final CallbackInfoReturnable<ItemBuilder<BlockItem, BlockBuilder<T, P>>> cir) {
        @SuppressWarnings("unchecked")
        final BlockBuilder<T, P> self = (BlockBuilder<T, P>) (Object) this;
        final NonNullBiFunction<T, Item.Properties, ? extends BlockItem> factory = CreateBlockEdits.getItemOverride(self.getName());
        if (factory != null) {
            cir.setReturnValue(self.item(factory));
        }
    }

}
