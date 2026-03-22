package com.fireblaze.extra_steps.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CrumbledWoolBlock extends Block {

    public CrumbledWoolBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand,
                                 BlockHitResult hit) {

        if(!level.isClientSide) {

            player.startSleepInBed(pos);

            level.destroyBlock(pos, false);
        }

        return InteractionResult.SUCCESS;
    }
}