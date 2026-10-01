package com.fireblaze.extra_steps.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/** Viewer-independent snapshot built after recipes and tags have loaded. */
public record ProcessingRecipeDisplay(List<List<ItemStack>> inputs, List<ItemStack> equipment,
        List<Component> equipmentTooltip, List<ItemStack> tools, ItemStack primaryOutput,
        List<Component> primaryTooltip, List<SecondaryOutput> secondaryOutputs,
        List<Component> actionTooltip) {
    public record SecondaryOutput(ItemStack stack, List<Component> tooltip) {}
}
