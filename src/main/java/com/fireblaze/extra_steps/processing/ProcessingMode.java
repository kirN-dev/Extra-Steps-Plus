package com.fireblaze.extra_steps.processing;

import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import net.minecraft.world.SimpleContainer;

import java.util.Optional;

public enum ProcessingMode {

    DRYING("drying"),
    BRUSHING("brushing"),
    COMPRESSING("compressing"),
    SCRAPING("scraping"),
    //SPINNING("spinning"),
    CLEANING("cleaning"),
    MIXING("mixing"),
    LYE_WATER("lye_water");

    private final String id;

    ProcessingMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ProcessingMode fromString(String id) {
        for (ProcessingMode mode : values()) {
            if (mode.id.equalsIgnoreCase(id)) {
                return mode;
            }
        }

        throw new IllegalArgumentException("Unknown processing mode: " + id);
    }
}