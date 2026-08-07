package com.cake.azimuth.mixin.fix;

import com.google.common.collect.Table;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Fuck it we mixin
 * TODO: PR and create a mixin plugin to disable on fixed versions
 */
@Mixin(OneTimeEventReceiver.class)
public class RegistrateOTERFix {
    @Mutable
    @Shadow(remap = false)
    private static boolean seenModBus;

    @Final
    @Shadow(remap = false)
    private static Table<AbstractRegistrate<?>, Class<?>, List<Pair<EventPriority, Consumer<?>>>> waitingModListeners;

    @Shadow
    @Final
    private static List<Triple<IEventBus, Object, Class<? extends Event>>> toUnregister;

    @Unique
    private static final Set<AbstractRegistrate<?>> azimuth$flushedOwners = new HashSet<>();

    @Inject(
            method = "addModListener(Lcom/tterrag/registrate/AbstractRegistrate;Lnet/neoforged/bus/api/EventPriority;Ljava/lang/Class;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            remap = false
    )
    private static <T extends Event & IModBusEvent> void azimuth$flushPerOwner(
            final AbstractRegistrate<?> owner,
            final EventPriority priority,
            final Class<? super T> evtClass,
            final Consumer<? super T> listener,
            final CallbackInfo ci
    ) {
        if (owner.getModEventBus() != null && !azimuth$flushedOwners.contains(owner)) {
            azimuth$flushedOwners.add(owner);
            seenModBus = true;

            for (final Map.Entry<Class<?>, List<Pair<EventPriority, Consumer<?>>>> waitingListener : waitingModListeners.row(
                    owner).entrySet()) {
                for (final Pair<EventPriority, Consumer<?>> pair : waitingListener.getValue()) {
                    @SuppressWarnings("unchecked") final Class<? super Event> cls = (Class<? super Event>) waitingListener.getKey();
                    @SuppressWarnings("unchecked") final Consumer<? super Event> cons = (Consumer<? super Event>) pair.getValue();
                    OneTimeEventReceiver.addListener(owner.getModEventBus(), pair.getKey(), cls, cons);
                }
            }

            OneTimeEventReceiver.addModListener(
                    owner,
                    FMLLoadCompleteEvent.class,
                    RegistrateOTERFix::azimuth$onLoadCompleteMirrored
            );
        }
    }

    @Unique
    private static void azimuth$onLoadCompleteMirrored(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            toUnregister.forEach(t -> t.getLeft().unregister(t.getMiddle()));
            toUnregister.clear();
        });
    }

}