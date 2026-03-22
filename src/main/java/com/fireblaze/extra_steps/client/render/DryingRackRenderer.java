package com.fireblaze.extra_steps.client.render;

import com.fireblaze.extra_steps.block.DryingRackBlock;
import com.fireblaze.extra_steps.blockentity.DryingRackBlockEntity;
import com.fireblaze.extra_steps.processing.ProcessingItemRegistry;
import com.fireblaze.extra_steps.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;

public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {

    public DryingRackRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(
            DryingRackBlockEntity rack,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay) {

        ItemStack stack = rack.getItemInSlot0();
        if (stack.isEmpty()) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, rack.getLevel(), null, 0);

        poseStack.pushPose();

        // Position leicht über der Rack-Mitte
        poseStack.translate(0.5, 0.5, 0.5);

        // Rotation anhand FACING
        var facing = rack.getBlockState().getValue(DryingRackBlock.FACING);
        float rotation = switch (facing) {
            case NORTH -> 0f;
            case EAST  -> 90f;
            case SOUTH -> 180f;
            case WEST  -> 270f;
            default -> 0f;
        };
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));

        // Skalierung
        poseStack.scale(0.95f, 0.95f, 0.95f);

        // Light fix (voll beleuchtet)
        int light = LightTexture.pack(
                rack.getLevel().getBrightness(LightLayer.BLOCK, rack.getBlockPos()),
                rack.getLevel().getBrightness(LightLayer.SKY, rack.getBlockPos())
        );

        // Rendern des Items
        itemRenderer.render(
                stack,
                ItemDisplayContext.FIXED,
                false,
                poseStack,
                buffer,
                light,
                combinedOverlay,
                model
        );

        poseStack.popPose();

        // --- Partikel-Logik ---
        if (!stack.isEmpty() && ProcessingItemRegistry.isValidDryingItem(stack)) { // Nur Wet Leather

            float speed = rack.clientSpeed;
            var world = rack.getLevel();
            var pos = rack.getBlockPos();

            if (world == null || !world.isClientSide) return; // nur clientseitig

            // Dezente Chance pro Frame: nicht jedes Frame ein Partikel
            if (world.random.nextFloat() < 0.03f) { // 3% Chance

                // Item-Center
                double centerX = pos.getX() + 0.5;
                double centerY = pos.getY() + 0.5 + 0.5; // leicht über Rack-Mitte
                double centerZ = pos.getZ() + 0.5;

                // Zufällige kleine Verschiebung um das Item (Item-Bounding Box)
                double offsetX = (world.random.nextDouble() - 0.5); // ±0.2
                double offsetY = (world.random.nextDouble() - 1) * 0.5; // ±0.1
                double offsetZ = (world.random.nextDouble() - 0.5); // ±0.2

                double x = centerX + offsetX;
                double y = centerY + offsetY;
                double z = centerZ + offsetZ;

                // Sehr langsame Aufwärtsbewegung
                double dy = (speed > 0 ? 0.005 : 0.002);

                // Partikel auswählen
                if (speed > 0) {
                    // warme Partikel (minimal, zufällig um das Item)
                    world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, dy, 0);
                } else {
                    // kalte Partikel
                    world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, dy, 0);
                }
            }
        }

        if(rack.showBrushFinishedParticles) {
            var world = rack.getLevel();
            var pos = rack.getBlockPos();
            if(world != null && world.isClientSide) {
                for(int i = 0; i < 10; i++) { // 10 Partikel
                    double x = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5);
                    double y = pos.getY() + 0.7 + world.random.nextDouble() * 0.3;
                    double z = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5);

                    world.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0.05, 0); // grüne Sterne!
                }
            }

            rack.showBrushFinishedParticles = false;
        }
    }
}