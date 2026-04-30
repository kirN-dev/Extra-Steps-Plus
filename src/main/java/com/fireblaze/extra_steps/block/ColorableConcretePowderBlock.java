package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.ColorableBlockEntity;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class ColorableConcretePowderBlock extends ConcretePowderBlock implements EntityBlock {

    public ColorableConcretePowderBlock(Properties props) {
        super(ModBlocks.COLORABLE_CONCRETE.get(), props); // Härte wie normales ConcretePowder
    }

    private boolean shouldSolidify(Level level, BlockPos pos) {
        for (var dir : net.minecraft.core.Direction.values()) {
            if (!level.getFluidState(pos.relative(dir)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void solidify(Level level, BlockPos pos) {
        BlockEntity oldBe = level.getBlockEntity(pos);
        if (!(oldBe instanceof ColorableBlockEntity)) return;

        ColorableBlockEntity oldColorable = (ColorableBlockEntity) oldBe;

        // 🔹 NBT sichern
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", oldColorable.getColor());
        tag.putFloat("fillFactor", oldColorable.getFillFactor());
        for (int i = 0; i < 3; i++) {
            tag.putIntArray("colorList" + i, oldColorable.getColorList(i).stream().mapToInt(Integer::intValue).toArray());
        }

        // 🔹 Block ersetzen → ColorableConcrete
        level.setBlock(pos, ModBlocks.COLORABLE_CONCRETE.get().defaultBlockState(), Block.UPDATE_ALL);

        // 🔹 Neue BE holen und NBT übertragen
        BlockEntity newBe = level.getBlockEntity(pos);
        if (newBe instanceof ColorableBlockEntity colorableNewBe) {
            colorableNewBe.load(tag);
            colorableNewBe.setChanged();
            colorableNewBe.sync();
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide && shouldSolidify(level, pos)) {
            solidify(level, pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Jede Powder-Instanz bekommt eine eigene ColorableBlockEntity
        return new ColorableBlockEntity(pos, state);
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

    @Override
    public void playerDestroy(Level level, net.minecraft.world.entity.player.Player player,
                              BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity,
                              ItemStack tool) {

        if (!level.isClientSide && blockEntity instanceof ColorableBlockEntity be) {

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