package com.fireblaze.extra_steps.item;

import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ColoredWoolItem extends Item {

    public ColoredWoolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        /*
        int colorInt = GenericColorHelper.getColorSmart(stack);
        String colorName = GenericColorHelper.TooltipTextGenerator(stack);

        tooltip.add(
                Component.literal(colorName)
                        .withStyle(style -> style.withColor(colorInt))
        );

         */
    }
}