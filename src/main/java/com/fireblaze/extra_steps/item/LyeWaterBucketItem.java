package com.fireblaze.extra_steps.item;

import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.function.Supplier;

public class LyeWaterBucketItem extends BucketItem {

    public LyeWaterBucketItem(Supplier<? extends Fluid> fluid, Properties properties) {
        super(fluid, properties);
    }

    private int getTotalAvailableInteractions(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;

        int total = 0;

        for (int i = 0; i < 3; i++) {
            String key = "availableInteraction" + i;

            if (tag.contains(key)) {
                total += tag.getInt(key);
            } else {
                total += 3; // default = voll
            }
        }

        return total; // 0 → 9
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            if (!stack.getTag().contains(GenericColorHelper.AVAILABLE_INTERACTIONS + "0")) return false;
        } else return false;
        return getTotalAvailableInteractions(stack) < 9;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int total = getTotalAvailableInteractions(stack);

        float fill = total / 9f; // max = 9

        return Math.round(13 * fill);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(GenericColorHelper.COLOR_TAG)) return 0xFFFFFF;

        int color = tag.getInt(GenericColorHelper.COLOR_TAG);

        // leicht abdunkeln (optional, aber sieht besser aus)
        int r = (int)(((color >> 16) & 0xFF) * 0.8f);
        int g = (int)(((color >> 8) & 0xFF) * 0.8f);
        int b = (int)((color & 0xFF) * 0.8f);

        return (r << 16) | (g << 8) | b;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Nur erlauben, wenn komplett voll (9/9)
        if (getTotalAvailableInteractions(stack) < 9) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Bucket is not full!"),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (stack.hasTag()) {
            assert stack.getTag() != null;
            if (!stack.getTag().contains(GenericColorHelper.AVAILABLE_INTERACTIONS + "0")) return;
        } else return;

        int total = getTotalAvailableInteractions(stack);
        if (total == 9) {
            tooltip.add(Component.literal("Bucket can be placed outside of cauldron").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal("(but will lose its color)").withStyle(ChatFormatting.RED));
        }
        else tooltip.add(Component.literal("Bucket is " + total + "/9 filled").withStyle(ChatFormatting.YELLOW));
    }
}