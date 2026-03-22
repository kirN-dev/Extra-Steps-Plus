package com.fireblaze.extra_steps.util;

import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import com.fireblaze.extra_steps.registry.ModTags;

import java.util.Optional;

public class ScrapingHelper {

    public static boolean canScrape(ItemStack target, Player player, Optional<ProcessingRecipe> recipe) {

        if (recipe.get().ingredient.test(target)) {
            ItemStack held = player.getMainHandItem();

            return held.getItem() instanceof AxeItem || held.getItem() instanceof SwordItem;
        }

        return false;
    }
}