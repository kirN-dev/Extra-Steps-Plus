package com.fireblaze.extra_steps.blockentity;

import com.fireblaze.extra_steps.blockentity.machine.MachineBlockEntity;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BasketBlockEntity extends MachineBlockEntity {

    // Wir behalten ein ItemStackHandler-Inventar für 1 Slot, max 64
    private final ItemStackHandler inventoryHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if(level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public BasketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASKET_BE.get(), pos, state);
    }

    // --- Überschreiben von abstrakten Methoden ---
    @Override
    protected Optional<ProcessingRecipe> getRecipe() {
        // Basket hat keine Rezepte
        return Optional.empty();
    }

    @Override
    protected float getProcessingSpeed() {
        return 0;
    }

    public IItemHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public Optional<ProcessingRecipe> getRecipeByMode(ProcessingMode mode) {

        if(level == null) return Optional.empty();

        SimpleContainer container = new SimpleContainer(inventoryHandler.getStackInSlot(0));

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(recipe -> recipe.getMode() == mode)
                .filter(recipe -> recipe.matches(container, level))
                .findFirst();
    }

    public Optional<ProcessingRecipe> getRecipeForItem(ItemStack stack, ProcessingMode mode) {

        if(level == null) return Optional.empty();

        SimpleContainer container = new SimpleContainer(stack);

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(recipe -> recipe.getMode() == mode)
                .filter(recipe -> recipe.matches(container, level))
                .findFirst();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventoryHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventoryHandler.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        load(pkt.getTag());
    }
}