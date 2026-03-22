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
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        ItemStack result = new ItemStack(
                Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(json.get("result").getAsString())))
        );
        int time = json.get("time").getAsInt();

        // Mode aus JSON
        String modeString = json.has("mode") ? json.get("mode").getAsString() : "drying";
        ProcessingMode mode = ProcessingMode.fromString(modeString);

        // Ein RecipeType für alle Processing-Rezepte
        RecipeType<ProcessingRecipe> type = ModRecipeTypes.PROCESSING.get();

        return new ProcessingRecipe(id, ingredient, result, time, type, mode);
    }

    @Override
    public ProcessingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        int time = buf.readInt();

        // Mode aus Netzwerk
        ProcessingMode mode = ProcessingMode.fromString(buf.readUtf());

        RecipeType<ProcessingRecipe> type = ModRecipeTypes.PROCESSING.get();
        return new ProcessingRecipe(id, ingredient, result, time, type, mode);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, ProcessingRecipe recipe) {
        recipe.ingredient.toNetwork(buf);
        buf.writeItem(recipe.getResultItem(null));
        buf.writeInt(recipe.getTime());
        buf.writeUtf(recipe.getMode().getId()); // Mode mitschreiben
    }
}