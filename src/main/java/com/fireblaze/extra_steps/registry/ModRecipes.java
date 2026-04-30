package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
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

    public static final RegistryObject<RecipeSerializer<?>> COLORABLE_WOOL =
            RECIPE_SERIALIZERS.register("colorable_wool",
                    () -> new SimpleCraftingRecipeSerializer<>(ColorableWoolRecipe::new));

    public static final RegistryObject<RecipeSerializer<?>> COLORABLE_STAINED_GLASS =
            RECIPE_SERIALIZERS.register("colorable_stained_glass",
                    () -> new SimpleCraftingRecipeSerializer<>(ColorableStainedGlassRecipe::new));

    public static final RegistryObject<RecipeSerializer<?>> COLORABLE_STAINED_GLASS_PANE =
            RECIPE_SERIALIZERS.register("colorable_stained_glass_pane",
                    () -> new SimpleCraftingRecipeSerializer<>(ColorableStainedGlassPanesRecipe::new));

    public static final RegistryObject<RecipeSerializer<?>> COLORABLE_STAINED_GLASS_PANE_FROM_GLASS =
            RECIPE_SERIALIZERS.register("colorable_stained_glass_pane_from_stained_glass",
                    () -> new SimpleCraftingRecipeSerializer<>((id, category) -> new ColorableStainedGlassPanesFromStainedGlassRecipe(id, category)));

    public static final RegistryObject<RecipeSerializer<?>> COLORABLE_TERRACOTTA =
            RECIPE_SERIALIZERS.register("colorable_terracotta",
                    () -> new SimpleCraftingRecipeSerializer<>(ColorableTerracottaRecipe::new));

    public static final RegistryObject<RecipeSerializer<?>> COLORABLE_CONCRETE =
            RECIPE_SERIALIZERS.register("colorable_concrete",
                    () -> new SimpleCraftingRecipeSerializer<>(ColorableConreteRecipe::new));
}