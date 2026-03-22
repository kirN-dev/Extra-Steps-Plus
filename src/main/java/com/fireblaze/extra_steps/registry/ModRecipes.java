package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    // <-- Hier die DeferredRegister definieren
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExtraSteps.MODID);

    // Rezept-Serializer registrieren
    public static final RegistryObject<RecipeSerializer<RawWoolFromCrumbledWoolRecipe>> RAW_WOOL_FROM_CRUMBLED_WOOL =
            RECIPE_SERIALIZERS.register("raw_wool_from_crumbled_wool",
                    RawWoolFromCrumbledWoolRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<?>> STRINGS_FROM_FIBERED_WOOL =
            RECIPE_SERIALIZERS.register("strings_from_fibered_wool",
                    StringsFromFiberedWoolRecipeSerializer::new);
}