package com.fireblaze.extra_steps.compat;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProcessingRecipeCategory implements IRecipeCategory<ProcessingRecipe> {
    private final ProcessingMode mode;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final IDrawable sun;
    private final IGuiHelper helper;
    private final List<ItemStack> weaponStacks = new ArrayList<>();
    private final ItemStack wool_brush;
    private final ItemStack cauldron;
    private final ItemStack basket;
    public ProcessingRecipeCategory(IGuiHelper helper, ProcessingMode mode) {
        this.helper = helper;
        this.mode = mode;
        this.background = helper.createBlankDrawable(120, 50); // etwas größer für Pfeil + Zeit
        this.icon = helper.createDrawableItemStack(JEIHelper.getIconForMode(mode));

        // Pfeil Drawable
        ResourceLocation furnaceGui = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
        this.arrow = helper.createDrawable(furnaceGui, 176, 14, 24, 17);

        ResourceLocation mySunTexture = ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "textures/gui/img.png");
        this.sun = helper.createDrawableItemStack(new ItemStack(Items.SUNFLOWER));

        // --- Alle Waffen sammeln ---
        for (Item item : BuiltInRegistries.ITEM) { // <-- BuiltInRegistries.ITEM iterierbar
            if (item instanceof AxeItem || item instanceof SwordItem) {
                weaponStacks.add(new ItemStack(item));
            }
        }
        this.wool_brush = new ItemStack(ModItems.WOOL_BRUSH.get());
        this.cauldron = new ItemStack(Blocks.CAULDRON);
        this.basket = new ItemStack(ModBlocks.BASKET.get());
    }

    public static ResourceLocation getUid(ProcessingMode mode) {
        return ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "processing_" + mode.getId());
    }

    @Override
    public RecipeType<ProcessingRecipe> getRecipeType() {
        return getRecipeType(mode);
    }

    @Override
    public Component getTitle() {
        return switch (mode) {
            case DRYING -> Component.literal("Drying Rack");
            case BRUSHING -> Component.literal("Brushing");
            case COMPRESSING -> Component.literal("Compressing");
            case SCRAPING -> Component.literal("Scraping");
            //case SPINNING -> Component.literal("Spinning");
            case CLEANING -> Component.literal("Cleaning & Dying");
            case LYE_WATER -> Component.literal("Lye Water");
        };
    }

    public static RecipeType<ProcessingRecipe> getRecipeType(ProcessingMode mode) {
        return new RecipeType<>(
                getUid(mode),
                ProcessingRecipe.class
        );
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ProcessingRecipe recipe, IFocusGroup focuses) {

        if (mode != ProcessingMode.LYE_WATER) {
            builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, 20, 16)
                    .addIngredients(recipe.getIngredients().get(0));

            builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, 80, 16)
                    .addItemStack(recipe.getResultItem(null));
        }
        else {
            builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, 20, 16)
                    .addIngredients(recipe.getIngredients().get(0));

            builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, 80, 16)
                    .addItemStack(new ItemStack(ModItems.LYE_WATER_BUCKET.get()));
        }
    }

    @Override
    public void draw(ProcessingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        int arrowX = 46;
        int arrowY = 16;

        // Pfeil rendern zwischen Input und Output
        arrow.draw(gui, arrowX, arrowY);
        // draw-Methode
        int iconX = arrowX + 4;
        int iconY = arrowY - 16;

        // Zeit über Pfeil anzeigen, wenn Maus darüber
        int arrowWidth = 24;
        int arrowHeight = 16;

        if (recipe.getTime() > 0 && mouseX >= arrowX && mouseX <= arrowX + arrowWidth &&
                mouseY >= arrowY && mouseY <= arrowY + arrowHeight) {
            Font font = Minecraft.getInstance().font;
            gui.renderTooltip(
                    font,
                    Component.literal("Time: " + (recipe.getTime() / 20f) + "s"),
                    (int) mouseX,
                    (int) mouseY
            );
        }

        switch (mode) {
            case DRYING -> sun.draw(gui, iconX, iconY);
            case SCRAPING -> gui.renderItem(Objects.requireNonNull(drawScrapingTools()), iconX, iconY);
            case BRUSHING -> gui.renderItem(wool_brush, iconX, iconY);
            case COMPRESSING -> gui.renderItem(basket, iconX, iconY);
            //case SPINNING -> sun.draw(gui, iconX, iconY);
            case CLEANING -> gui.renderItem(cauldron, iconX, iconY);
            case LYE_WATER -> gui.renderItem(cauldron, iconX, iconY);
        };

        if (mouseX >= iconX && mouseX <= iconX + arrowWidth &&
                mouseY >= iconY && mouseY <= iconY + arrowHeight) {

            List<Component> tooltipLines = null;

            switch (mode) {
                case DRYING -> tooltipLines = List.of(
                        Component.literal("Factors like biomes, daytime"),
                        Component.literal("and weather can influence the duration")
                );
                case SCRAPING -> tooltipLines = List.of(
                        Component.literal("Interact on Item in Drying Rack")
                );
                case BRUSHING -> tooltipLines = List.of(
                        Component.literal("Hold interact on Item in Drying Rack")
                );
                case COMPRESSING -> tooltipLines = List.of(
                        Component.literal("Jump on the Basket")
                );
                //case SPINNING -> tooltipLines = null;
                case CLEANING -> tooltipLines = List.of(
                        Component.literal("Interact with Lye Water Cauldron")
                );
                case LYE_WATER -> tooltipLines = List.of(
                        Component.literal("Interact with Water Cauldron")
                );
            };

            if (tooltipLines != null) renderMultilineTooltip(gui, Minecraft.getInstance().font, tooltipLines, (int) mouseX, (int) mouseY);
        }
    }

    private ItemStack drawScrapingTools() {
        if (!weaponStacks.isEmpty()) {
            int index = (int) (Minecraft.getInstance().level.getGameTime() / 20 % weaponStacks.size());
            return weaponStacks.get(index);
        }
        return null;
    }

    public static void renderMultilineTooltip(GuiGraphics gui, Font font, List<Component> lines, int mouseX, int mouseY) {
        int lineHeight = 13; // Abstand zwischen den Zeilen
        int yOffset = 0;

        for (Component line : lines) {
            gui.renderTooltip(font, line, mouseX, mouseY + yOffset);
            yOffset += lineHeight;
        }
    }

    @Override
    public boolean isHandled(ProcessingRecipe recipe) {
        return recipe.getMode() == mode;
    }
}
