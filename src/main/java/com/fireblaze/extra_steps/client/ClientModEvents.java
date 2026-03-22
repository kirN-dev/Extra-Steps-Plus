package com.fireblaze.extra_steps.client;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.blockentity.DryingRackBlockEntity;
import com.fireblaze.extra_steps.client.render.BasketRenderer;
import com.fireblaze.extra_steps.client.render.LyeWaterCauldronRenderer;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.client.render.DryingRackRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtraSteps.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK_BE.get(), DryingRackRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.LYE_WATER_CAULDRON_BE.get(), LyeWaterCauldronRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BASKET_BE.get(), BasketRenderer::new);
    }
}