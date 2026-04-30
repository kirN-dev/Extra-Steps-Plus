package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.ColorableGlassBlockEntity;
import com.fireblaze.extra_steps.blockentity.ColorableBlockEntity;
import com.fireblaze.extra_steps.config.ModConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColorableGlassBlock extends GlassBlock implements EntityBlock {
    public ColorableGlassBlock(Properties p_53640_) {
        super(p_53640_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorableGlassBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if(level.getBlockEntity(pos) instanceof ColorableGlassBlockEntity be) {
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
                    be.setColorList(i, arr);
                }
            }

        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true; // Licht geht durch
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        if (ModConfigHandler.enableGlassBlockCulling.get())
            return RenderShape.INVISIBLE;
        else return RenderShape.MODEL;
    }
}
