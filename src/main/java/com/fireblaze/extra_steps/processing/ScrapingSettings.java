package com.fireblaze.extra_steps.processing;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Recipe rules shared by the server, tooltips and recipe viewers. */
public class ScrapingSettings {
    public record Byproduct(ItemStack stack, float chance, String trigger, List<Integer> interactions) {
        public boolean appliesAt(int interaction, boolean finished) {
            return (trigger.equals("per_interaction") || finished)
                    && (interactions.isEmpty() || interactions.contains(interaction));
        }
        public int occurrencesPerCycle(int cycleLength) {
            if (trigger.equals("on_completion")) return appliesAt(cycleLength, true) ? 1 : 0;
            return interactions.isEmpty() ? cycleLength : interactions.size();
        }
    }
    public final int interactions;
    public final boolean damageInput;
    public final int damage;
    public final List<Ingredient> tools = new ArrayList<>();
    public final List<Byproduct> byproducts = new ArrayList<>();
    public final JsonObject json;

    public ScrapingSettings(JsonObject json) {
        this.json = new JsonObject();
        for (String key : List.of("interaction_count", "input_cost", "tools", "byproducts"))
            if (json.has(key)) this.json.add(key, json.get(key).deepCopy());
        interactions = json.has("interaction_count") ? json.get("interaction_count").getAsInt() : 3;
        if (interactions < 1) throw new IllegalArgumentException("interaction_count must be positive");
        JsonObject cost = json.has("input_cost") ? json.getAsJsonObject("input_cost") : new JsonObject();
        String type = cost.has("type") ? cost.get("type").getAsString() : "consume";
        if (!type.equals("consume") && !type.equals("damage")) throw new IllegalArgumentException("Unknown input_cost type");
        damageInput = type.equals("damage");
        damage = cost.has("amount") ? cost.get("amount").getAsInt() : 1;
        if (damage < 1) throw new IllegalArgumentException("input_cost amount must be positive");
        if (!damageInput && damage != 1) throw new IllegalArgumentException("Scraping consumes one input");
        if (cost.has("trigger") && !cost.get("trigger").getAsString().equals(damageInput ? "per_interaction" : "on_completion"))
            throw new IllegalArgumentException("Unsupported input_cost trigger");
        if (json.has("tools")) {
            for (var element : json.getAsJsonArray("tools")) tools.add(Ingredient.fromJson(element));
            if (tools.isEmpty()) throw new IllegalArgumentException("tools must not be empty");
        }
        if (json.has("byproducts")) for (var element : json.getAsJsonArray("byproducts")) {
            JsonObject entry = element.getAsJsonObject();
            ItemStack stack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(entry.get("item").getAsString()))), entry.has("count") ? entry.get("count").getAsInt() : 1);
            float chance = entry.has("chance") ? entry.get("chance").getAsFloat() : 1;
            String trigger = entry.has("trigger") ? entry.get("trigger").getAsString() : "on_completion";
            if (stack.isEmpty() || !Float.isFinite(chance) || chance < 0 || chance > 1 || (!trigger.equals("per_interaction") && !trigger.equals("on_completion")))
                throw new IllegalArgumentException("Invalid scraping byproduct");
            List<Integer> selectedInteractions = new ArrayList<>();
            if (entry.has("interactions")) {
                for (var action : entry.getAsJsonArray("interactions")) {
                    int number = action.getAsInt();
                    if (!action.getAsString().equals(Integer.toString(number)) || number < 1 || number > interactions || selectedInteractions.contains(number))
                        throw new IllegalArgumentException("Invalid byproduct interaction number");
                    selectedInteractions.add(number);
                }
                if (selectedInteractions.isEmpty()) throw new IllegalArgumentException("Byproduct interactions must not be empty");
            }
            byproducts.add(new Byproduct(stack, chance, trigger, List.copyOf(selectedInteractions)));
        }
    }

    public boolean acceptsTool(ItemStack stack) {
        return tools.isEmpty() ? stack.getItem() instanceof AxeItem || stack.getItem() instanceof SwordItem : tools.stream().anyMatch(tool -> tool.test(stack));
    }
}
