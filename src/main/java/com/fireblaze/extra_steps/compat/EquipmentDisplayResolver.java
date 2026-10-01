package com.fireblaze.extra_steps.compat;

import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRequirements;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.LinkedHashSet;
import java.util.List;

public final class EquipmentDisplayResolver {
    private EquipmentDisplayResolver() {}

    public static List<ItemStack> resolve(ProcessingMode mode) {
        var items = new LinkedHashSet<Item>();
        for (Block block : ProcessingRequirements.equipment(mode)) {
            Item item = displayItem(block);
            if (item != Items.AIR) items.add(item);
        }
        return items.stream().map(ItemStack::new).toList();
    }

    private static Item displayItem(Block block) {
        if (block == Blocks.WATER_CAULDRON || block == ModBlocks.LYE_WATER_CAULDRON.get())
            return Blocks.CAULDRON.asItem();
        if (block == ModBlocks.WOODEN_WATER_CAULDRON.get() || block == ModBlocks.WOODEN_LYE_WATER_CAULDRON.get())
            return ModBlocks.WOODEN_CAULDRON.get().asItem();
        return block.asItem();
    }
}
