package com.fireblaze.extra_steps.recipe;

import com.fireblaze.extra_steps.item.LyeWaterGlassBottleItem;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipes;
import com.fireblaze.extra_steps.registry.ModTags;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ColorableTerracottaRecipe implements CraftingRecipe {

    private final ResourceLocation id;
    private final CraftingBookCategory category;

    public ColorableTerracottaRecipe(ResourceLocation id, CraftingBookCategory category) {
        this.id = id;
        this.category = category;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {

        if (inv.getWidth() != 3 || inv.getHeight() != 3) {
            return false;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {

                int index = x + y * 3;
                ItemStack stack = inv.getItem(index);

                // Mitte → Bottle
                if (x == 1 && y == 1) {
                    if (!(stack.getItem() instanceof LyeWaterGlassBottleItem)) {
                        CompoundTag tag = stack.getTag();
                        if (tag != null) {
                            if (!tag.contains(GenericColorHelper.COLOR_TAG)) return false;
                        }
                        else return false;
                    }
                }
                // Außen → Terracotta (egal welche Variante)
                else {
                    if (!stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta")))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(ModBlocks.COLORABLE_TERRACOTTA.get(), 8);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof LyeWaterGlassBottleItem) {
                // Farbe übertragen
                GenericColorHelper.copyColor(stack, result);
                break;
            }
        }

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof LyeWaterGlassBottleItem) {

                // WICHTIG: nur 1 Item aus dem Stack verarbeiten
                ItemStack single = stack.copy();
                single.setCount(1);

                if (single.hasTag()) {
                    int interactions = single.getTag().getInt(GenericColorHelper.AVAILABLE_INTERACTIONS) - 1;

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
        ItemStack previewTerracotta = new ItemStack(ModBlocks.COLORABLE_TERRACOTTA.get().asItem(), 8);
        previewTerracotta.getOrCreateTag().putBoolean("preview_rainbow", true);
        return previewTerracotta;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COLORABLE_TERRACOTTA.get();
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

        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        ItemStack previewBottle = new ItemStack(ModItems.LYE_WATER_GLASS_BOTTLE.get());
        previewBottle.getOrCreateTag().putBoolean("preview_rainbow", true);
        list.add(Ingredient.of(previewBottle));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));
        list.add(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "terracotta"))));

        return list;
    }
}