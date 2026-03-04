package com.cake.azimuth.advancement;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.*;

public class AzimuthAdvancementBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<AzimuthAdvancementBehaviour> TYPE = new BehaviourType<>();

    private UUID playerId;
    private final Set<AzimuthAdvancement> advancements;

    public static void create(final List<BlockEntityBehaviour> behaviours, final SmartBlockEntity be, final AzimuthAdvancement... advancements) {
        final AzimuthAdvancementBehaviour existing = (AzimuthAdvancementBehaviour) behaviours.stream()
                .filter(blockEntityBehaviour -> blockEntityBehaviour instanceof AzimuthAdvancementBehaviour)
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.add(advancements);
        } else {
            behaviours.add(new AzimuthAdvancementBehaviour(be, advancements));
        }
    }

    public AzimuthAdvancementBehaviour(final SmartBlockEntity be, final AzimuthAdvancement... advancements) {
        super(be);
        this.advancements = new HashSet<>();
        add(advancements);
    }

    public void add(final AzimuthAdvancement... advancements) {
        this.advancements.addAll(Arrays.asList(advancements));
    }

    public boolean isOwnerPresent() {
        return playerId != null;
    }

    public void setPlayer(final UUID id) {
        final Player player = getWorld().getPlayerByUUID(id);
        if (player == null) {
            return;
        }
        playerId = id;
        removeAwarded();
        blockEntity.setChanged();
    }

    @Override
    public void initialize() {
        super.initialize();
        removeAwarded();
    }

    private void removeAwarded() {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        advancements.removeIf(c -> c.isAlreadyAwardedTo(player));
        if (advancements.isEmpty()) {
            playerId = null;
            blockEntity.setChanged();
        }
    }

    public void awardPlayerIfNear(final AzimuthAdvancement advancement, final int maxDistance) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        if (player.distanceToSqr(Vec3.atCenterOf(getPos())) > maxDistance * maxDistance) {
            return;
        }
        award(advancement, player);
    }

    public void awardPlayer(final AzimuthAdvancement advancement) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        award(advancement, player);
    }

    private void award(final AzimuthAdvancement advancement, final Player player) {
        if (advancements.contains(advancement)) {
            advancement.awardTo(player);
        }
        removeAwarded();
    }

    private Player getPlayer() {
        if (playerId == null) {
            return null;
        }
        return getWorld().getPlayerByUUID(playerId);
    }

    @Override
    public void write(final CompoundTag nbt, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (playerId != null) {
            nbt.putUUID("Owner", playerId);
        }
    }

    @Override
    public void read(final CompoundTag nbt, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("Owner")) {
            playerId = nbt.getUUID("Owner");
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public static void tryAward(final BlockGetter reader, final BlockPos pos, final AzimuthAdvancement advancement) {
        final AzimuthAdvancementBehaviour behaviour = BlockEntityBehaviour.get(reader, pos, TYPE);
        if (behaviour != null) {
            behaviour.awardPlayer(advancement);
        }
    }

    public static void setPlacedBy(final Level worldIn, final BlockPos pos, final LivingEntity placer) {
        final AzimuthAdvancementBehaviour behaviour = BlockEntityBehaviour.get(worldIn, pos, TYPE);
        if (behaviour == null) {
            return;
        }
        if (placer instanceof FakePlayer) {
            return;
        }
        if (placer instanceof ServerPlayer) {
            behaviour.setPlayer(placer.getUUID());
        }
    }
}
