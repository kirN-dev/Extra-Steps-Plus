package com.fireblaze.extra_steps.processing;

import com.fireblaze.extra_steps.registry.ModTags;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.List;

/** Rules shared by processing handlers and recipe viewers. */
public final class ProcessingRequirements {
    private ProcessingRequirements() {}

    public static boolean isMixingTool(ItemStack stack) {
        return stack.is(ModTags.Items.MIXING_TOOLS);
    }

    public static boolean isMixingCauldron(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(ModTags.Blocks.VALID_MIXING_CAULDRONS);
    }

    public static List<Block> equipment(ProcessingMode mode) {
        if (mode == ProcessingMode.MIXING) {
            return BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.VALID_MIXING_CAULDRONS)
                    .map(tag -> tag.stream().map(holder -> holder.value()).toList()).orElse(List.of());
        }
        return switch (mode) {
            case CLEANING -> List.of(ModBlocks.LYE_WATER_CAULDRON.get(), ModBlocks.WOODEN_LYE_WATER_CAULDRON.get());
            case LYE_WATER -> List.of(Blocks.WATER_CAULDRON, ModBlocks.WOODEN_WATER_CAULDRON.get());
            case COMPRESSING -> List.of(ModBlocks.BASKET.get());
            default -> List.of(ModBlocks.DRYING_RACK.get());
        };
    }
}
