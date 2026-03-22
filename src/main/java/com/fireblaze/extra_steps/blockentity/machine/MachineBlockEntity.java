package com.fireblaze.extra_steps.blockentity.machine;

import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public abstract class MachineBlockEntity extends BlockEntity {

    protected final SimpleContainer inventory = new SimpleContainer(1);

    protected float progress;
    protected int maxProgress;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract Optional<ProcessingRecipe> getRecipe();

    public void tick() {

        /*
        if(level == null || level.isClientSide) return;

        Optional<ProcessingRecipe> recipe = getRecipe();

        if(recipe.isPresent()) {

            float speed = getProcessingSpeed();

            progress += speed;

            maxProgress = recipe.get().getTime();

            if(progress >= maxProgress) {

                craft(recipe.get());

                progress = 0;
            }

        } else {

            progress = 0;
        }

         */
    }

    private void craft(ProcessingRecipe recipe) {

        inventory.removeItem(0,1);

        inventory.setItem(0, recipe.getResultItem(null));
    }

    protected abstract float getProcessingSpeed();
}