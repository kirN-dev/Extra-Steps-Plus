package com.fireblaze.extra_steps.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import com.fireblaze.extra_steps.ExtraSteps;

public class ModTags {

    public static final TagKey<Item> RAW_HIDES =
            TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "raw_hides"));
}