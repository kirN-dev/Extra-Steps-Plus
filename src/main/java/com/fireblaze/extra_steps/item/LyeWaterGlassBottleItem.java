package com.fireblaze.extra_steps.item;

import com.fireblaze.extra_steps.util.GenericColorHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.function.Supplier;

public class LyeWaterGlassBottleItem extends Item {

    public LyeWaterGlassBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        // Standardname
        Component baseName = super.getName(stack);

        // Prüfen, ob eine Farbe gesetzt ist
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (tag.contains(GenericColorHelper.COLOR_TAG)) {
                int color = tag.getInt(GenericColorHelper.COLOR_TAG);
                String hex = String.format("#%06X", color);
                return Component.literal("Dye Bottle (" + hex + ")");
            } if (tag.contains("preview_rainbow")) {
                return Component.literal("Dye Bottle");
            }
        }

        return baseName;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (stack.hasTag()) {
            assert stack.getTag() != null;
            if (!stack.getTag().contains(GenericColorHelper.AVAILABLE_INTERACTIONS)) return;
        } else return;

        int total = getAvailableInteractions(stack);
        if (total == 3) return;
        tooltip.add(Component.literal(total + "/3 filled").withStyle(ChatFormatting.YELLOW));
    }

    public int getAvailableInteractions(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(GenericColorHelper.AVAILABLE_INTERACTIONS) : 0;
    }
}