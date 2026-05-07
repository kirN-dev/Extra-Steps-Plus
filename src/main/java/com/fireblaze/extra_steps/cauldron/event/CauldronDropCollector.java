package com.fireblaze.extra_steps.cauldron.event;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.cauldron.data.CauldronMixingData;
import com.fireblaze.extra_steps.cauldron.interaction.CauldronMixingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtraSteps.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CauldronDropCollector {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Level level = event.level;
        if (level.isClientSide) return;

        CauldronMixingData data = CauldronMixingData.get(level);

        for (BlockPos pos : data.getActiveCauldrons()) {

            AABB box = new AABB(pos);

            for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, box)) {

                ItemStack stack = itemEntity.getItem();

                if (!CauldronMixingHandler.isValidIngredient(level, stack)) continue;

                NonNullList<ItemStack> inv = data.getInventory(pos);

                ItemStack remaining = insertStack(inv, stack.copy());

                data.setInventory(pos, inv, (ServerLevel) level);

                if (remaining.isEmpty()) {
                    itemEntity.discard();

                    BlockState state = level.getBlockState(pos);

                    boolean hasWater =
                            state.hasProperty(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL)
                                    && state.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL) > 0;

                    if (hasWater) {
                        ServerLevel serverLevel = (ServerLevel) level;

                        serverLevel.sendParticles(
                                net.minecraft.core.particles.ParticleTypes.SPLASH,
                                pos.getX() + 0.5,
                                pos.getY() + 1.0,
                                pos.getZ() + 0.5,
                                8,
                                0.3,
                                0.2,
                                0.3,
                                0.05
                        );

                        level.playSound(
                                null,
                                pos,
                                net.minecraft.sounds.SoundEvents.GENERIC_SPLASH,
                                net.minecraft.sounds.SoundSource.BLOCKS,
                                0.3f,
                                1.35f
                        );
                    } else {
                        level.playSound(
                                null,
                                pos,
                                net.minecraft.sounds.SoundEvents.ITEM_PICKUP, // alternativ ITEM_FRAME_ADD_ITEM
                                net.minecraft.sounds.SoundSource.BLOCKS,
                                0.3f,
                                1.35f
                        );
                    }
                }
            }
        }
    }

    private static ItemStack insertStack(NonNullList<ItemStack> inv, ItemStack stack) {

        // 1. Versuche zu bestehenden Stacks hinzuzufügen
        for (int i = 0; i < inv.size(); i++) {
            ItemStack existing = inv.get(i);

            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack)) {
                int max = Math.min(existing.getMaxStackSize(), 64);
                int space = max - existing.getCount();

                if (space > 0) {
                    int toMove = Math.min(space, stack.getCount());
                    existing.grow(toMove);
                    stack.shrink(toMove);

                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }

        // 2. Leere Slots nutzen
        for (int i = 0; i < inv.size(); i++) {
            if (inv.get(i).isEmpty()) {
                int toMove = Math.min(stack.getMaxStackSize(), stack.getCount());
                ItemStack newStack = stack.split(toMove);
                inv.set(i, newStack);

                if (stack.isEmpty()) return ItemStack.EMPTY;
            }
        }

        return stack; // Rest übrig
    }
}
