package com.fireblaze.extra_steps.client;

import com.fireblaze.extra_steps.processing.ProcessingItemRegistry;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DryingTooltipHandler {
    static int color;

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        var level = net.minecraft.client.Minecraft.getInstance().level;
        if (level != null) {
            level.getRecipeManager().getAllRecipesFor(com.fireblaze.extra_steps.registry.ModRecipeTypes.PROCESSING.get()).stream()
                .filter(recipe -> recipe.getMode() == com.fireblaze.extra_steps.processing.ProcessingMode.SCRAPING)
                .filter(recipe -> recipe.matches(new net.minecraft.world.SimpleContainer(stack), level))
                .findFirst().ifPresent(recipe -> {
                    int scrapes = com.fireblaze.extra_steps.util.ScrapingProgressHelper.get(stack, recipe);
                    if (scrapes > 0) {
                        event.getToolTip().add(Component.translatable("tooltip.extra_steps.scrapes",
                            scrapes, recipe.getScrapingSettings().interactions));
                    }
                    if (recipe.getScrapingSettings().damageInput && stack.isDamageableItem())
                        event.getToolTip().add(Component.translatable("tooltip.extra_steps.uses", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
                });
        }
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        assert tag != null;

        // Optional: Fortschritt anzeigen, falls vorhanden
        if(ProcessingItemRegistry.isValidDryingItem(stack)) {
            if (Objects.requireNonNull(stack.getTag()).contains("dryingProgress")) {
                float progress = stack.getTag().getFloat("dryingProgress");
                int percent = (int)(progress * 100f);
                event.getToolTip().add(Component.literal("Drying Progress: " + percent + "%"));
            }  else event.getToolTip().add(Component.literal("Dryable in Drying Rack"));
        }

        // ------------------------------
        // 2️⃣ Hauptfarbe und Sättigung
        // ------------------------------
        if (tag.contains(GenericColorHelper.COLOR_TAG)) {
            color = GenericColorHelper.getColorSmart(stack);
            String colorName = GenericColorHelper.TooltipTextGenerator(stack);
            event.getToolTip().add(
                    Component.literal(colorName)
                            .withStyle(style -> style.withColor(color))
            );
        }

        boolean shiftDown = Screen.hasShiftDown();

        if (!tag.contains(GenericColorHelper.FILL_FACTOR)) return;

        if (shiftDown) {
            // ------------------------------
            // 3️⃣ Eingeworfene Farben
            // ------------------------------
            for (int i = 0; i < 3; i++) {
                String key = "colorList" + i;
                if (tag.contains(key)) {
                    int[] colors = tag.getIntArray(key);

                    Map<String, Integer> colorCounts = new LinkedHashMap<>();
                    Map<String, Integer> colorRGBs = new HashMap<>();
                    for (int rgb : colors) {
                        if (rgb != 0) {
                            String name = GenericColorHelper.TooltipTextGenerator(
                                    GenericColorHelper.setColor(new ItemStack(stack.getItem()), rgb)
                            );
                            colorCounts.put(name, colorCounts.getOrDefault(name, 0) + 1);
                            colorRGBs.put(name, rgb);
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
                        if (!sb.isEmpty()) sb.append("  ");
                        sb.append(entry.getValue()).append("x ").append(entry.getKey());
                    }

                    if (!sb.isEmpty()) {
                        event.getToolTip().add(
                                Component.literal(sb.toString())
                                        .withStyle(style -> style.withColor(color))
                        );
                    }
                }
            }

            // ------------------------------
            // 4️⃣ Sättigung
            // ------------------------------
            if (tag.contains(GenericColorHelper.FILL_FACTOR)) {
                float fillFactor = tag.getFloat(GenericColorHelper.FILL_FACTOR);
                int percent = (int) (fillFactor * 100f);
                event.getToolTip().add(Component.literal("Dilution: " + (100 - percent) + "%"));
            }
        } else {
            // Wenn Shift nicht gedrückt wird, kleinen Hinweis anzeigen
            event.getToolTip().add(Component.literal("Hold SHIFT for color details"));
        }
    }
}
