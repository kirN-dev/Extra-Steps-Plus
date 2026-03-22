package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ExtraSteps.MODID);

    // Ein RecipeType für alles
    public static final RegistryObject<RecipeType<ProcessingRecipe>> PROCESSING =
            RECIPE_TYPES.register("processing",
                    () -> new RecipeType<ProcessingRecipe>() {
                        @Override public String toString() {
                            return ExtraSteps.MODID + ":processing";
                        }
                    });
}