package com.fireblaze.extra_steps.fluid;

import com.fireblaze.extra_steps.blockentity.LyeWaterCauldronBlockEntity;
import com.fireblaze.extra_steps.client.color.GenericColors;
import com.fireblaze.extra_steps.item.ModLeatherArmorItem;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class ModCauldronInteractions {

    public static final Map<Item, CauldronInteraction> LYE_WATER =
            CauldronInteraction.newInteractionMap();

    public static void registerCauldronInteractions(Level level2) {

        CauldronInteraction.EMPTY.put(ModItems.LYE_WATER_BUCKET.get(), (state, level, pos, player, hand, stack) -> {

            if (!level.isClientSide) {

                // 🔥 1. NBT vorbereiten
                CompoundTag beTag = new CompoundTag();

                if (stack.hasTag()) {
                    CompoundTag itemTag = stack.getTag();
                    for (String key : itemTag.getAllKeys()) {
                        beTag.put(key, itemTag.get(key).copy());
                    }
                }

                // 🔥 2. Block setzen
                BlockState newState = ModBlocks.LYE_WATER_CAULDRON.get()
                        .defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL, 3);

                level.setBlockAndUpdate(pos, newState);

                // 🔥 3. BE initialisieren
                if (level.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {
                    be.load(beTag);
                    be.setChanged();
                    be.sync();
                }

                // Bucket zurückgeben
                player.setItemInHand(hand,
                        ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
            }

            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);

            return InteractionResult.sidedSuccess(level.isClientSide);
        });

        LYE_WATER.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {

            if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide) {


                ItemStack result = ModItems.LYE_WATER_BUCKET.get().getDefaultInstance();


                if (level.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {
                    CompoundTag tag = be.getUpdateTag(); // Tag sicher erstellen
                    if (tag.contains(GenericColorHelper.FILL_FACTOR)) {
                        if (tag.getFloat(GenericColorHelper.FILL_FACTOR) != 0f) {
                            GenericColorHelper.copyColorFromLyeWater(be, result, false);
                        } else {
                            GenericColorHelper.copyColorFromLyeWater(be, result, true);
                        }
                    } else {
                        GenericColorHelper.copyColorFromLyeWater(be, result, true);
                    }
                }



                //player.setItemInHand(hand, ModItems.LYE_WATER_BUCKET.get().getDefaultInstance());
                player.setItemInHand(hand, result);
                level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            }

            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);

            return InteractionResult.sidedSuccess(level.isClientSide);
        });

        // Alle CLEANING-Rezepte holen
        List<ProcessingRecipe> cleaningRecipes = level2.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(r -> r.getMode() == ProcessingMode.CLEANING)
                .toList();

        for (ProcessingRecipe recipe : cleaningRecipes) {
            for (ItemStack itemStack : recipe.ingredient.getItems()) {
                Item item = itemStack.getItem();

                LYE_WATER.put(item, (state, level, pos, player, hand, stack) -> {
                    int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);

                    if (currentLevel == 0) return InteractionResult.PASS;

                    if (!level.isClientSide) {
                        ItemStack result = recipe.getResultItem(null).copy();

                        // 🔥 Farbe übernehmen (falls vorhanden)
                        if (level.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {
                            CompoundTag tag = be.getUpdateTag(); // Tag sicher erstellen
                            if (tag.contains(GenericColorHelper.FILL_FACTOR)) {
                                if (tag.getFloat(GenericColorHelper.FILL_FACTOR) != 0f) {
                                    if (stack.is(ModItems.SCRAPED_HIDE.get())) return InteractionResult.PASS; //todo filtern nach colorable items

                                    GenericColorHelper.copyColorFromLyeWater(be, result, false);
                                }
                                else {
                                    if (stack.hasTag() && !Objects.requireNonNull(stack.getTag()).contains("color", GenericColors.WHITE.getGenericColor())) {
                                        assert tag != null;
                                        if (tag.contains(GenericColorHelper.FILL_FACTOR)) {
                                            if (tag.getFloat(GenericColorHelper.FILL_FACTOR) != 0f) GenericColorHelper.copyColorFromLyeWater(be, result, false);
                                            else GenericColorHelper.copyColor(stack, result);
                                        }
                                    } else {
                                        if (!stack.is(ModItems.SCRAPED_HIDE.get())) GenericColorHelper.copyColorFromLyeWater(be, result, false);
                                    }
                                }
                            }

                            if (recipe.getResultItem(null).getItem() instanceof DyeableLeatherItem) {
                                ModLeatherArmorItem.setArmorColor(result, be.getColor());
                                stack.getItem().setDamage(result, result.getItem().getDamage(stack));
                            }
                        }
                        //GenericColorHelper.copyColor(stack, result);
                        // Item umwandeln
                        stack.shrink(1);

                        // --- Prüfen, ob Inventar voll ist ---
                        boolean added = player.getInventory().add(result);
                        if (!added) {
                            // Inventar voll → Item droppen lassen
                            player.drop(result, false);
                        }
                        player.level().playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5f, 1.0f);

                        // Level verringern oder Block auf leeren Cauldron setzen
                        if (level.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {

                            boolean shouldDrain = be.consumeInteraction(currentLevel);

                            if (shouldDrain) {
                                be.resetInteractionCount(currentLevel);

                                if (currentLevel == 1) {
                                    level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                                } else {
                                    level.setBlockAndUpdate(pos,
                                            state.setValue(LayeredCauldronBlock.LEVEL, currentLevel - 1));
                                }
                            }
                        }
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                });
            }
        }

        // Alle LYE_WATER-Rezepte holen
        List<ProcessingRecipe> lyeWaterRecipes = level2.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(r -> r.getMode() == ProcessingMode.LYE_WATER)
                .toList();

        for (ProcessingRecipe recipe : lyeWaterRecipes) {
            for (ItemStack itemStack : recipe.ingredient.getItems()) {
                Item item = itemStack.getItem();
                CauldronInteraction.WATER.put(item, (state, level, pos, player, hand, stack) -> {

                    if (!level.isClientSide) {
                        // 1. Ash im Inventar reduzieren
                        stack.shrink(1);

                        // 2. Aktuelles Level aus dem Wasser-Cauldron übernehmen
                        int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);

                        // 3. Block auf Lye Water Cauldron setzen mit dem übernommenen Level
                        level.setBlockAndUpdate(pos,
                                ModBlocks.LYE_WATER_CAULDRON.get()
                                        .defaultBlockState()
                                        .setValue(LayeredCauldronBlock.LEVEL, currentLevel)
                        );

                        // Optional: Sound abspielen
                        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.5f, 1.0f);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                });
            }
        }

        // COLORING

        Map<GenericColors, Item> dyeMap = new HashMap<>();

        dyeMap.put(GenericColors.WHITE, Items.WHITE_DYE);
        dyeMap.put(GenericColors.ORANGE, Items.ORANGE_DYE);
        dyeMap.put(GenericColors.MAGENTA, Items.MAGENTA_DYE);
        dyeMap.put(GenericColors.LIGHT_BLUE, Items.LIGHT_BLUE_DYE);
        dyeMap.put(GenericColors.YELLOW, Items.YELLOW_DYE);
        dyeMap.put(GenericColors.LIME, Items.LIME_DYE);
        dyeMap.put(GenericColors.PINK, Items.PINK_DYE);
        dyeMap.put(GenericColors.GRAY, Items.GRAY_DYE);
        dyeMap.put(GenericColors.LIGHT_GRAY, Items.LIGHT_GRAY_DYE);
        dyeMap.put(GenericColors.CYAN, Items.CYAN_DYE);
        dyeMap.put(GenericColors.PURPLE, Items.PURPLE_DYE);
        dyeMap.put(GenericColors.BLUE, Items.BLUE_DYE);
        dyeMap.put(GenericColors.BROWN, Items.BROWN_DYE);
        dyeMap.put(GenericColors.GREEN, Items.GREEN_DYE);
        dyeMap.put(GenericColors.RED, Items.RED_DYE);
        dyeMap.put(GenericColors.BLACK, Items.BLACK_DYE);

        for (var entry : dyeMap.entrySet()) {
            GenericColors color = entry.getKey();
            Item dyeItem = entry.getValue();

            LYE_WATER.put(dyeItem, (state, world, pos, player, hand, stack) -> {
                if (world.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {
                    if (be.getFull()) return InteractionResult.CONSUME;
                    int level = state.getValue(LayeredCauldronBlock.LEVEL);

                    world.setBlockAndUpdate(pos,
                            ModBlocks.LYE_WATER_CAULDRON.get()
                                    .defaultBlockState()
                                    .setValue(LayeredCauldronBlock.LEVEL, level)
                    );

                    if(!be.mixColor(color.getGenericColor(), level)) return InteractionResult.CONSUME;
                    world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.5f, 1.0f);
                    stack.shrink(1);
                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
                return InteractionResult.PASS;
            });
        }

        LYE_WATER.put(ModItems.WOOL_BRUSH.get(), (state, world, pos, player, hand, stack) -> {
            if (world.getBlockEntity(pos) instanceof LyeWaterCauldronBlockEntity be) {
                int level = state.getValue(LayeredCauldronBlock.LEVEL) - 1;

                if (level == 0) world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                else {
                    world.setBlockAndUpdate(pos,
                            ModBlocks.LYE_WATER_CAULDRON.get()
                                    .defaultBlockState()
                                    .setValue(LayeredCauldronBlock.LEVEL, level)
                    );
                }

                be.setChanged();

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
            return InteractionResult.PASS;
        });
    }
}
