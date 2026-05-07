package com.fireblaze.extra_steps.cauldron.client.event;

import com.fireblaze.extra_steps.cauldron.client.ClientCauldronCache;
import com.fireblaze.extra_steps.cauldron.client.cauldron.CauldronItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            return;

        PoseStack poseStack = event.getPoseStack();
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (var entry : ClientCauldronCache.getAll().entrySet()) {
            CauldronItemRenderer.render(entry.getKey(), poseStack, bufferSource);
        }

        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
    }
}