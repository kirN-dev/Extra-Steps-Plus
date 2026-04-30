package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.WoodenCauldronBlockEntity;
import com.fireblaze.extra_steps.fluid.WoodenCauldronInteractions;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class WoodenWaterCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public WoodenWaterCauldronBlock(Properties properties) {
        super(properties, item -> false, WoodenCauldronInteractions.WOODEN_WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenCauldronBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.WOODEN_CAULDRON_BE.get()) {
            // Safe cast: wir wissen, dass T WoodenCauldronBlockEntity ist
            return (lvl, pos, st, be) -> WoodenCauldronBlockEntity.tick(lvl, pos, st, (WoodenCauldronBlockEntity) be);
        }
        return null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(ModBlocks.WOODEN_CAULDRON.get().asItem());
    }
}