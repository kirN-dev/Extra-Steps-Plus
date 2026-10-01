package com.fireblaze.extra_steps.util;

import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ScrapingProgressHelper {
    private static final String KEY = "extra_steps:scraping";
    private ScrapingProgressHelper() {}
    public static int get(ItemStack stack, ProcessingRecipe recipe) {
        if (!stack.hasTag()) return 0;
        CompoundTag progress = stack.getTag().getCompound(KEY);
        return progress.getString("recipe").equals(recipe.getId().toString())
                ? Math.max(0, Math.min(progress.getInt("completed"), recipe.getScrapingSettings().interactions - 1)) : 0;
    }
    public static void set(ItemStack stack, ProcessingRecipe recipe, int completed) {
        CompoundTag progress = new CompoundTag();
        progress.putString("recipe", recipe.getId().toString());
        progress.putInt("completed", completed);
        stack.getOrCreateTag().put(KEY, progress);
    }
    public static void clear(ItemStack stack) {
        if (stack.hasTag()) stack.getTag().remove(KEY);
    }
    public static boolean has(ItemStack stack) { return stack.hasTag() && stack.getTag().contains(KEY); }
}
