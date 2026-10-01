package com.fireblaze.extra_steps.blockentity;

import com.fireblaze.extra_steps.blockentity.machine.MachineBlockEntity;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import com.fireblaze.extra_steps.util.DryingEnvironment;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

public class DryingRackBlockEntity extends MachineBlockEntity {

    public float dryingTime = 0;
    public float clientSpeed = 0f;
    private boolean isBrushing = false;
    private float brushProgress = 0f;
    private int legacyScrapingActions = 0;
    public int brushInteractionCooldown = 15;
    public int pickupCooldown = 0;
    public boolean showBrushFinishedParticles = false;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) { return 1; }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            legacyScrapingActions = 0;

            ItemStack stack = inventory.getStackInSlot(slot);
            if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains("dryingProgress")) {
                // Progress von Item in dryingTime umrechnen
                Optional<ProcessingRecipe> recipe = getRecipeByMode(ProcessingMode.DRYING);
                if(recipe.isPresent()) {
                    float progress = stack.getTag().getFloat("dryingProgress"); // 0.0 -> 1.0
                    dryingTime = progress * recipe.get().getTime();
                }
            }

            markForRenderUpdate(); // BlockEntity Update
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public ItemStack getItemInSlot0() {
        return inventory.getStackInSlot(0);
    }

    public int getScrapingActions() {
        return getRecipeByMode(ProcessingMode.SCRAPING).map(recipe -> {
            migrateScraping(recipe);
            return com.fireblaze.extra_steps.util.ScrapingProgressHelper.get(getItemInSlot0(), recipe);
        }).orElse(0);
    }

    public float getBrushProgress() {
        return brushProgress;
    }

    private void migrateScraping(ProcessingRecipe recipe) {
        ItemStack stack = getItemInSlot0();
        if (legacyScrapingActions > 0 && !com.fireblaze.extra_steps.util.ScrapingProgressHelper.has(stack)) {
            com.fireblaze.extra_steps.util.ScrapingProgressHelper.set(stack, recipe, Math.min(legacyScrapingActions, recipe.getScrapingSettings().interactions - 1));
            setChanged();
        }
        legacyScrapingActions = 0;
    }

    public boolean scrape(Player player, net.minecraft.world.InteractionHand hand) {
        if (level == null || level.isClientSide) return false;
        Optional<ProcessingRecipe> found = getRecipeByMode(ProcessingMode.SCRAPING);
        if (found.isEmpty()) return false;
        ProcessingRecipe recipe = found.get();
        var rules = recipe.getScrapingSettings();
        ItemStack input = getItemInSlot0();
        if (input.getCount() != 1 || !rules.acceptsTool(player.getItemInHand(hand))) return false;
        if (rules.damageInput && !input.isDamageableItem()) return false;
        migrateScraping(recipe);
        int completed = com.fireblaze.extra_steps.util.ScrapingProgressHelper.get(input, recipe) + 1;
        boolean finished = completed >= rules.interactions;
        boolean broken = false;
        if (rules.damageInput) {
            // Processing wear is deterministic: enchantments and creative mode do not alter the yield.
            int remaining = input.getMaxDamage() - input.getDamageValue();
            broken = rules.damage >= remaining;
            if (!broken) input.setDamageValue(input.getDamageValue() + rules.damage);
        }
        for (var byproduct : rules.byproducts) {
            if (byproduct.appliesAt(completed, finished) && level.random.nextFloat() < byproduct.chance())
                net.minecraft.world.level.block.Block.popResource(level, worldPosition, byproduct.stack().copy());
        }
        if (finished) {
            if (rules.damageInput) {
                ItemStack output = recipe.getResultItem(level.registryAccess()).copy();
                if (GenericColorHelper.hasColor(input)) GenericColorHelper.copyColor(input, output);
                net.minecraft.world.level.block.Block.popResource(level, worldPosition, output);
                com.fireblaze.extra_steps.util.ScrapingProgressHelper.clear(input);
            } else processItem(recipe);
        } else com.fireblaze.extra_steps.util.ScrapingProgressHelper.set(input, recipe, completed);
        if (broken) inventory.extractItem(0, 1, false);
        player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER, net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, 1f);
        markForRenderUpdate();
        return true;
    }

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK_BE.get(), pos, state);
    }

    @Override
    protected Optional<ProcessingRecipe> getRecipe() {

        if(level == null) return Optional.empty();

        return level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.PROCESSING.get(),
                new SimpleContainer(inventory.getStackInSlot(0)),
                level
        );
    }

    public int brushSoundCooldown = 0;
    private int brushStopTimer = 0;

    public void tick() {
        if(level == null || level.isClientSide) return;

        // Cooldown runterzählen
        if(pickupCooldown > 0) {
            pickupCooldown--;
        }

        if (brushInteractionCooldown > 0) {
            brushInteractionCooldown--;
        }

        ItemStack stack = inventory.getStackInSlot(0);
        if(stack.isEmpty()) {
            dryingTime = 0;
            brushProgress = 0;
            isBrushing = false;
            return;
        }

        getRecipeByMode(ProcessingMode.SCRAPING).ifPresent(this::migrateScraping);

        // --- Brushing Mode ---
        Optional<ProcessingRecipe> brushingRecipe = getRecipeByMode(ProcessingMode.BRUSHING);

        if(isBrushing && brushingRecipe.isPresent()) {
            brushProgress += 1f;
            //System.out.println(brushProgress + " | " + brushSoundCooldown + " | " + brushStopTimer);

            if(brushSoundCooldown > 0) brushSoundCooldown--;
            if(brushStopTimer > 0) brushStopTimer--;

            if(brushStopTimer <= 0) {
                isBrushing = false;
                brushProgress = 0;
                return;
            }

            if(brushProgress >= brushingRecipe.get().getTime()) {

                processItem(brushingRecipe.get());
                com.fireblaze.extra_steps.processing.ProcessingByproducts.drop(level, worldPosition,
                        brushingRecipe.get(), brushingRecipe.get().getScrapingSettings().interactions, true);

                brushProgress = 0;
                isBrushing = false;

                processBrushingFinished();

                pickupCooldown = 4;
            }

            markForRenderUpdate();
            return;
        }


        // --- Drying Mode ---
        Optional<ProcessingRecipe> dryingRecipe = getRecipeByMode(ProcessingMode.DRYING);

        if (dryingRecipe.isEmpty()) {
            dryingTime = 0;
            return;
        }

        ProcessingRecipe recipe = dryingRecipe.get();

        float speed = getProcessingSpeed();
        clientSpeed = speed;

        dryingTime += speed;
        dryingTime = Math.max(dryingTime, -200);

        updateItemProgress(stack, dryingTime, recipe.getTime());
        markForRenderUpdate();

        if (dryingTime >= recipe.getTime()) {
            processItem(recipe);
            dryingTime = 0;
        }
    }

    private void processItem(ProcessingRecipe recipe) {
        // Input-Stack entfernen
        ItemStack input = inventory.extractItem(0, 1, false);

        // Ergebnis-Stack erzeugen (kopieren!)
        ItemStack result = recipe.getResultItem(null).copy();

        // Farbe vom Input auf das Ergebnis übertragen, falls vorhanden
        if (GenericColorHelper.hasColor(input)) {
            GenericColorHelper.copyColor(input, result);
        }

        // Ergebnis ins Inventar einfügen
        inventory.insertItem(0, result, false);

        setChanged();

        // BlockUpdate an Client senden
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void brushing(Player player) {
        ItemStack stack = getItemInSlot0();
        if(stack.isEmpty()) return;
        isBrushing = true;

        brushStopTimer = 5;
    }

    private boolean brushFinishedParticlePending = false;
    private void processBrushingFinished() {

        brushProgress = 0;
        isBrushing = false;
        pickupCooldown = 5;

        if(level != null && !level.isClientSide) {
            brushFinishedParticlePending = true;
            markForRenderUpdate();
        }
    }

    public void damageTool(Player player) {
        ItemStack tool = player.getMainHandItem();
        tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    public void brushingSound() {
        if(level != null) {
            level.playSound(
                    null,
                    worldPosition,
                    net.minecraft.sounds.SoundEvents.BRUSH_GENERIC,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1f,
                    1f
            );
        }
    }
    public void markForRenderUpdate() {
        if (level != null && !level.isClientSide && level instanceof ServerLevel serverLevel) {
            setChanged(); // markiert BlockEntity dirty
            serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

            // BE-Paket an alle Spieler im Chunk senden
            ChunkPos chunkPos = new ChunkPos(worldPosition);
            serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false)
                    .forEach(player -> player.connection.send(ClientboundBlockEntityDataPacket.create(this)));
        }
    }

    private void updateItemProgress(ItemStack stack, float progress, float maxProgress) {
        if(stack.isEmpty()) return;
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat("dryingProgress", progress / maxProgress);
        stack.setTag(tag);
    }

    public IItemHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putFloat("dryingTime", dryingTime);
        tag.putInt("scrapingActions", legacyScrapingActions);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        dryingTime = tag.getFloat("dryingTime");
        legacyScrapingActions = tag.getInt("scrapingActions");
        clientSpeed = tag.getFloat("clientSpeed");
    }

    @Override
    public float getProcessingSpeed() {

        if(level == null)
            return 1f;

        return DryingEnvironment.getDryingSpeed(level, worldPosition);
    }


    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        tag.putFloat("clientSpeed", clientSpeed);

        // Serverseitig setzen, Client bekommt es über Paket
        tag.putBoolean("brushFinishedParticlePending", brushFinishedParticlePending);
        brushFinishedParticlePending = false; // nur ein Paket nötig

        return tag;
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag);

        // Client empfängt Paket → einmaliger Partikel-Trigger
        if(tag.contains("brushFinishedParticlePending") && tag.getBoolean("brushFinishedParticlePending")) {
            showBrushFinishedParticles = true; // Clientseitiger Trigger
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Paket für BlockEntity Sync an Client
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public Optional<ProcessingRecipe> getRecipeByMode(ProcessingMode mode) {

        if(level == null) return Optional.empty();

        SimpleContainer container = new SimpleContainer(inventory.getStackInSlot(0));

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESSING.get())
                .stream()
                .filter(recipe -> recipe.getMode() == mode)
                .filter(recipe -> recipe.matches(container, level))
                .findFirst();
    }
}
