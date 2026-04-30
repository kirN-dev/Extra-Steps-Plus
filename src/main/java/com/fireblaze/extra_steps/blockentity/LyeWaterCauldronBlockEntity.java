package com.fireblaze.extra_steps.blockentity;

import com.fireblaze.extra_steps.registry.ModBlockEntities;
import com.fireblaze.extra_steps.util.CauldronInteractionHelper;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import com.fireblaze.extra_steps.util.GenericCauldronInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class LyeWaterCauldronBlockEntity extends BlockEntity implements GenericCauldronInteractions {

    private int color = 0xFFFFFF; // Startfarbe Weiß

    // Anzahl an zugegebenen Farbpulvern pro Level (max 3)
    private final int[] colorCounts = new int[3];
    private final int[][] accumulatedColors = new int[3][3]; // RGB pro Level
    private float fillFactor = 0f; // Saturation Ratio (usedSlots / maxSlots)
    private final List<Integer>[] colorLists = new List[3]; // pro Wasserlevel 1..3
    public final int[] availableInteractions = new int[]{3, 3, 3};
    private boolean full = false;

    public float itemYaw = 0f;

    public LyeWaterCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LYE_WATER_CAULDRON_BE.get(), pos, state);
        for (int i = 0; i < 3; i++) {
            colorLists[i] = new ArrayList<>();
        }
    }

    @Override
    public void copyColorTo(ItemStack stack, boolean empty) {
        GenericColorHelper.copyColorFromLyeWater(this, stack, empty);
    }

    @Override
    public void drainLevel(Level level, BlockPos pos, BlockState state,
                           int currentLevel, int interactions,
                           BlockState emptyState) {
        CauldronInteractionHelper.drainLevel(level, pos, state, currentLevel, interactions, this, emptyState);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    public float getFillFactor() {
        return fillFactor;
    }

    public boolean getFull() {
        return full;
    }

    public boolean mixColor(int newColor, int waterLevel) {
        if (waterLevel <= 0) return false;

        int levelIndex = waterLevel - 1;
        int usedSlots = getUsedSlots();
        int maxSlots = waterLevel * 3;

        if (usedSlots >= maxSlots) return false;

        // Farbe auf die Level-Liste hinzufügen
        colorLists[levelIndex].add(newColor);
        colorLists[levelIndex].sort(Integer::compare);

        // RGB-Komponenten extrahieren
        int rNew = (newColor >> 16) & 0xFF;
        int gNew = (newColor >> 8) & 0xFF;
        int bNew = newColor & 0xFF;

        // AccumulatedColors & Counts für NBT beibehalten
        accumulatedColors[levelIndex][0] += rNew;
        accumulatedColors[levelIndex][1] += gNew;
        accumulatedColors[levelIndex][2] += bNew;
        colorCounts[levelIndex]++;

        // Neue Gesamtsummen über alle Level berechnen
        usedSlots = getUsedSlots();
        if (usedSlots == 0) return false;

        int totalR = 0, totalG = 0, totalB = 0;
        for (int i = 0; i < 3; i++) {
            totalR += accumulatedColors[i][0];
            totalG += accumulatedColors[i][1];
            totalB += accumulatedColors[i][2];
        }

        // Durchschnittsfarbe
        int rAvg = totalR / usedSlots;
        int gAvg = totalG / usedSlots;
        int bAvg = totalB / usedSlots;

        // Verdünnung durch Wasser (fillFactor: 0..1)
        fillFactor = usedSlots / (float) maxSlots; // bleibt NBT-relevant

        float dilution = fillFactor; // 1 = volle Farbe, 0 = komplett verdünnt

        // Mit Weiß mischen für Verdünnung
        rAvg = (int)(rAvg * dilution + 255 * (1 - dilution));
        gAvg = (int)(gAvg * dilution + 255 * (1 - dilution));
        bAvg = (int)(bAvg * dilution + 255 * (1 - dilution));

        // Neue Farbe setzen
        this.color = (rAvg << 16) | (gAvg << 8) | bAvg;

        setChanged();

        // Client update
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return true;
    }

    public int getUsedSlots() {
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += colorCounts[i];
        }
        return sum;
    }

    private void rebuildAccumulatedColors() {
        for (int i = 0; i < 3; i++) {
            accumulatedColors[i][0] = 0;
            accumulatedColors[i][1] = 0;
            accumulatedColors[i][2] = 0;
            for (int col : colorLists[i]) {
                accumulatedColors[i][0] += (col >> 16) & 0xFF;
                accumulatedColors[i][1] += (col >> 8) & 0xFF;
                accumulatedColors[i][2] += col & 0xFF;
            }
        }
    }

    public List<Integer>[] getUsedColors() {
        return colorLists;
    }

    public boolean consumeInteraction(int waterLevel) {
        if (waterLevel <= 0) return false;

        int index = waterLevel - 1;

        if (availableInteractions[index] <= 0) {
            return false;
        }

        availableInteractions[index]--;

        if (availableInteractions[index] <= 0) {
            //then you get availableInteractions[index] = 3;
            setChanged();
            return true; // Level reduzieren
        }

        setChanged();
        sync();

        return false;
    }

    public void resetInteractionCount(int waterLevel) {
        if (waterLevel <= 0) return;

        availableInteractions[waterLevel - 1] = 3;

        setChanged();
        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("color", this.color);
        tag.putFloat("fillFactor", this.fillFactor);

        for (int i = 0; i < 3; i++) {
            tag.putInt("colorCount" + i, colorCounts[i]);

            // Liste speichern
            int[] arr = colorLists[i].stream().mapToInt(Integer::intValue).toArray();
            tag.putIntArray("colorList" + i, arr);
        }

        for (int i = 0; i < 3; i++) {
            tag.putInt("availableInteraction" + i, availableInteractions[i]);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("color")) this.color = tag.getInt("color");
        if (tag.contains("fillFactor")) this.fillFactor = tag.getFloat("fillFactor");

        for (int i = 0; i < 3; i++) {
            colorCounts[i] = tag.contains("colorCount" + i) ? tag.getInt("colorCount" + i) : 0;

            colorLists[i].clear();
            if (tag.contains("colorList" + i)) {
                int[] arr = tag.getIntArray("colorList" + i);
                for (int c : arr) colorLists[i].add(c);
            }
        }

        for (int i = 0; i < 3; i++) {
            availableInteractions[i] = tag.contains("availableInteraction" + i)
                    ? tag.getInt("availableInteraction" + i)
                    : 3;
        }
        rebuildAccumulatedColors();
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

        rebuildAccumulatedColors(); // interne Summen aktualisieren
    }
}