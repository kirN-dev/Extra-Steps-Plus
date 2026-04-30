package com.fireblaze.extra_steps.event;

import com.fireblaze.extra_steps.config.ModConfigHandler;
import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class ShearHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onShear(PlayerInteractEvent.EntityInteract event) {

        if (ModConfigHandler.skipWoolProcess.get()) return;

        ItemStack shears = event.getItemStack();

        //if (!(shears.getItem() instanceof ShearsItem) || !event.getItemStack().is(Items.SHEARS)) return;
        if (!shears.canPerformAction(ToolActions.SHEARS_CARVE)) return;

        Entity entity = event.getTarget();
        Level level = event.getLevel();

        if (!(entity instanceof IForgeShearable shearable)) return;

        if (!shearable.isShearable(event.getItemStack(), level, entity.blockPosition()))
            return;

        // Vanilla verhindern
        event.setCanceled(true);

        List<ItemStack> drops = shearable.onSheared(
                event.getEntity(),
                event.getItemStack(),
                level,
                entity.blockPosition(),
                0
        );

        for (ItemStack stack : drops) {

            ItemStack resultStack = stack;

            // Wenn Wool gedroppt wird -> durch Raw Wool ersetzen
            if (stack.is(ItemTags.WOOL)) {

                resultStack = new ItemStack(ModItems.RAW_WOOL.get(), stack.getCount());

                int color;

                // Sicherster Fall: Vanilla Sheep
                if (entity instanceof Sheep sheep) {
                    color = sheep.getColor().getFireworkColor();
                }
                else {
                    // Fallback: Farbe aus dem ursprünglichen Item lesen
                    color = GenericColorHelper.getColorSmart(stack);
                }
                GenericColorHelper.setColor(resultStack, color);
            }

            ItemEntity item = entity.spawnAtLocation(resultStack);

            if (item != null) {
                item.setDeltaMovement(
                        (level.random.nextFloat() - level.random.nextFloat()) * 0.1F,
                        level.random.nextFloat() * 0.05F,
                        (level.random.nextFloat() - level.random.nextFloat()) * 0.1F
                );
            }
        }

        // Schere beschädigen
        event.getItemStack().hurtAndBreak(1, event.getEntity(),
                p -> p.broadcastBreakEvent(event.getHand()));
    }
}