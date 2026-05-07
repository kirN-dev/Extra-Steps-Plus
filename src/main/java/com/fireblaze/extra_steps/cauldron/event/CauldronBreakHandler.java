package com.fireblaze.extra_steps.cauldron.event;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.cauldron.data.CauldronMixingData;
import com.fireblaze.extra_steps.cauldron.network.ModNetwork;
import com.fireblaze.extra_steps.cauldron.network.packet.RemoveCauldronPacket;
import com.fireblaze.extra_steps.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ExtraSteps.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CauldronBreakHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();

        if (!(level instanceof ServerLevel serverLevel)) return;

        if (event.getState().is(ModTags.VALID_MIXING_CAULDRONS)) {
            CauldronMixingData data = CauldronMixingData.get(serverLevel);
            if (data == null) return;

            NonNullList<ItemStack> inv = data.inventories.get(pos.asLong());

            if (inv != null) {
                boolean hasItems = inv.stream().anyMatch(s -> !s.isEmpty());

                if (hasItems) {
                    for (ItemStack stack : inv) {
                        if (!stack.isEmpty()) {
                            Containers.dropItemStack(
                                    serverLevel,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    stack
                            );
                        }
                    }
                }
            }

            // ✅ richtig entfernen
            data.removeCauldron(pos);
            ModNetwork.CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(pos)),
                    new RemoveCauldronPacket(pos)
            );
        }
    }
}
