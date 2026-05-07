package com.fireblaze.extra_steps.blockentity;

import com.fireblaze.extra_steps.block.WoodenCauldronBlock;
import com.fireblaze.extra_steps.config.ModConfigHandler;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class WoodenCauldronBlockEntity extends BlockEntity {

    private int tickCounter = 0; // Zählt die Ticks
    private final ItemStackHandler inventory = new ItemStackHandler(20);

    public WoodenCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WOODEN_CAULDRON_BE.get(), pos, state);
    }
    public ItemStackHandler getInventory() {
        return inventory;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodenCauldronBlockEntity be) {
        if (level.isClientSide) return; // nur Server-Seite

        // Tick-Counter hochzählen
        be.tickCounter++;
        // Prüfe nur alle 10 Sekunden (20 Ticks * 10 Sekunden = 200)
        if (be.tickCounter < ModConfigHandler.cauldronFillCheckInterval.get()) return;
        be.tickCounter = 0; // Reset Counter

        if (!level.isRainingAt(pos.above())) return;

        // Randomizer: Chance, dass der Cauldron tatsächlich steigt (z.B. 1/4)
        if (level.random.nextInt(ModConfigHandler.cauldronFillChance.get()) != 0) return;

        BlockState newState;
        if (state.getBlock() == ModBlocks.WOODEN_WATER_CAULDRON.get()) {
            int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
            if (currentLevel < 3) {
                newState = state.setValue(LayeredCauldronBlock.LEVEL, currentLevel + 1);
                level.setBlock(pos, newState, 2);
            }
        } else {
            newState = ModBlocks.WOODEN_WATER_CAULDRON.get()
                    .defaultBlockState()
                    .setValue(LayeredCauldronBlock.LEVEL, 1);
            level.setBlock(pos, newState, 2);
        }
    }

    /*
    public Optional<ProcessingRecipe> getMatchingMixingRecipe() {

        if (level == null) return Optional.empty();

        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) inputs.add(stack);
        }

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(r -> r.getMode() == ProcessingMode.MIXING)
                .filter(r -> matchesRecipe(r, inputs))
                .findFirst();
    }

    private boolean matchesRecipe(ProcessingRecipe recipe, List<ItemStack> inputs) {

        List<Ingredient> ingredients = recipe.getIngredients();

        // Kopie der Inputs (wichtig!)
        List<ItemStack> remaining = new ArrayList<>();

        for (ItemStack stack : inputs) {
            if (!stack.isEmpty()) {
                // Stack mehrfach zählen (Count beachten!)
                for (int i = 0; i < stack.getCount(); i++) {
                    remaining.add(stack.copy());
                }
            }
        }

        for (Ingredient ingredient : ingredients) {

            boolean found = false;

            Iterator<ItemStack> it = remaining.iterator();

            while (it.hasNext()) {
                ItemStack stack = it.next();

                if (ingredient.test(stack)) {
                    it.remove(); // verbraucht genau 1 Item
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }



    public boolean isValidMixingIngredient(ItemStack stack) {

        if (level == null) return false;

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(r -> r.getMode() == ProcessingMode.MIXING)
                .anyMatch(recipe ->
                        recipe.getIngredients().stream()
                                .anyMatch(ingredient -> ingredient.test(stack))
                );
    }

     */
}