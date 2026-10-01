package com.fireblaze.extra_steps.compat.jade;

import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CauldronProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var state = accessor.getBlockState();
        if (!state.is(ModBlocks.WOODEN_WATER_CAULDRON.get())
                && !state.is(ModBlocks.WOODEN_LYE_WATER_CAULDRON.get())
                && !state.is(ModBlocks.LYE_WATER_CAULDRON.get())) return;
        tooltip.add(Component.translatable("jade.extra_steps.liquid", state.getValue(LayeredCauldronBlock.LEVEL), 3));
    }

    @Override
    public ResourceLocation getUid() {
        return ExtraStepsJadePlugin.CAULDRON;
    }
}
