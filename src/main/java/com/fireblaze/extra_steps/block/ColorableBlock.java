package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.ColorableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ColorableBlock extends Block implements EntityBlock {

    public ColorableBlock(Properties props) {
        super(props);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if(level.getBlockEntity(pos) instanceof ColorableBlockEntity be) {
            CompoundTag tag = stack.getTag();
            if (tag == null) return;

            if(stack.hasTag() && stack.getTag().contains("color")) {
                be.setColor(stack.getTag().getInt("color"));
            }

            if(stack.hasTag() && stack.getTag().contains("fillFactor")) {
                be.setFillFactor(stack.getTag().getFloat("fillFactor"));
            }

            for (int i = 0; i < 3; i++) {
                String key = "colorList" + i;

                if (tag.contains(key)) {
                    int[] arr = tag.getIntArray(key);
                    be.setColorList(i, arr); // ebenfalls Setter nötig
                }
            }

        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorableBlockEntity(pos, state);
    }
}