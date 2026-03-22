package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.processing.ProcessingRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "extra_steps");

    public static final RegistryObject<RecipeSerializer<ProcessingRecipe>> PROCESSING =
            SERIALIZERS.register("processing", () -> ProcessingRecipeSerializer.INSTANCE);
}