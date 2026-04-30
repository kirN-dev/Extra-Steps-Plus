package com.fireblaze.extra_steps.recipe;

import com.fireblaze.extra_steps.item.LyeWaterGlassBottleItem;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipes;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ColorableConreteRecipe implements CraftingRecipe {

    private final ResourceLocation id;
    private final CraftingBookCategory category;

    public ColorableConreteRecipe(ResourceLocation id, CraftingBookCategory category) {
        this.id = id;
        this.category = category;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int bottleCount = 0;
        int sandCount = 0;
        int gravelCount = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof LyeWaterGlassBottleItem && (stack.hasTag()) && (stack.getTag().contains(GenericColorHelper.COLOR_TAG))) {
                bottleCount++;
            } else if (stack.is(Items.SAND)) {
                sandCount++;
            } else if (stack.is(Items.GRAVEL)) {
                gravelCount++;
            } else {
                // ein unerwartetes Item → kein Match
                return false;
            }
        }

        return bottleCount == 1 && sandCount == 4 && gravelCount == 4;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(ModBlocks.COLORABLE_CONCRETE.get(), 8);

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
        ItemStack previewConcrete = new ItemStack(ModBlocks.COLORABLE_CONCRETE.get().asItem(), 8);
        previewConcrete.getOrCreateTag().putBoolean("preview_rainbow", true);
        return previewConcrete;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COLORABLE_CONCRETE.get();
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

        // 4 Sand
        for (int i = 0; i < 4; i++) {
            list.add(Ingredient.of(Items.SAND));
        }

        // 4 Gravel
        for (int i = 0; i < 4; i++) {
            list.add(Ingredient.of(Items.GRAVEL));
        }

        return list;
    }
}