package com.fireblaze.extra_steps.recipe;

import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModRecipes;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ColorableStainedGlassPanesFromStainedGlassRecipe extends ShapedRecipe {
    private final ResourceLocation id;
    private final CraftingBookCategory category;
    public ColorableStainedGlassPanesFromStainedGlassRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(
                id,
                "",
                category,
                3, // width
                2, // height
                NonNullList.of(
                        Ingredient.EMPTY,
                        Ingredient.of(ModBlocks.COLORABLE_STAINED_GLASS.get()),
                        Ingredient.of(ModBlocks.COLORABLE_STAINED_GLASS.get()),
                        Ingredient.of(ModBlocks.COLORABLE_STAINED_GLASS.get()),
                        Ingredient.of(ModBlocks.COLORABLE_STAINED_GLASS.get()),
                        Ingredient.of(ModBlocks.COLORABLE_STAINED_GLASS.get()),
                        Ingredient.of(ModBlocks.COLORABLE_STAINED_GLASS.get())
                ),
                new ItemStack(ModBlocks.COLORABLE_STAINED_GLASS_PANE.get(), 16)
        );
        this.id = id;
        this.category = category;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        // Vanilla Pattern Check (inkl. verschieben + spiegeln)
        if (!super.matches(inv, level)) {
            return false;
        }

        CompoundTag referenceColor = null;

        // Nur die Slots prüfen, die tatsächlich im Rezept liegen
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty()) continue;

            if (!stack.is(ModBlocks.COLORABLE_STAINED_GLASS.get().asItem())) {
                return false;
            }

            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains(GenericColorHelper.COLOR_TAG)) {
                return false;
            }

            CompoundTag color = tag.getCompound(GenericColorHelper.COLOR_TAG);

            if (referenceColor == null) {
                referenceColor = color;
            } else {
                if (!referenceColor.equals(color)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {

        ItemStack result = new ItemStack(
                ModBlocks.COLORABLE_STAINED_GLASS_PANE.get(),
                16
        );

        // komplette NBT übernehmen
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (!stack.isEmpty() && stack.hasTag()) {
                result.setTag(stack.getTag().copy());
                break;
            }
        }

        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack preview = new ItemStack(
                ModBlocks.COLORABLE_STAINED_GLASS_PANE.get(),
                16
        );
        preview.getOrCreateTag().putBoolean("preview_rainbow", true);
        return preview;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COLORABLE_STAINED_GLASS_PANE_FROM_GLASS.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        ItemStack preview = new ItemStack(ModBlocks.COLORABLE_STAINED_GLASS.get());
        preview.getOrCreateTag().putBoolean("preview_rainbow", true);

        for (int i = 0; i < 6; i++) {
            list.add(Ingredient.of(preview));
        }

        return list;
    }
}