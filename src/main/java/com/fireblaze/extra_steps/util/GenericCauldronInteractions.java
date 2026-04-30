package com.fireblaze.extra_steps.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface GenericCauldronInteractions {

    CompoundTag getUpdateTag();
    boolean consumeInteraction(int level);

    void copyColorTo(ItemStack stack, boolean empty);

    void drainLevel(Level level, BlockPos pos, BlockState state,
                    int currentLevel, int interactions,
                    BlockState emptyState);

}