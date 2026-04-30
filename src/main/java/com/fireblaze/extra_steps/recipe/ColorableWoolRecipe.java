package com.fireblaze.extra_steps.recipe;

import com.fireblaze.extra_steps.item.LyeWaterGlassBottleItem;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipes;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ColorableWoolRecipe implements CraftingRecipe {

    private final ResourceLocation id;
    private final CraftingBookCategory category;

    public ColorableWoolRecipe(ResourceLocation id, CraftingBookCategory category) {
        this.id = id;
        this.category = category;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int bottleCount = 0;
        int woolCount = 0;
        int availableInteractions = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof LyeWaterGlassBottleItem
                    && stack.hasTag()
                    && stack.getTag().contains(GenericColorHelper.COLOR_TAG)) {

                bottleCount++;

                // 🔥 Interactions auslesen
                availableInteractions = stack.getTag().getInt(GenericColorHelper.AVAILABLE_INTERACTIONS);

            } else if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "wool")))) {
                woolCount++;
            } else {
                return false;
            }
        }

        // 🔥 Bedingungen:
        // 1 Bottle
        // 1–3 Wool
        // genug Interactions vorhanden
        return bottleCount == 1
                && woolCount >= 1
                && woolCount <= 3
                && woolCount <= availableInteractions;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        int woolCount = 0;
        ItemStack bottle = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "wool")))) {
                woolCount++;
            } else if (stack.getItem() instanceof LyeWaterGlassBottleItem) {
                bottle = stack;
            }
        }

        ItemStack result = new ItemStack(ModBlocks.COLORABLE_WOOL.get(), woolCount);

        if (!bottle.isEmpty()) {
            GenericColorHelper.copyColor(bottle, result);
        }

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        int woolCount = 0;

        // zuerst zählen
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "wool")))) {
                woolCount++;
            }
        }

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof LyeWaterGlassBottleItem) {
                ItemStack single = stack.copy();
                single.setCount(1);

                if (single.hasTag()) {
                    int interactions = single.getTag().getInt(GenericColorHelper.AVAILABLE_INTERACTIONS) - woolCount;

                    if (interactions <= 0) {
                        remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        single.getOrCreateTag().putInt(GenericColorHelper.AVAILABLE_INTERACTIONS, interactions);
                        remaining.set(i, single);
                    }
                } else {
                    remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack previewWool = new ItemStack(ModBlocks.COLORABLE_WOOL.get().asItem());
        previewWool.getOrCreateTag().putBoolean("preview_rainbow", true);
        return previewWool;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COLORABLE_WOOL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        ItemStack previewBottle = new ItemStack(ModItems.LYE_WATER_GLASS_BOTTLE.get());
        previewBottle.getOrCreateTag().putBoolean("preview_rainbow", true);
        list.add(Ingredient.of(previewBottle));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "wool"))));

        return list;
    }
}