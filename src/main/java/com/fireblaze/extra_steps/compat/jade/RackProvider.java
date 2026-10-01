package com.fireblaze.extra_steps.compat.jade;

import com.fireblaze.extra_steps.blockentity.DryingRackBlockEntity;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum RackProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof DryingRackBlockEntity rack)) return;
        ItemStack stack = rack.getItemInSlot0();
        if (stack.isEmpty()) return;
        data.put("Item", stack.save(new CompoundTag()));
        if (rack.getRecipeByMode(ProcessingMode.SCRAPING).isPresent()) {
            data.putInt("Scraping", rack.getScrapingActions());
            data.putInt("ScrapingRequired", rack.getRecipeByMode(ProcessingMode.SCRAPING).get().getScrapingSettings().interactions);
        } else if (rack.getRecipeByMode(ProcessingMode.BRUSHING).isPresent()) {
            rack.getRecipeByMode(ProcessingMode.BRUSHING).ifPresent(recipe -> {
                if (recipe.getTime() > 0) {
                    data.putInt("Brushing", Mth.clamp(Math.round(100f * rack.getBrushProgress() / recipe.getTime()), 0, 100));
                }
            });
        } else {
            rack.getRecipeByMode(ProcessingMode.DRYING).ifPresent(recipe -> {
                if (recipe.getTime() > 0) {
                    data.putInt("Drying", Mth.clamp(Math.round(100f * rack.dryingTime / recipe.getTime()), 0, 100));
                }
            });
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("Item")) return;
        ItemStack stack = ItemStack.of(data.getCompound("Item"));
        tooltip.add(Component.translatable("jade.extra_steps.item", stack.getHoverName()));
        if (data.contains("Scraping")) {
            if (data.getInt("Scraping") > 0) {
                tooltip.add(Component.translatable("tooltip.extra_steps.scrapes", data.getInt("Scraping"), data.getInt("ScrapingRequired")));
            }
        } else if (data.contains("Brushing")) {
            tooltip.add(Component.translatable("jade.extra_steps.brushing", data.getInt("Brushing")));
        } else if (data.contains("Drying")) {
            tooltip.add(Component.translatable("jade.extra_steps.drying", data.getInt("Drying")));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ExtraStepsJadePlugin.RACK;
    }
}
