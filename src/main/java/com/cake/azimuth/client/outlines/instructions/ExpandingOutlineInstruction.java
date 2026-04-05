package com.cake.azimuth.client.outlines.instructions;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.world.phys.Vec3;

public class ExpandingOutlineInstruction extends PonderInstruction {

    public ExpandingOutlineInstruction(final PonderPalette palette, final Vec3 from, final Vec3 to,
                                       final int expandTicks, final int holdTicks) {
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void tick(final PonderScene scene) {
    }
}
