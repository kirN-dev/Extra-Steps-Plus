package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class RawWoolFromCrumbledWoolRecipeSerializer implements RecipeSerializer<RawWoolFromCrumbledWoolRecipe> {

    @Override
    public RawWoolFromCrumbledWoolRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new RawWoolFromCrumbledWoolRecipe(id);
    }

    @Nullable
    @Override
    public RawWoolFromCrumbledWoolRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return new RawWoolFromCrumbledWoolRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, RawWoolFromCrumbledWoolRecipe recipe) {
        // keine Daten zu senden nötig
    }
}