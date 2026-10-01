package com.fireblaze.extra_steps.compat;

import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ProcessingDisplayFactory {
    private ProcessingDisplayFactory() {}

    public static ProcessingRecipeDisplay create(ProcessingRecipe recipe) {
        var inputs = recipe.getCountedIngredients().stream().map(input ->
                Arrays.stream(input.ingredient().getItems()).map(stack -> {
                    ItemStack copy = stack.copy();
                    copy.setCount(input.count());
                    return copy;
                }).toList()).toList();
        ProcessingMode mode = recipe.getMode();
        var equipmentTooltip = new ArrayList<Component>();
        var primaryTooltip = new ArrayList<Component>();
        var actionTooltip = actionTooltip(recipe);
        var secondary = new ArrayList<ProcessingRecipeDisplay.SecondaryOutput>();
        List<ItemStack> tools = List.of();
        if (mode == ProcessingMode.MIXING) {
            tools = Arrays.asList(Ingredient.of(ModTags.Items.MIXING_TOOLS).getItems());
            equipmentTooltip.add(text(recipe.getWaterLevelReduction() > 0 ? "water" : "mixing_cauldron"));
        } else if (mode == ProcessingMode.SCRAPING) {
            var settings = recipe.getScrapingSettings();
            tools = BuiltInRegistries.ITEM.stream().map(ItemStack::new)
                    .filter(settings::acceptsTool).toList();
        } else if (mode == ProcessingMode.BRUSHING) {
            tools = List.of(new ItemStack(ModItems.WOOL_BRUSH.get()));
        }
        if (mode == ProcessingMode.SCRAPING || mode == ProcessingMode.MIXING || mode == ProcessingMode.BRUSHING) {
            var settings = recipe.getScrapingSettings();
            int cycleLength = mode == ProcessingMode.MIXING ? recipe.getStirCount() : settings.interactions;
            for (var byproduct : settings.byproducts) {
                int occurrences = mode == ProcessingMode.BRUSHING
                        ? (byproduct.appliesAt(cycleLength, true) ? 1 : 0)
                        : byproduct.occurrencesPerCycle(cycleLength);
                if (occurrences == 0 || byproduct.chance() <= 0) continue;
                ItemStack stack = byproduct.stack().copy();
                stack.setCount(stack.getCount() * occurrences);
                var tooltip = new ArrayList<Component>();
                if (byproduct.chance() < 1) tooltip.add(text("chance", new java.math.BigDecimal(Float.toString(byproduct.chance()))
                        .multiply(java.math.BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString())
                        .copy().withStyle(ChatFormatting.GOLD));
                secondary.add(new ProcessingRecipeDisplay.SecondaryOutput(stack, List.copyOf(tooltip)));
            }
        }
        if (mode == ProcessingMode.CLEANING) {
            equipmentTooltip.add(text("lye"));
        }
        if (mode == ProcessingMode.LYE_WATER) {
            equipmentTooltip.add(text("water"));
            primaryTooltip.add(text("lye_result"));
        }
        return new ProcessingRecipeDisplay(inputs, EquipmentDisplayResolver.resolve(mode),
                List.copyOf(equipmentTooltip), tools, recipe.getResultItem(null).copy(),
                List.copyOf(primaryTooltip), List.copyOf(secondary), List.copyOf(actionTooltip));
    }

    public static List<Component> actionTooltip(ProcessingRecipe recipe) {
        return switch (recipe.getMode()) {
            case MIXING -> recipe.getWaterLevelReduction() > 0
                    ? List.of(text("stirs", recipe.getStirCount()), text("water_cost", recipe.getWaterLevelReduction()))
                    : List.of(text("stirs", recipe.getStirCount()));
            // Cleaning handlers consume one level of lye water per operation.
            case CLEANING -> List.of(text("lye_cost", 1));
            case SCRAPING -> List.of(text("hits", recipe.getScrapingSettings().interactions));
            case DRYING -> List.of(text("time", recipe.getTime() / 20f), text("drying_factors"));
            case BRUSHING -> List.of(text("brushing"));
            case COMPRESSING -> List.of(text("compressing"));
            default -> List.of();
        };
    }

    private static Component text(String key, Object... values) {
        return Component.translatable("jei.extra_steps." + key, values);
    }
}
