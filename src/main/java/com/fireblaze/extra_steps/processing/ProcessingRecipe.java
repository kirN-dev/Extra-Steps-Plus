package com.fireblaze.extra_steps.processing;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProcessingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    public record CountedIngredient(Ingredient ingredient, int count) {}
    private final List<CountedIngredient> ingredients;
    private final ItemStack result;
    private final int time;
    private final RecipeType<ProcessingRecipe> type;
    private final ProcessingMode mode;
    private final int waterLevelConsumption;
    private final int stirCount;

    private ScrapingSettings scrapingSettings = new ScrapingSettings(new com.google.gson.JsonObject());
    public ScrapingSettings getScrapingSettings() { return scrapingSettings; }
    public ProcessingRecipe withScrapingSettings(ScrapingSettings settings) {
        scrapingSettings = settings;
        return this;
    }
    private final int ingredientAmount;

    public ProcessingRecipe(ResourceLocation id,
                            List<CountedIngredient> ingredients,
                            ItemStack result,
                            int time,
                            RecipeType<ProcessingRecipe> type,
                            ProcessingMode mode,
                            int ingredientAmount,
                            int waterLevelConsumption,
                            int stirCount) {

        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.time = time;
        this.type = type;
        this.mode = mode;
        this.ingredientAmount = ingredientAmount;
        this.waterLevelConsumption = waterLevelConsumption;
        this.stirCount = stirCount;
    }

    public int getIngredientAmount() {
        return ingredientAmount;
    }

    public int getTime() {
        return time;
    }

    public int getWaterLevelReduction() {
        return waterLevelConsumption;
    }
    public int getStirCount() {
        return stirCount;
    }

    public ProcessingMode getMode() {
        return mode;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {

        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                inputs.add(stack.copy());
            }
        }

        for (CountedIngredient ci : ingredients) {

            int needed = ci.count();
            int matched = 0;

            for (ItemStack input : inputs) {

                if (ci.ingredient().test(input)) {

                    int available = input.getCount();
                    int take = Math.min(available, needed - matched);
                    matched += take;

                    input.shrink(take);

                    if (matched >= needed) break;
                }
            }

            if (matched < needed) return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess access) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        for (CountedIngredient ci : ingredients) {
            list.add(ci.ingredient());
        }

        return list;
    }

    public List<CountedIngredient> getCountedIngredients() {
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ProcessingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return type; // jetzt immer ModRecipeTypes.PROCESSING.get()
    }
}