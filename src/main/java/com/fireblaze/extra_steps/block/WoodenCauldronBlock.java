package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.WoodenCauldronBlockEntity;
import com.fireblaze.extra_steps.cauldron.interaction.WoodenCauldronInteractions;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoodenCauldronBlock extends AbstractCauldronBlock implements EntityBlock {

    public WoodenCauldronBlock(Properties properties) {
        super(properties, WoodenCauldronInteractions.WOODEN_EMPTY);
    }

    @Override
    public boolean isFull(BlockState pState) {
        return false;
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
}