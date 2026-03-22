package com.fireblaze.extra_steps.registry;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class StringsFromFiberedWoolRecipeSerializer implements RecipeSerializer<StringsFromFiberedWoolRecipe> {

    @Override
    public StringsFromFiberedWoolRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new StringsFromFiberedWoolRecipe(id);
    }

    @Override
    public StringsFromFiberedWoolRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return new StringsFromFiberedWoolRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, StringsFromFiberedWoolRecipe recipe) {}
}