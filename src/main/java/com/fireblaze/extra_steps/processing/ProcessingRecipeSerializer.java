package com.fireblaze.extra_steps.processing;

import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProcessingRecipeSerializer implements RecipeSerializer<ProcessingRecipe> {

    public static final ProcessingRecipeSerializer INSTANCE = new ProcessingRecipeSerializer();

    @Override
    public ProcessingRecipe fromJson(ResourceLocation id, JsonObject json) {
        // 🔹 Ingredients (alt + neu)
        List<ProcessingRecipe.CountedIngredient> ingredients = new ArrayList<>();

        if (json.has("ingredients")) {
            // 👉 Neues System (Liste)
            for (var element : json.getAsJsonArray("ingredients")) {

                JsonObject obj = element.getAsJsonObject();

                Ingredient ingredient = Ingredient.fromJson(obj.has("ingredient") ? obj.get("ingredient") : obj);

                int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

                ingredients.add(new ProcessingRecipe.CountedIngredient(ingredient, count));
            }
        } else if (json.has("ingredient")) {

            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            ingredients.add(new ProcessingRecipe.CountedIngredient(ingredient, 1));
        } else {
            throw new IllegalStateException("Recipe " + id + " has no 'ingredient' or 'ingredients' field!");
        }

        int ingredientAmount = json.has("ingredientAmount")
                ? json.get("ingredientAmount").getAsInt()
                : ingredients.get(0).count();

        // 🔹 Result
        ItemStack result;
        if (json.get("result").isJsonObject()) {
            JsonObject resultJson = json.getAsJsonObject("result");
            result = new ItemStack(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(
                            ResourceLocation.tryParse(resultJson.get("item").getAsString())
                    ))
            );
        } else {
            String resultItem = json.get("result").getAsString();
            result = new ItemStack(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(
                            ResourceLocation.tryParse(resultItem)
                    ))
            );
        }

        // 🔹 Result-Menge
        int resultAmount = json.has("resultAmount")
                ? json.get("resultAmount").getAsInt()
                : (json.get("result").isJsonObject() && json.getAsJsonObject("result").has("count") ? json.getAsJsonObject("result").get("count").getAsInt() : 1);
        result.setCount(resultAmount);

        // 🔹 Zeit & Mode
        int time = json.has("time") ? json.get("time").getAsInt() : 0;
        int stirCount = json.has("interaction_count") ? json.get("interaction_count").getAsInt() : json.has("stir_count")
                ? json.get("stir_count").getAsInt()
                : 3;

        ProcessingMode mode = ProcessingMode.fromString(
                json.has("mode") ? json.get("mode").getAsString() : "drying"
        );

        int waterCost = json.has("water_level_consumption")
                ? json.get("water_level_consumption").getAsInt()
                : 0;

        if (mode == ProcessingMode.SCRAPING && (ingredients.size() != 1 || ingredients.get(0).count() != 1 || ingredientAmount != 1))
            throw new IllegalArgumentException("Scraping requires a single input item");
        return new ProcessingRecipe(
                id,
                ingredients,
                result,
                time,
                ModRecipeTypes.PROCESSING.get(),
                mode,
                ingredientAmount,
                waterCost,
                stirCount
        ).withScrapingSettings(new ScrapingSettings(json));
    }

    @Override
    public ProcessingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        int size = buf.readInt();

        List<ProcessingRecipe.CountedIngredient> ingredients = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Ingredient ing = Ingredient.fromNetwork(buf);
            int count = buf.readInt();

            ingredients.add(new ProcessingRecipe.CountedIngredient(ing, count));
        }

        int ingredientAmount = buf.readInt();

        ItemStack result = buf.readItem();
        int resultAmount = buf.readInt();
        result.setCount(resultAmount);

        int time = buf.readInt();
        int waterLevelReduction = buf.readInt();
        int stirCount = buf.readInt();
        ProcessingMode mode = ProcessingMode.fromString(buf.readUtf());

        RecipeType<ProcessingRecipe> type = ModRecipeTypes.PROCESSING.get();

        return new ProcessingRecipe(
                id,
                ingredients,
                result,
                time,
                type,
                mode,
                ingredientAmount,
                waterLevelReduction,
                stirCount
        ).withScrapingSettings(new ScrapingSettings(com.google.gson.JsonParser.parseString(buf.readUtf()).getAsJsonObject()));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, ProcessingRecipe recipe) {
        buf.writeInt(recipe.getIngredients().size());

        for (ProcessingRecipe.CountedIngredient ci : recipe.getCountedIngredients()) {
            ci.ingredient().toNetwork(buf);
            buf.writeInt(ci.count());
        }
        buf.writeInt(recipe.getIngredientAmount());      // <-- schreiben
        buf.writeItem(recipe.getResultItem(null));
        buf.writeInt(recipe.getResultItem(null).getCount()); // <-- schreiben
        buf.writeInt(recipe.getTime());
        buf.writeInt(recipe.getWaterLevelReduction());
        buf.writeInt(recipe.getStirCount());
        buf.writeUtf(recipe.getMode().getId());
        buf.writeUtf(recipe.getScrapingSettings().json.toString());
    }
}