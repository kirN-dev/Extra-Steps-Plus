package com.fireblaze.extra_steps.client.render;

import com.fireblaze.extra_steps.blockentity.BasketBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class BasketRenderer implements BlockEntityRenderer<BasketBlockEntity> {

    private final ItemRenderer itemRenderer;

    public BasketRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BasketBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {

        ItemStack stack = be.getInventoryHandler().getStackInSlot(0);
        if(stack.isEmpty()) return;

        int count = stack.getCount();

        // --- Füllstand bestimmen ---
        int level = 0;
        if(count >= 48) level = 4;
        else if(count >= 32) level = 3;
        else if(count >= 16) level = 2;
        else if(count > 0) level = 1;

        if(level == 0) return;

        poseStack.pushPose();

        // Zentrum vom Block
        poseStack.translate(0.5, 0.3, 0.5);

        // leicht verkleinern
        poseStack.scale(0.8f, 0.8f, 0.8f);

        // leichte Rotation für schöneren Look
        poseStack.mulPose(Axis.YP.rotationDegrees(15f));

        // --- Mehrere Layer rendern ---
        for(int i = 0; i < level; i++) {

            poseStack.pushPose();

            // Höhe je Layer
            poseStack.translate(0, i * 0.10f, 0);

            // Optional: leicht versetzen für natürlicheren Look
            float offset = (i % 2 == 0) ? 0.05f : -0.05f;
            poseStack.translate(offset, 0, offset);

            // --- Horizontale leichte Kippung ---
            long baseSeed = be.getBlockPos().asLong(); // konstant pro Block
            Random rand = new Random(baseSeed + i);
            float randomYaw = rand.nextFloat() * 360f; // 0-359°
            poseStack.mulPose(Axis.YP.rotationDegrees(randomYaw));

            // Item flach hinlegen: 90° um X-Achse
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));

            // Optional: kleine Y-Rotation für Variation
            //poseStack.mulPose(Axis.YP.rotationDegrees(i * 20f));

            // Einzelnes Item rendern (Count = 1!)
            ItemStack renderStack = stack.copy();
            renderStack.setCount(1);

            itemRenderer.renderStatic(
                    renderStack,
                    ItemDisplayContext.FIXED,
                    light,
                    overlay,
                    poseStack,
                    buffer,
                    be.getLevel(),
                    0
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}