package com.fireblaze.extra_steps.cauldron.network;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.cauldron.network.packet.RemoveCauldronPacket;
import com.fireblaze.extra_steps.cauldron.network.packet.SyncCauldronInventoryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExtraSteps.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {

        CHANNEL.registerMessage(id++,
                SyncCauldronInventoryPacket.class,
                SyncCauldronInventoryPacket::encode,
                SyncCauldronInventoryPacket::decode,
                SyncCauldronInventoryPacket::handle
        );

        CHANNEL.registerMessage(id++,
                RemoveCauldronPacket.class,
                RemoveCauldronPacket::encode,
                RemoveCauldronPacket::decode,
                RemoveCauldronPacket::handle
        );
    }
}