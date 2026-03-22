package com.fireblaze.extra_steps.processing;

import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class ProcessingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    public final Ingredient ingredient;
    private final ItemStack result;
    private final int time;
    private final RecipeType<ProcessingRecipe> type;
    private final ProcessingMode mode;

    public ProcessingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int time, RecipeType<ProcessingRecipe> type, ProcessingMode mode) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.time = time;
        this.type = type;
        this.mode = mode;
    }

    public int getTime() {
        return time;
    }

    public ProcessingMode getMode() {
        return mode;
    }

    @Override
    public boolean matches(SimpleContainer container, net.minecraft.world.level.Level level) {
        return ingredient.test(container.getItem(0));
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
        list.add(this.ingredient);
        return list;
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