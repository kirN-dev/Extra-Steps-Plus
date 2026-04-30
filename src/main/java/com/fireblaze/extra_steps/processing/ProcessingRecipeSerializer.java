package com.fireblaze.extra_steps.processing;

import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ProcessingRecipeSerializer implements RecipeSerializer<ProcessingRecipe> {

    public static final ProcessingRecipeSerializer INSTANCE = new ProcessingRecipeSerializer();

    @Override
    public ProcessingRecipe fromJson(ResourceLocation id, JsonObject json) {
        // Ingredient
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        int ingredientAmount = json.has("ingredientAmount") ? json.get("ingredientAmount").getAsInt() : 1;

        // Result
        ItemStack result;
        if (json.get("result").isJsonObject()) {
            JsonObject resultJson = json.getAsJsonObject("result");
            result = new ItemStack(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(
                            ResourceLocation.tryParse(resultJson.get("item").getAsString())
                    ))
            );
        } else {
            // result ist ein String
            String resultItem = json.get("result").getAsString();
            result = new ItemStack(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(resultItem)))
            );
        }

        // Menge
        int resultAmount = json.has("resultAmount") ? json.get("resultAmount").getAsInt() : 1;
        result.setCount(resultAmount);

        int time = json.has("time") ? json.get("time").getAsInt() : 0;
        ProcessingMode mode = ProcessingMode.fromString(json.has("mode") ? json.get("mode").getAsString() : "drying");

        return new ProcessingRecipe(id, ingredient, result, time, ModRecipeTypes.PROCESSING.get(), mode, ingredientAmount);
    }

    @Override
    public ProcessingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int ingredientAmount = buf.readInt(); // <-- hier

        ItemStack result = buf.readItem();
        int resultAmount = buf.readInt();      // <-- hier
        result.setCount(resultAmount);         // Menge setzen

        int time = buf.readInt();
        ProcessingMode mode = ProcessingMode.fromString(buf.readUtf());

        RecipeType<ProcessingRecipe> type = ModRecipeTypes.PROCESSING.get();

        return new ProcessingRecipe(id, ingredient, result, time, type, mode, ingredientAmount);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, ProcessingRecipe recipe) {
        recipe.ingredient.toNetwork(buf);
        buf.writeInt(recipe.getIngredientAmount());      // <-- schreiben
        buf.writeItem(recipe.getResultItem(null));
        buf.writeInt(recipe.getResultItem(null).getCount()); // <-- schreiben
        buf.writeInt(recipe.getTime());
        buf.writeUtf(recipe.getMode().getId());
    }
}