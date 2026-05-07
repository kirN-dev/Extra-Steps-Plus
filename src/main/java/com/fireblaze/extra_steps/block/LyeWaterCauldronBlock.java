package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.LyeWaterCauldronBlockEntity;
import com.fireblaze.extra_steps.cauldron.interaction.ModCauldronInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LyeWaterCauldronBlock extends LayeredCauldronBlock  implements EntityBlock {

    public LyeWaterCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties,
                precipitation -> false,
                ModCauldronInteractions.LYE_WATER);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        // Cauldron aufheben gibt normalen Cauldron zurück
        return new ItemStack(Items.CAULDRON);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LyeWaterCauldronBlockEntity(pos, state);
    }

    // Optional: Helper für Farb-Tinting auf Client
    public static int getTintColor(BlockState state, Level level, BlockPos pos) {
        if(level != null && pos != null) {
            var be = level.getBlockEntity(pos);
            if(be instanceof LyeWaterCauldronBlockEntity dye) {
                return dye.getColor();
            }
        }
        return 0xFFFFFF; // Default Weiß
    }
}