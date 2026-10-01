package com.fireblaze.extra_steps.compat;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.processing.ProcessingMode;
import com.fireblaze.extra_steps.processing.ProcessingRecipe;
import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class ProcessingRecipeCategory implements IRecipeCategory<ProcessingRecipe> {
    private final ProcessingMode mode;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final ProcessingRecipeLayout layout;

    public ProcessingRecipeCategory(IGuiHelper helper, ProcessingMode mode) {
        this.mode = mode;
        int inputs = 1;
        int outputs = 1;
        boolean hasTool = mode == ProcessingMode.MIXING || mode == ProcessingMode.SCRAPING
                || mode == ProcessingMode.BRUSHING;
        var level = Minecraft.getInstance().level;
        if (level != null) {
            for (var recipe : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.PROCESSING.get())) {
                if (recipe.getMode() != mode) continue;
                var display = ProcessingDisplayFactory.create(recipe);
                inputs = Math.max(inputs, display.inputs().size());
                outputs = Math.max(outputs, 1 + display.secondaryOutputs().size());
                hasTool |= !display.tools().isEmpty();
            }
        }
        layout = new ProcessingRecipeLayout(inputs, outputs, hasTool);
        background = helper.createBlankDrawable(layout.width(), layout.height());
        icon = helper.createDrawableItemStack(JEIHelper.getIconForMode(mode));
        arrow = helper.createDrawable(ResourceLocation.fromNamespaceAndPath("minecraft",
                "textures/gui/container/furnace.png"), 176, 14, 24, 17);
    }

    public static ResourceLocation getUid(ProcessingMode mode) {
        return ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "processing_" + mode.getId());
    }
    public static RecipeType<ProcessingRecipe> getRecipeType(ProcessingMode mode) {
        return new RecipeType<>(getUid(mode), ProcessingRecipe.class);
    }
    @Override public RecipeType<ProcessingRecipe> getRecipeType() { return getRecipeType(mode); }
    @Override public Component getTitle() { return Component.translatable("jei.extra_steps.category." + mode.getId()); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ProcessingRecipe recipe, IFocusGroup focuses) {
        var display = ProcessingDisplayFactory.create(recipe);
        for (int i = 0; i < display.inputs().size(); i++) {
            slot(builder, RecipeIngredientRole.INPUT, layout.inputX(i, display.inputs().size()),
                    layout.slotY(i), display.inputs().get(i), List.of());
        }
        slot(builder, RecipeIngredientRole.OUTPUT, layout.outputX(0),
                layout.arrowY(), List.of(display.primaryOutput()), display.primaryTooltip());
        for (int i = 0; i < display.secondaryOutputs().size(); i++) {
            var output = display.secondaryOutputs().get(i);
            slot(builder, RecipeIngredientRole.OUTPUT, layout.outputX(i + 1),
                    layout.slotY(i + 1), List.of(output.stack()), output.tooltip());
        }
        slot(builder, RecipeIngredientRole.CATALYST, layout.equipmentX(), layout.equipmentY(),
                display.equipment(), display.equipmentTooltip());
        slot(builder, RecipeIngredientRole.CATALYST, layout.equipmentX(),
                layout.toolY(), display.tools(),
                List.of(Component.translatable("jei.extra_steps.tool")));
    }

    private void slot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y,
            List<ItemStack> stacks, List<Component> tooltip) {
        if (stacks.isEmpty()) return;
        var slot = builder.addSlot(role, x, y).addItemStacks(stacks);
        if (!tooltip.isEmpty()) slot.addTooltipCallback((view, lines) -> lines.addAll(tooltip));
    }

    @Override
    public void draw(ProcessingRecipe recipe, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        arrow.draw(gui, layout.arrowX(), layout.arrowY());
    }

    @Override
    public List<Component> getTooltipStrings(ProcessingRecipe recipe, IRecipeSlotsView slots,
            double mouseX, double mouseY) {
        if (mouseX >= layout.arrowX() && mouseX < layout.arrowX() + 24
                && mouseY >= layout.arrowY() && mouseY < layout.arrowY() + 17)
            return ProcessingDisplayFactory.actionTooltip(recipe);
        return List.of();
    }
    @Override public boolean isHandled(ProcessingRecipe recipe) { return recipe.getMode() == mode; }
}
