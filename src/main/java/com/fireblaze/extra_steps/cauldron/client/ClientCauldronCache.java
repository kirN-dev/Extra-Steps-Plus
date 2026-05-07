package com.fireblaze.extra_steps.cauldron.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import java.util.HashMap;
import java.util.Map;

public class ClientCauldronCache {

    private static final Map<BlockPos, NonNullList<ItemStack>> INVENTORIES = new HashMap<>();

    public static void setInventory(BlockPos pos, NonNullList<ItemStack> inv) {
        INVENTORIES.put(pos, inv);
    }

    public static NonNullList<ItemStack> getInventory(BlockPos pos) {
        return INVENTORIES.get(pos);
    }

    public static void remove(BlockPos pos) {
        INVENTORIES.remove(pos);
    }

    // 🔥 DAS FEHLTE
    public static Map<BlockPos, NonNullList<ItemStack>> getAll() {
        return INVENTORIES;
    }
}