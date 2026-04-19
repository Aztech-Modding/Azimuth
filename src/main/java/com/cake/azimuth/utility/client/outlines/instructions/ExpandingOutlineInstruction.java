package com.cake.azimuth.utility.client.outlines.instructions;

import com.cake.azimuth.utility.client.outlines.ExpandingLineOutline;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.world.phys.Vec3;

public class ExpandingOutlineInstruction extends TickingInstruction {

    private final PonderPalette color;

    private final ExpandingLineOutline expandingOutlineInstruction = new ExpandingLineOutline();

    public ExpandingOutlineInstruction(final PonderPalette color,
                                       final Vec3 start,
                                       final Vec3 end,
                                       final int ticks,
                                       final int growingTicks) {
        super(false, ticks);
        this.color = color;
        this.expandingOutlineInstruction
                .setGrowingTicks(growingTicks)
                .set(start, end);
    }

    @Override
    protected void firstTick(final PonderScene scene) {
        super.firstTick(scene);
        this.expandingOutlineInstruction.setGrowingTicksElapsed(0);
    }

    @Override
    public void tick(final PonderScene scene) {
        super.tick(scene);
        this.expandingOutlineInstruction.tickGrowingTicksElapsed();
        scene.getOutliner()
                .showOutline(this, this.expandingOutlineInstruction)
                .lineWidth(1 / 16f)
                .colored(this.color.getColor());
    }

}
