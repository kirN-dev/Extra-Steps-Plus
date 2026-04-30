package com.fireblaze.extra_steps.client.render;

import com.fireblaze.extra_steps.block.ColorableGlassBlock;
import com.fireblaze.extra_steps.block.ColorableGlassPaneBlock;
import com.fireblaze.extra_steps.blockentity.ColorableGlassBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ColorableGlassBlockRenderer implements BlockEntityRenderer<ColorableGlassBlockEntity> {

    public ColorableGlassBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ColorableGlassBlockEntity be, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        int color = be.getColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        VertexConsumer builder = buffer.getBuffer(RenderType.translucentNoCrumbling());

        stack.pushPose();
        // Verschiebung zum Block-Mittelpunkt
        stack.translate(0, 0, 0);

        for (Direction dir : Direction.values()) {
            if (shouldRenderFace(be, dir)) {
                renderFace(stack, builder, dir, r, g, b, combinedLight, combinedOverlay);
            }
        }

        stack.popPose();
    }

    private boolean shouldRenderFace(ColorableGlassBlockEntity be, Direction dir) {
        BlockPos neighborPos = be.getBlockPos().relative(dir);
        BlockState neighborState = be.getLevel().getBlockState(neighborPos);

        if (!(neighborState.getBlock() instanceof ColorableGlassBlock)) {
            return true; // kein benachbarter Glasblock → rendern
        }

        BlockEntity neighborBE = be.getLevel().getBlockEntity(neighborPos);
        if (!(neighborBE instanceof ColorableGlassBlockEntity)) return true;

        Block block = neighborState.getBlock();

        if (!(block instanceof ColorableGlassBlock) || block instanceof ColorableGlassPaneBlock) {
            return true;
        }

        // Nur Face rendern, wenn Farben unterschiedlich sind
        return ((ColorableGlassBlockEntity) neighborBE).getColor() != be.getColor();
    }

    private void renderFace(PoseStack stack, VertexConsumer builder, Direction dir,
                            float r, float g, float b, int light, int overlay) {

        addQuad(builder, stack, r, g, b, light, overlay, dir);
    }

    private void addQuad(VertexConsumer builder, PoseStack stack,
                         float r, float g, float b, int light, int overlay, Direction dir) {

        TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(ResourceLocation.fromNamespaceAndPath("extra_steps", "block/white_stained_glass"));

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        var pose = stack.last().pose();

        switch (dir) {

            case NORTH -> {
                // z = 0
                vertex(builder, pose, 0, 0, 0, r, g, b, u0, v1, light, overlay, 0, 0, -1);
                vertex(builder, pose, 0, 1, 0, r, g, b, u0, v0, light, overlay, 0, 0, -1);
                vertex(builder, pose, 1, 1, 0, r, g, b, u1, v0, light, overlay, 0, 0, -1);
                vertex(builder, pose, 1, 0, 0, r, g, b, u1, v1, light, overlay, 0, 0, -1);
            }

            case SOUTH -> {
                // z = 1
                vertex(builder, pose, 0, 0, 1, r, g, b, u0, v1, light, overlay, 0, 0, 1);
                vertex(builder, pose, 1, 0, 1, r, g, b, u1, v1, light, overlay, 0, 0, 1);
                vertex(builder, pose, 1, 1, 1, r, g, b, u1, v0, light, overlay, 0, 0, 1);
                vertex(builder, pose, 0, 1, 1, r, g, b, u0, v0, light, overlay, 0, 0, 1);
            }

            case WEST -> {
                // x = 0
                vertex(builder, pose, 0, 0, 0, r, g, b, u0, v1, light, overlay, -1, 0, 0);
                vertex(builder, pose, 0, 0, 1, r, g, b, u1, v1, light, overlay, -1, 0, 0);
                vertex(builder, pose, 0, 1, 1, r, g, b, u1, v0, light, overlay, -1, 0, 0);
                vertex(builder, pose, 0, 1, 0, r, g, b, u0, v0, light, overlay, -1, 0, 0);
            }

            case EAST -> {
                // x = 1
                vertex(builder, pose, 1, 0, 0, r, g, b, u0, v1, light, overlay, 1, 0, 0);
                vertex(builder, pose, 1, 1, 0, r, g, b, u0, v0, light, overlay, 1, 0, 0);
                vertex(builder, pose, 1, 1, 1, r, g, b, u1, v0, light, overlay, 1, 0, 0);
                vertex(builder, pose, 1, 0, 1, r, g, b, u1, v1, light, overlay, 1, 0, 0);
            }

            case DOWN -> {
                // y = 0
                vertex(builder, pose, 0, 0, 0, r, g, b, u0, v1, light, overlay, 0, -1, 0);
                vertex(builder, pose, 1, 0, 0, r, g, b, u1, v1, light, overlay, 0, -1, 0);
                vertex(builder, pose, 1, 0, 1, r, g, b, u1, v0, light, overlay, 0, -1, 0);
                vertex(builder, pose, 0, 0, 1, r, g, b, u0, v0, light, overlay, 0, -1, 0);
            }

            case UP -> {
                // y = 1
                vertex(builder, pose, 0, 1, 0, r, g, b, u0, v1, light, overlay, 0, 1, 0);
                vertex(builder, pose, 0, 1, 1, r, g, b, u0, v0, light, overlay, 0, 1, 0);
                vertex(builder, pose, 1, 1, 1, r, g, b, u1, v0, light, overlay, 0, 1, 0);
                vertex(builder, pose, 1, 1, 0, r, g, b, u1, v1, light, overlay, 0, 1, 0);
            }
        }
    }

    private void vertex(VertexConsumer builder, org.joml.Matrix4f pose,
                        float x, float y, float z,
                        float r, float g, float b,
                        float u, float v,
                        int light, int overlay,
                        float nx, float ny, float nz) {

        builder.vertex(pose, x, y, z)
                .color(r, g, b, 1f)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }
}