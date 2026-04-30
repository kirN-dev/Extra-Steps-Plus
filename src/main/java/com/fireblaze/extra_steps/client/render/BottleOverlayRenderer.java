package com.fireblaze.extra_steps.client.render;

import com.fireblaze.extra_steps.item.LyeWaterGlassBottleItem;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BottleOverlayRenderer {

    // Hotbar-Rendering
    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiOverlayEvent.Post event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        NamedGuiOverlay overlay = event.getOverlay();
        if (!"minecraft:hotbar".equals(overlay.id().toString())) return;

        for (int i = 0; i < 9; i++) {
            renderBottleBarHotbar(guiGraphics, i);
        }
    }

    // Inventar-Rendering
    @SubscribeEvent
    public static void onRenderInventory(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();

        for (Slot slot : screen.getMenu().slots) {
            ItemStack stack = slot.getItem();
            if (!(stack.getItem() instanceof LyeWaterGlassBottleItem)) continue;

            // exakte Slot-Position auf dem Bildschirm
            int slotX = slot.x + screen.getGuiLeft() - 3;
            int slotY = slot.y + screen.getGuiTop() - 3;

            renderBottleBarSlot(guiGraphics, stack, slotX, slotY);
        }
    }

    private static void renderBottleBarHotbar(GuiGraphics guiGraphics, int index) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = mc.player.getInventory().getItem(index);
        if (!(stack.getItem() instanceof LyeWaterGlassBottleItem)) return;

        renderBottleBarSlot(guiGraphics, stack,
                mc.getWindow().getGuiScaledWidth() / 2 - 91 + index * 20,
                mc.getWindow().getGuiScaledHeight() - 22);
    }

    private static void renderBottleBarSlot(GuiGraphics guiGraphics, ItemStack stack, int slotX, int slotY) {
        int interactions = ((LyeWaterGlassBottleItem) stack.getItem()).getAvailableInteractions(stack);
        if (interactions <= 0 || interactions >= 3) return;

        int color = stack.hasTag() && stack.getTag().contains(GenericColorHelper.COLOR_TAG)
                ? stack.getTag().getInt(GenericColorHelper.COLOR_TAG)
                : 0xFFFFFF;

        int r = (int)(((color >> 16) & 0xFF) * 0.8f);
        int g = (int)(((color >> 8) & 0xFF) * 0.8f);
        int b = (int)((color & 0xFF) * 0.8f);
        int finalColor = (r << 16) | (g << 8) | b;

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int barWidth = 1;
        int barHeight = 12;
        int slotHeight = 14;
        int filled = Math.round((interactions / 3f) * barHeight);

        // Links neben Slot
        int barX = slotX - barWidth + 5;
        int barBottomY = slotY + slotHeight + 3;
        int barTopY = barBottomY - filled;

        // Hintergrund
        guiGraphics.fill(barX, barBottomY - barHeight, barX + barWidth, barBottomY, 0xFF555555);
        // Gefüllt
        guiGraphics.fill(barX, barTopY, barX + barWidth, barBottomY, 0xFF000000 | finalColor);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}