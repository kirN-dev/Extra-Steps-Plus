package com.fireblaze.extra_steps.processing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Emit configured secondary outputs after a successful processing action. */
public final class ProcessingByproducts {
    private ProcessingByproducts() {}

    public static void drop(Level level, BlockPos pos, ProcessingRecipe recipe, int interaction, boolean finished) {
        if (level.isClientSide) return;
        for (var output : recipe.getScrapingSettings().byproducts) {
            if (output.appliesAt(interaction, finished) && level.random.nextFloat() < output.chance())
                Block.popResource(level, pos, output.stack().copy());
        }
    }
}
