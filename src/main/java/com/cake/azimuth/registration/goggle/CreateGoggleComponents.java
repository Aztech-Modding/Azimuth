package com.cake.azimuth.registration.goggle;

import com.cake.azimuth.goggle.component.GoggleBuilderHelper;
import com.cake.azimuth.goggle.component.GoggleComponent;

public class CreateGoggleComponents {

    private static final GoggleBuilderHelper HELPER = new GoggleBuilderHelper("create");

    public static void register() {
    }

    public static final class Kinetic {
        public static final GoggleComponent STRESS_IMPACT = HELPER.component("create.tooltip.stressImpact");
        public static final GoggleComponent CAPACITY_PROVIDED = HELPER.component("create.tooltip.capacityProvided");
        public static final GoggleComponent AT_CURRENT_SPEED = HELPER.component("create.gui.goggles.at_current_speed");
        public static final GoggleComponent SU = HELPER.component("create.generic.unit.stress");

        private Kinetic() {
        }
    }

    public static final class Generic {
        public static final GoggleComponent RPM = HELPER.component("create.generic.unit.rpm");
        public static final GoggleComponent MB = HELPER.component("create.generic.unit.millibuckets");

        private Generic() {
        }
    }
}