package com.fireblaze.extra_steps.blockentity;

import com.fireblaze.extra_steps.block.WoodenCauldronBlock;
import com.fireblaze.extra_steps.config.ModConfigHandler;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WoodenCauldronBlockEntity extends BlockEntity {

    private int tickCounter = 0; // Zählt die Ticks

    public WoodenCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WOODEN_CAULDRON_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodenCauldronBlockEntity be) {
        if (level.isClientSide) return; // nur Server-Seite

        // Tick-Counter hochzählen
        be.tickCounter++;
        // Prüfe nur alle 10 Sekunden (20 Ticks * 10 Sekunden = 200)
        if (be.tickCounter < ModConfigHandler.cauldronFillCheckInterval.get()) return;
        be.tickCounter = 0; // Reset Counter

        if (!level.isRainingAt(pos.above())) return;

        // Randomizer: Chance, dass der Cauldron tatsächlich steigt (z.B. 1/4)
        if (level.random.nextInt(ModConfigHandler.cauldronFillChance.get()) != 0) return;

        BlockState newState;
        if (state.getBlock() == ModBlocks.WOODEN_WATER_CAULDRON.get()) {
            int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
            if (currentLevel < 3) {
                newState = state.setValue(LayeredCauldronBlock.LEVEL, currentLevel + 1);
                level.setBlock(pos, newState, 2);
            }
        } else {
            newState = ModBlocks.WOODEN_WATER_CAULDRON.get()
                    .defaultBlockState()
                    .setValue(LayeredCauldronBlock.LEVEL, 1);
            level.setBlock(pos, newState, 2);
        }
    }
}