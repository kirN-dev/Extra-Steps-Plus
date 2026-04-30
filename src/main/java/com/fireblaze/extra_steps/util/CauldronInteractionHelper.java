package com.fireblaze.extra_steps.util;

import com.fireblaze.extra_steps.blockentity.LyeWaterCauldronBlockEntity;
import com.fireblaze.extra_steps.blockentity.WoodenLyeWaterCauldronBlockEntity;
import com.fireblaze.extra_steps.client.color.GenericColors;
import com.fireblaze.extra_steps.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CauldronInteractionHelper {
    public static void replaceHeldItem(Player player, ItemStack oldStack, ItemStack newStack) {
        player.setItemInHand(player.getUsedItemHand(),
                ItemUtils.createFilledResult(oldStack, player, newStack));
    }

    public static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public static void setBlock(Level level, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, state);
    }

    public static void setAndReloadBlock(Level level, BlockPos pos, BlockState state, CompoundTag beTag) {
        level.setBlockAndUpdate(pos, state);

        if (level.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {
            be.load(beTag);
            be.setChanged();
            be.sync();
        }

        if (level.getBlockEntity(pos) instanceof WoodenLyeWaterCauldronBlockEntity be) {
            be.load(beTag);
            be.setChanged();
            be.sync();
        }
    }

    public static int drainLevel(Level level, BlockPos pos, BlockState state,
                                 int currentLevel, int interactions,
                                 GenericCauldronInteractions be,
                                 BlockState emptyState) {

        for (int i = 0; i < interactions; i++) {
            boolean shouldDrain = be.consumeInteraction(currentLevel);

            if (shouldDrain) {
                currentLevel--;

                if (currentLevel <= 0) {
                    setBlock(level, pos, emptyState);
                    return 0;
                } else {
                    setBlock(level, pos,
                            state.setValue(LayeredCauldronBlock.LEVEL, currentLevel));
                }
            }
        }

        return currentLevel;
    }

    public static void replaceItemForPlayer(Player player, ItemStack stack, ItemStack result) {
        stack.shrink(1);
        boolean added = player.getInventory().add(result);
        if (!added) {
            player.drop(result, false);
        }
    }

    public static void applyLyeColor(GenericCauldronInteractions be, ItemStack result) {
        CompoundTag tag = be.getUpdateTag(); // Tag sicher erstellen

        if (tag.contains(GenericColorHelper.FILL_FACTOR)) {
            if (tag.getFloat(GenericColorHelper.FILL_FACTOR) != 0f) {
                be.copyColorTo(result, false);
            } else {
                be.copyColorTo(result, true);
            }
        } else {
            be.copyColorTo(result, true);
        }
    }

    public static InteractionResult applyLyeColorIfColorable(GenericCauldronInteractions be, ItemStack stack, ItemStack result) {
        CompoundTag tag = be.getUpdateTag();

        if (tag.getFloat(GenericColorHelper.FILL_FACTOR) != 0f) {
            if (stack.is(ModItems.SCRAPED_HIDE.get())) return InteractionResult.PASS; //todo filtern nach colorable items
            be.copyColorTo(result, false);
        }
        else {
            if (stack.hasTag() && !Objects.requireNonNull(stack.getTag()).contains(GenericColorHelper.COLOR_TAG, GenericColors.WHITE.getGenericColor())) {
                if (tag.contains(GenericColorHelper.FILL_FACTOR)) {
                    if (tag.getFloat(GenericColorHelper.FILL_FACTOR) != 0f) be.copyColorTo(result, false);
                    else GenericColorHelper.copyColor(stack, result);
                }
            }
        }
        return null;
    }

    public static final Map<GenericColors, Item> DYE_MAP = new HashMap<>(Map.ofEntries(
            Map.entry(GenericColors.WHITE, Items.WHITE_DYE),
            Map.entry(GenericColors.ORANGE, Items.ORANGE_DYE),
            Map.entry(GenericColors.MAGENTA, Items.MAGENTA_DYE),
            Map.entry(GenericColors.LIGHT_BLUE, Items.LIGHT_BLUE_DYE),
            Map.entry(GenericColors.YELLOW, Items.YELLOW_DYE),
            Map.entry(GenericColors.LIME, Items.LIME_DYE),
            Map.entry(GenericColors.PINK, Items.PINK_DYE),
            Map.entry(GenericColors.GRAY, Items.GRAY_DYE),
            Map.entry(GenericColors.LIGHT_GRAY, Items.LIGHT_GRAY_DYE),
            Map.entry(GenericColors.CYAN, Items.CYAN_DYE),
            Map.entry(GenericColors.PURPLE, Items.PURPLE_DYE),
            Map.entry(GenericColors.BLUE, Items.BLUE_DYE),
            Map.entry(GenericColors.BROWN, Items.BROWN_DYE),
            Map.entry(GenericColors.GREEN, Items.GREEN_DYE),
            Map.entry(GenericColors.RED, Items.RED_DYE),
            Map.entry(GenericColors.BLACK, Items.BLACK_DYE)
    ));
}