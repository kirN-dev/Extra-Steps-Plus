package com.fireblaze.extra_steps.client.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.Level;

public class WoodenCauldronColors {

    public static final BlockColor WATER_COLOR = (state, world, pos, tintIndex) -> {
        if (world instanceof Level level && pos != null) {
            FluidState fluid = world.getFluidState(pos);
            if (fluid.getType() == Fluids.WATER) {
                // Biome-Farbe korrekt holen
                return level.getBiome(pos).value().getWaterColor();
            }
        }

        // Fallback: typisches Minecraft-Wasserblau
        return 0x3F76E4;
    };
}