package com.fireblaze.extra_steps.cauldron.client.event;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.cauldron.data.CauldronMixingData;
import com.fireblaze.extra_steps.cauldron.network.ModNetwork;
import com.fireblaze.extra_steps.cauldron.network.packet.SyncCauldronInventoryPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtraSteps.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerJoinSync {

    @SubscribeEvent
    public static void onJoin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        CauldronMixingData data = CauldronMixingData.get(level);
        if (data == null) return;

        data.inventories.forEach((pos, inv) -> {
            ModNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new SyncCauldronInventoryPacket(BlockPos.of(pos), inv)
            );
        });
    }
}