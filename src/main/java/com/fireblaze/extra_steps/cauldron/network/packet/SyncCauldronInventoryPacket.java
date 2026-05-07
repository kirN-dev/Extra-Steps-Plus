package com.fireblaze.extra_steps.cauldron.network.packet;

import com.fireblaze.extra_steps.cauldron.client.ClientCauldronCache;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCauldronInventoryPacket {

    private final BlockPos pos;
    private final NonNullList<ItemStack> inv;

    public SyncCauldronInventoryPacket(BlockPos pos, NonNullList<ItemStack> inv) {
        this.pos = pos;
        this.inv = inv;
    }

    public static void encode(SyncCauldronInventoryPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.inv.size());

        for (ItemStack stack : msg.inv) {
            buf.writeItem(stack);
        }
    }

    public static SyncCauldronInventoryPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();

        NonNullList<ItemStack> inv = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int i = 0; i < size; i++) {
            inv.set(i, buf.readItem());
        }

        return new SyncCauldronInventoryPacket(pos, inv);
    }

    public static void handle(SyncCauldronInventoryPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientCauldronCache.setInventory(msg.pos, msg.inv);
        });
        ctx.get().setPacketHandled(true);
    }
}