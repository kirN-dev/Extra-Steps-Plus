package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.ColorableWoolBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ColorableWoolBlock extends Block implements EntityBlock {

    public ColorableWoolBlock(Properties props) {
        super(props);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if(level.getBlockEntity(pos) instanceof ColorableWoolBlockEntity be) {
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
        return new ColorableWoolBlockEntity(pos, state);
    }

    @Override
    public void playerDestroy(Level level, net.minecraft.world.entity.player.Player player,
                              BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity,
                              ItemStack tool) {

        if (!level.isClientSide && blockEntity instanceof ColorableWoolBlockEntity be) {

            ItemStack stack = new ItemStack(this);

            CompoundTag tag = new CompoundTag();

            tag.putInt("color", be.getColor());
            tag.putFloat("fillFactor", be.getFillFactor());

            for (int i = 0; i < 3; i++) {
                tag.putIntArray("colorList" + i, be.getColorList(i));
            }

            stack.setTag(tag);

            popResource(level, pos, stack);
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
}