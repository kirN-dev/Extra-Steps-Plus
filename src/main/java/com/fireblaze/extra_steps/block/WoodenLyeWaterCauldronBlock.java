package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.WoodenLyeWaterCauldronBlockEntity;
import com.fireblaze.extra_steps.cauldron.interaction.WoodenCauldronInteractions;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoodenLyeWaterCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public WoodenLyeWaterCauldronBlock(Properties properties) {
        super(properties, null, WoodenCauldronInteractions.WOODEN_LYE_WATER);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        // Cauldron aufheben gibt normalen Cauldron zurück
        return new ItemStack(ModBlocks.WOODEN_CAULDRON.get().asItem());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenLyeWaterCauldronBlockEntity(pos, state);
    }

    // Optional: Helper für Farb-Tinting auf Client
    public static int getTintColor(BlockState state, Level level, BlockPos pos) {
        if(level != null && pos != null) {
            var be = level.getBlockEntity(pos);
            if(be instanceof WoodenLyeWaterCauldronBlockEntity dye) {
                return dye.getColor();
            }
        }
        return 0xFFFFFF; // Default Weiß
    }
}