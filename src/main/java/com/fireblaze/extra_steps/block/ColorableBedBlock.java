package com.fireblaze.extra_steps.block;

import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;

public class ColorableBedBlock extends BedBlock implements EntityBlock {

    public ColorableBedBlock(Properties props) {
        super(DyeColor.WHITE, props);
    }
}