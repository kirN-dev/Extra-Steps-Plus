package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.WoodenCauldronBlockEntity;
import com.fireblaze.extra_steps.cauldron.interaction.WoodenCauldronInteractions;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoodenWaterCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public WoodenWaterCauldronBlock(Properties properties) {
        super(properties, item -> false, WoodenCauldronInteractions.WOODEN_WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenCauldronBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.WOODEN_CAULDRON_BE.get()) {
            // Safe cast: wir wissen, dass T WoodenCauldronBlockEntity ist
            return (lvl, pos, st, be) -> WoodenCauldronBlockEntity.tick(lvl, pos, st, (WoodenCauldronBlockEntity) be);
        }
        return null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(ModBlocks.WOODEN_CAULDRON.get().asItem());
    }

    /*
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        InteractionResult vanillaResult = super.use(state, level, pos, player, hand, hit);
        if (vanillaResult.consumesAction()) {
            return vanillaResult;
        }

        ItemStack held = player.getItemInHand(hand);

        // 🔹 Leere Hand → Items rausnehmen
        if (held.isEmpty()) {

            if (level.isClientSide) return InteractionResult.SUCCESS;

            if (!(level.getBlockEntity(pos) instanceof WoodenCauldronBlockEntity be)) {
                return InteractionResult.PASS;
            }

            // von hinten nach vorne durchgehen (optional schöner)
            for (int i = be.getInventory().getSlots() - 1; i >= 0; i--) {

                ItemStack stack = be.getInventory().getStackInSlot(i);

                if (!stack.isEmpty()) {

                    ItemStack extracted = be.getInventory().extractItem(i, stack.getCount(), false);

                    ItemEntity entity = new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 0.75,
                            pos.getZ() + 0.5,
                            extracted
                    );

                    level.addFreshEntity(entity);

                    level.playSound(null, pos,
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.BLOCKS,
                            0.7f,
                            1.0f
                    );

                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.SUCCESS;
        }

        // 🔹 Item einfügen (wenn KEINE Schaufel)
        if (!(held.getItem() instanceof ShovelItem)) {

            if (!(level.getBlockEntity(pos) instanceof WoodenCauldronBlockEntity be)) {
                return InteractionResult.PASS;
            }

            // ❌ Ungültige Items blockieren
            if (!be.isValidMixingIngredient(held)) {
                return InteractionResult.SUCCESS;
            }

            // Item einfügen
            for (int i = 0; i < be.getInventory().getSlots(); i++) {
                if (be.getInventory().getStackInSlot(i).isEmpty()) {
                    be.getInventory().insertItem(i, held.split(1), false);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (!(level.getBlockEntity(pos) instanceof WoodenCauldronBlockEntity be)) {
            return InteractionResult.PASS;
        }
        Optional<ProcessingRecipe> recipeOpt = be.getMatchingMixingRecipe();

        if (recipeOpt.isEmpty()) {
            return InteractionResult.SUCCESS;
        }

        ProcessingRecipe recipe = recipeOpt.get();
        List<Ingredient> ingredients = recipe.getIngredients();

        int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);

        if (currentLevel < recipe.getWaterLevelReduction()) {
            return InteractionResult.SUCCESS;
        }

        for (Ingredient ingredient : ingredients) {

            for (int i = 0; i < be.getInventory().getSlots(); i++) {

                ItemStack stack = be.getInventory().getStackInSlot(i);

                if (!stack.isEmpty() && ingredient.test(stack)) {
                    be.getInventory().extractItem(i, 1, false);
                    break;
                }
            }
        }

        int newLevel = currentLevel - recipe.getWaterLevelReduction();

        if (newLevel <= 0) {
            level.setBlock(pos, ModBlocks.WOODEN_CAULDRON.get().defaultBlockState(), 3);
        } else {
            level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, newLevel), 3);
        }

        ItemStack result = recipe.getResultItem(null).copy();

        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.75,
                pos.getZ() + 0.5,
                result
        );

        level.addFreshEntity(entity);

        level.playSound(null, pos, SoundEvents.PLAYER_SPLASH_HIGH_SPEED,
                SoundSource.BLOCKS, 1.0f, 1.0f);

        return InteractionResult.SUCCESS;
    }

     */
}