package com.fireblaze.extra_steps.compat.jade;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.block.DryingRackBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ExtraStepsJadePlugin implements IWailaPlugin {
    static final ResourceLocation RACK = ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "rack");
    static final ResourceLocation CAULDRON = ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "cauldron");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(RackProvider.INSTANCE, com.fireblaze.extra_steps.blockentity.DryingRackBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(RackProvider.INSTANCE, DryingRackBlock.class);
        registration.registerBlockComponent(CauldronProvider.INSTANCE, LayeredCauldronBlock.class);
    }
}
