package com.fireblaze.extra_steps.client.color;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class GenericItemColoring implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) return 0xFFFFFF;

        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("color")) {
            return tag.getInt("color");
        }

        return 0xFFFFFF;
    }
}