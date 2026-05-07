package com.fireblaze.extra_steps.compat;

import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class JEIHelper {
    public static ItemStack getIconForMode(ProcessingMode mode) {
        return switch (mode) {
            case DRYING, SCRAPING -> new ItemStack(ModBlocks.DRYING_RACK.get());
            case BRUSHING -> new ItemStack(ModItems.WOOL_BRUSH.get());
            case COMPRESSING -> new ItemStack(ModBlocks.BASKET.get());
            case MIXING -> new ItemStack(Blocks.CAULDRON);
            //case SPINNING -> new ItemStack(ModBlocks.SPINNING_WHEEL.get());
            case CLEANING, LYE_WATER -> new ItemStack(Blocks.CAULDRON);
        };
    }
}
