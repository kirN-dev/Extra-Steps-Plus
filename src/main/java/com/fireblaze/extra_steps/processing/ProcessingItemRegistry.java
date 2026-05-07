package com.fireblaze.extra_steps.processing;

import com.fireblaze.extra_steps.registry.ModRecipeTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ProcessingItemRegistry {

    private static final Set<Item> RACK_ITEMS = new HashSet<>();
    private static final Set<Item> DRYING_ITEMS = new HashSet<>();

    // 👉 Hauptmethode zum Neuaufbau
    public static void rebuild(RecipeManager manager) {
        RACK_ITEMS.clear();

        for (ProcessingRecipe recipe : manager.getAllRecipesFor(ModRecipeTypes.PROCESSING.get())) {

            ProcessingMode mode = recipe.getMode();

            if (mode == ProcessingMode.DRYING ||
                    mode == ProcessingMode.SCRAPING ||
                    mode == ProcessingMode.BRUSHING) {

                for (Ingredient ingredient : recipe.getIngredients()) {
                    for (ItemStack stack : ingredient.getItems()) {
                        RACK_ITEMS.add(stack.getItem());

                        if (mode == ProcessingMode.DRYING) {
                            DRYING_ITEMS.add(stack.getItem());
                        }
                    }
                }
            }
        }
    }

    // 👉 Abfrage-Methode (ersetzt dein Tag)
    public static boolean isValidRackItem(ItemStack stack) {
        return RACK_ITEMS.contains(stack.getItem());
    }
    public static boolean isValidDryingItem(ItemStack stack) {
        return DRYING_ITEMS.contains(stack.getItem());
    }

    // 👉 Wird beim Serverstart ausgeführt (inkl. KubeJS Rezepte)
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        rebuild(server.getRecipeManager());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        rebuild(event.getPlayerList().getServer().getRecipeManager());
    }

    public static Set<Item> getDryingItems() {
        return DRYING_ITEMS;
    }
}