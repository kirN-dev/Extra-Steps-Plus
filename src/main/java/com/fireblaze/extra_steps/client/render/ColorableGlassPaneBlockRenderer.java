package com.fireblaze.extra_steps.client.render;

import com.fireblaze.extra_steps.block.ColorableGlassPaneBlock;
import com.fireblaze.extra_steps.blockentity.ColorableGlassPaneBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ColorableGlassPaneBlockRenderer implements BlockEntityRenderer<ColorableGlassPaneBlockEntity> {

    public ColorableGlassPaneBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ColorableGlassPaneBlockEntity be, float partialTicks, PoseStack stack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        int color = be.getColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        stack.pushPose();
        Matrix4f pose = stack.last().pose();

        // Pane Dimensionen in Blockkoordinaten (0-1)
        float x0 = 0f, x1 = 1f;
        float y0 = 0f, y1 = 1f;
        float z0 = 7f/16f, z1 = 9f/16f; // dünne Pane auf Z-Achse

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(ResourceLocation.fromNamespaceAndPath("extra_steps", "block/white_stained_glass"));
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // Front/Back (North/South)
        renderQuad(builder, pose, r, g, b, u0, v0, u1, v1, x0, y0, z0, x1, y1, z0, 0, 0, -1);
        renderQuad(builder, pose, r, g, b, u0, v0, u1, v1, x0, y0, z1, x1, y1, z1, 0, 0, 1);

        // West
        renderQuad(builder, pose, r, g, b, u0, v0, u1, v1, x0, y0, z0, x0, y1, z1, -1, 0, 0);
        // East
        renderQuad(builder, pose, r, g, b, u0, v0, u1, v1, x1, y0, z0, x1, y1, z1, 1, 0, 0);

        // Top/Bottom
        renderQuad(builder, pose, r, g, b, u0, v0, u1, v1, x0, y1, z0, x1, y1, z1, 0, 1, 0);
        renderQuad(builder, pose, r, g, b, u0, v0, u1, v1, x0, y0, z0, x1, y0, z1, 0, -1, 0);

        stack.popPose();
    }

    private void renderQuad(VertexConsumer builder, Matrix4f pose,
                            float r, float g, float b,
                            float u0, float v0, float u1, float v1,
                            float x0, float y0, float z0,
                            float x1, float y1, float z1,
                            float nx, float ny, float nz) {

        // Front Vertices
        builder.vertex(pose, x0, y0, z0).color(r, g, b, 1f).uv(u0, v1).overlayCoords(0).uv2(0).normal(nx, ny, nz).endVertex();
        builder.vertex(pose, x1, y0, z0).color(r, g, b, 1f).uv(u1, v1).overlayCoords(0).uv2(0).normal(nx, ny, nz).endVertex();
        builder.vertex(pose, x1, y1, z1).color(r, g, b, 1f).uv(u1, v0).overlayCoords(0).uv2(0).normal(nx, ny, nz).endVertex();
        builder.vertex(pose, x0, y1, z1).color(r, g, b, 1f).uv(u0, v0).overlayCoords(0).uv2(0).normal(nx, ny, nz).endVertex();

        // Back Vertices
        builder.vertex(pose, x0, y1, z1).color(r, g, b, 1f).uv(u0, v0).overlayCoords(0).uv2(0).normal(-nx, -ny, -nz).endVertex();
        builder.vertex(pose, x1, y1, z1).color(r, g, b, 1f).uv(u1, v0).overlayCoords(0).uv2(0).normal(-nx, -ny, -nz).endVertex();
        builder.vertex(pose, x1, y0, z0).color(r, g, b, 1f).uv(u1, v1).overlayCoords(0).uv2(0).normal(-nx, -ny, -nz).endVertex();
        builder.vertex(pose, x0, y0, z0).color(r, g, b, 1f).uv(u0, v1).overlayCoords(0).uv2(0).normal(-nx, -ny, -nz).endVertex();
    }
}