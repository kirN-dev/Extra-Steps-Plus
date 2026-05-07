package com.fireblaze.extra_steps.cauldron.network.packet;

import com.fireblaze.extra_steps.cauldron.client.ClientCauldronCache;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RemoveCauldronPacket {

    private final BlockPos pos;

    public RemoveCauldronPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(RemoveCauldronPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static RemoveCauldronPacket decode(FriendlyByteBuf buf) {
        return new RemoveCauldronPacket(buf.readBlockPos());
    }

    public static void handle(RemoveCauldronPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientCauldronCache.remove(msg.pos);
        });
        ctx.get().setPacketHandled(true);
    }
}