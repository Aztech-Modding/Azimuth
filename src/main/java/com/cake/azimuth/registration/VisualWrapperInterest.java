package com.cake.azimuth.registration;

import com.cake.azimuth.foundation.preconstruct.PreConstructEventHelper;
import com.cake.azimuth.registration.event.RegisterVisualWrapperInterestEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Used to limit the impact of wrapping visuals to only the block entities that need it.
 * This is necessary because wrapping visuals is slightly dodgy, and likely incurs some form of (small but non-zero) performance penalty.
 * This is not necessary for renderers.
 */
public class VisualWrapperInterest {

    private static boolean registered = false;
    private static final List<Predicate<BlockEntityType<?>>> PENDING_TYPE_PREDICATES = new ArrayList<>();

    public static void registerInterest(final Predicate<BlockEntityType<?>> typePredicate) {
        synchronized (PENDING_TYPE_PREDICATES) {
            PENDING_TYPE_PREDICATES.add(typePredicate);
        }
    }

    public static void registerInterest(final BlockEntityType<?> interestedType) {
        synchronized (PENDING_TYPE_PREDICATES) {
            PENDING_TYPE_PREDICATES.add(type -> type == interestedType);
        }
    }

    public static boolean isInterested(final BlockEntityType<?> type) {
        synchronized (PENDING_TYPE_PREDICATES) {
            if (!registered) {
                registered = true;
                PreConstructEventHelper.post(new RegisterVisualWrapperInterestEvent());
            }

            for (final Predicate<BlockEntityType<?>> pending : PENDING_TYPE_PREDICATES) {
                if (pending.test(type)) {
                    return true;
                }
            }
            return false;
        }
    }

}
