package com.fireblaze.extra_steps.item;

import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EquipmentSlot;

public class ModLeatherArmorItem extends ArmorItem implements DyeableLeatherItem {

    public ModLeatherArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public int getColor(ItemStack stack) {
        if (GenericColorHelper.hasColor(stack)) {
            return GenericColorHelper.getColor(stack);
        }
        // Vanilla Lederfarbe #A06540
        return 0xA06540;
    }

    @Override
    public void clearColor(ItemStack stack) {
        GenericColorHelper.clearColor(stack);
    }

    public static void setArmorColor(ItemStack armor, int rgb) {
        if (armor.getItem() instanceof DyeableLeatherItem) {
            ((DyeableLeatherItem) armor.getItem()).setColor(armor, rgb);
        }
    }
}