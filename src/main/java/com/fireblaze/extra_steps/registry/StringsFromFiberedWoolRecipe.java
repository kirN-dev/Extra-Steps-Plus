package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;

import java.util.ArrayList;
import java.util.List;

public class StringsFromFiberedWoolRecipe implements CraftingRecipe {
    private final ResourceLocation id;

    public StringsFromFiberedWoolRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean hasFiberedWool = false;
        boolean hasShears = false;
        int items = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            items++;

            if (stack.getItem() == ModItems.FIBERED_WOOL.get()) {
                hasFiberedWool = true;
            }
            else if (stack.canPerformAction(ToolActions.SHEARS_CARVE)) {
                hasShears = true;
            }
            else {
                return false;
            }
        }

        return items == 2 && hasFiberedWool && hasShears;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(Items.STRING, 4);

        // Durch das Inventar iterieren, die Crumbled Wool finden
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == ModItems.FIBERED_WOOL.get()) {
                break; // nur das erste Crumbled Wool nehmen
            }
        }

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.canPerformAction(ToolActions.SHEARS_CARVE)) {
                ItemStack copy = stack.copy();
                copy.setDamageValue(copy.getDamageValue() + 1);

                if (copy.getDamageValue() >= copy.getMaxDamage()) {
                    copy = ItemStack.EMPTY;
                }

                remaining.set(i, copy);
            }
        }

        return remaining;
    }

    public static List<ItemStack> getAllShearTools() {
        List<ItemStack> list = new ArrayList<>();

        for (var item : net.minecraftforge.registries.ForgeRegistries.ITEMS.getValues()) {
            ItemStack stack = new ItemStack(item);

            if (stack.canPerformAction(ToolActions.SHEARS_CARVE)) {
                list.add(stack);
            }
        }

        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(Items.STRING, 4);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRINGS_FROM_FIBERED_WOOL.get();
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

        list.add(Ingredient.of(ModItems.FIBERED_WOOL.get()));

        list.add(Ingredient.of(getAllShearTools().stream()));

        return list;
    }
}