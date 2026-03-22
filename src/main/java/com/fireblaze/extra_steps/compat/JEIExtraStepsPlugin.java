package com.fireblaze.extra_steps.compat;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIExtraStepsPlugin implements IModPlugin {


    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();

        for (ProcessingMode mode : ProcessingMode.values()) {
            registration.addRecipeCategories(
                    new ProcessingRecipeCategory(helper, mode)
            );
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) return;

        var manager = level.getRecipeManager();
        var allRecipes = manager.getAllRecipesFor(
                com.fireblaze.extra_steps.registry.ModRecipeTypes.PROCESSING.get()
        );

        for (ProcessingMode mode : ProcessingMode.values()) {

            var filtered = allRecipes.stream()
                    .filter(r -> r.getMode() == mode)
                    .toList();

            registration.addRecipes(
                    ProcessingRecipeCategory.getRecipeType(mode),
                    filtered
            );
        }

        var craftingRecipes = manager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);

        var woolRecipe = craftingRecipes.stream()
                .filter(r -> r.getId().getPath().contains("raw_wool_from_crumbled_wool"))
                .toList();

        registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, woolRecipe);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (ProcessingMode mode : ProcessingMode.values()) {
            registration.addRecipeCatalyst(
                    JEIHelper.getIconForMode(mode),
                    ProcessingRecipeCategory.getRecipeType(mode)
            );
        }
    }
}
