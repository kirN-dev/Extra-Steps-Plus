package com.fireblaze.extra_steps.blockentity;

import com.fireblaze.extra_steps.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ColorableBlockEntity extends BlockEntity {

    public ColorableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COLORABLE_BE.get(), pos, state);

        for (int i = 0; i < 3; i++) {
            colorLists[i] = new ArrayList<>();
        }
    }
    private int color = 0xFFFFFF; // Default: weiß
    private float fillFactor = 0; // Default: weiß
    private final List<Integer>[] colorLists = new List[3]; // pro Wasserlevel 1..3

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged(); // wichtig: markiert BE als verändert

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putInt("color", this.color);

        // Fill factor (Sättigung)
        tag.putFloat("fillFactor", this.fillFactor);

        // Color lists pro Slot
        for (int i = 0; i < 3; i++) {
            int[] arr = colorLists[i].stream().mapToInt(Integer::intValue).toArray();
            tag.putIntArray("colorList" + i, arr);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        // 🔴 Farbe
        if (tag.contains("color")) {
            this.color = tag.getInt("color");
        }

        // 🟡 FillFactor
        if (tag.contains("fillFactor")) {
            this.fillFactor = tag.getFloat("fillFactor");
        }

        // 🔵 ColorLists
        for (int i = 0; i < 3; i++) {
            String key = "colorList" + i;

            // Liste IMMER resetten → verhindert Alt-Daten
            colorLists[i].clear();

            if (tag.contains(key)) {
                int[] arr = tag.getIntArray(key);

                for (int c : arr) {
                    colorLists[i].add(c);
                }
            }
        }

        setChanged();
    }

    public float getFillFactor() {
        return this.fillFactor;
    }
    public void setFillFactor(float fillFactor) {
        this.fillFactor = fillFactor;
    }

    public List<Integer> getColorList(int i) {
        return colorLists[i];
    }
    public void setColorList(int index, int[] colors) {
        if (index < 0 || index >= colorLists.length) return;

        colorLists[index].clear();

        for (int c : colors) {
            colorLists[index].add(c);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void sync() {
        if (level == null) return;

        setChanged(); // BlockEntity dirty markieren

        Packet<ClientGamePacketListener> packet = getUpdatePacket();

        if (!level.isClientSide) {

            // Optional: tickversetzt nochmal senden
            level.getServer().execute(() -> {
                level.getServer().getPlayerList().getPlayers().forEach(player -> {
                    if (player.distanceToSqr(worldPosition.getX()+0.5, worldPosition.getY()+0.5, worldPosition.getZ()+0.5) < 64*64) {
                        player.connection.send(packet);
                    }
                });
            });
        }
    }
}