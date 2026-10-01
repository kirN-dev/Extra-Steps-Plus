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

        return recipe.isPresent() && recipe.get().getIngredients().stream().anyMatch(ingredient -> ingredient.test(target))
                && recipe.get().getScrapingSettings().acceptsTool(player.getMainHandItem());
    }
}