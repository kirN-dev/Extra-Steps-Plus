package com.fireblaze.extra_steps.cauldron.data;

import com.fireblaze.extra_steps.cauldron.network.ModNetwork;
import com.fireblaze.extra_steps.cauldron.network.packet.SyncCauldronInventoryPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class CauldronMixingData extends SavedData {

    public final Map<Long, NonNullList<ItemStack>> inventories = new HashMap<>();
    public final Set<Long> activeCauldrons = new HashSet<>();
    private final Map<Long, Integer> shovelHits = new HashMap<>();
    private final Map<Long, Long> lastStirTicks = new HashMap<>();

    public long getLastStirTick(BlockPos pos) {
        return lastStirTicks.getOrDefault(pos.asLong(), 0L);
    }

    public void setLastStirTick(BlockPos pos, long tick) {
        lastStirTicks.put(pos.asLong(), tick);
        setDirty();
    }

    public static CauldronMixingData get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;

        return serverLevel.getDataStorage().computeIfAbsent(
                CauldronMixingData::load,
                CauldronMixingData::new,
                "extra_steps_cauldron_data"
        );
    }

    public CauldronMixingData() {
    }

    public NonNullList<ItemStack> getInventory(BlockPos pos) {
        return inventories.computeIfAbsent(pos.asLong(),
                p -> {
                    activeCauldrons.add(p);
                    return NonNullList.withSize(8, ItemStack.EMPTY);
                });
    }

    public void setInventory(BlockPos pos, NonNullList<ItemStack> inv, ServerLevel level) {
        NonNullList<ItemStack> copy = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for (int i = 0; i < inv.size(); i++) {
            copy.set(i, inv.get(i).copy());
        }

        inventories.put(pos.asLong(), copy);
        setDirty();

        syncToClients(level, pos, copy);
    }

    public Set<BlockPos> getActiveCauldrons() {
        Set<BlockPos> result = new HashSet<>();

        for (Long l : activeCauldrons) {
            result.add(BlockPos.of(l));
        }

        return result;
    }



    public int getHits(BlockPos pos) {
        return shovelHits.getOrDefault(pos.asLong(), 0);
    }

    public void addHit(BlockPos pos) {
        long key = pos.asLong();
        shovelHits.put(key, getHits(pos) + 1);
    }

    public void resetHits(BlockPos pos) {
        shovelHits.remove(pos.asLong());
    }

    public void cleanup(Level level) {
        Iterator<Long> it = activeCauldrons.iterator();

        while (it.hasNext()) {
            long posLong = it.next();
            BlockPos pos = BlockPos.of(posLong);

            // Block gone?
            if (!(level.getBlockState(pos).is(Blocks.WATER_CAULDRON)
                    || level.getBlockState(pos).is(Blocks.CAULDRON))) {

                it.remove();
                inventories.remove(posLong);
                continue;
            }

            // Inventory empty?
            NonNullList<ItemStack> inv = inventories.get(posLong);
            if (inv == null || inv.stream().allMatch(ItemStack::isEmpty)) {
                it.remove();
                inventories.remove(posLong);
            }
        }

        setDirty();
    }

    public void removeCauldron(BlockPos pos) {
        long key = pos.asLong();

        activeCauldrons.remove(key);
        inventories.remove(key);

        setDirty();
    }

    private void syncToClients(ServerLevel level, BlockPos pos, NonNullList<ItemStack> inv) {
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)),
                new SyncCauldronInventoryPacket(pos, inv)
        );
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

        CompoundTag cauldronsTag = new CompoundTag();

        for (Map.Entry<Long, NonNullList<ItemStack>> entry : inventories.entrySet()) {
            CompoundTag invTag = new CompoundTag();

            NonNullList<ItemStack> inv = entry.getValue();

            for (int i = 0; i < inv.size(); i++) {
                ItemStack stack = inv.get(i);

                if (!stack.isEmpty()) {
                    CompoundTag stackTag = new CompoundTag();
                    stack.save(stackTag);
                    invTag.put("slot" + i, stackTag);
                }
            }

            cauldronsTag.put(String.valueOf(entry.getKey()), invTag);
        }

        tag.put("cauldrons", cauldronsTag);

        CompoundTag activeTag = new CompoundTag();

        int i = 0;
        for (Long pos : activeCauldrons) {
            activeTag.putLong("pos" + i++, pos);
        }

        tag.put("active", activeTag);
        return tag;
    }

    public static CauldronMixingData load(CompoundTag tag) {

        CauldronMixingData data = new CauldronMixingData();

        if (!tag.contains("cauldrons")) return data;

        CompoundTag cauldronsTag = tag.getCompound("cauldrons");

        for (String key : cauldronsTag.getAllKeys()) {

            long posKey = Long.parseLong(key);
            CompoundTag invTag = cauldronsTag.getCompound(key);

            NonNullList<ItemStack> inv = NonNullList.withSize(8, ItemStack.EMPTY);

            for (int i = 0; i < 8; i++) {
                String slotKey = "slot" + i;

                if (invTag.contains(slotKey)) {
                    CompoundTag stackTag = invTag.getCompound(slotKey);
                    inv.set(i, ItemStack.of(stackTag));
                }
            }

            data.inventories.put(posKey, inv);
        }

        if (tag.contains("active")) {
            CompoundTag activeTag = tag.getCompound("active");

            for (String key : activeTag.getAllKeys()) {
                data.activeCauldrons.add(activeTag.getLong(key));
            }
        }

        return data;
    }
}