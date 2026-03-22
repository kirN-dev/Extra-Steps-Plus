package com.fireblaze.extra_steps.loot;

import com.fireblaze.extra_steps.registry.ModItems;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ReplaceWoolItemModifier extends LootModifier {

    public static final Supplier<Codec<ReplaceWoolItemModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst)
                            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("to").forGetter(m -> m.toItem))
                            .apply(inst, ReplaceWoolItemModifier::new)
            )
    );

    private final Item toItem;

    public ReplaceWoolItemModifier(LootItemCondition[] conditionsIn, Item toItem) {
        super(conditionsIn);
        this.toItem = toItem;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {

        if (!context.getQueriedLootTableId().getPath().contains("entities")) {
            return generatedLoot;
        }

        for (int i = 0; i < generatedLoot.size(); i++) {

            ItemStack stack = generatedLoot.get(i);

            if (stack.is(ItemTags.WOOL)) {

                ItemStack result = new ItemStack(toItem, stack.getCount());

                // Wenn Zielitem Crumbled Wool ist -> Farbe übernehmen
                if (toItem == ModItems.CRUMBLED_WOOL.get()) {

                    int color = GenericColorHelper.getColorSmart(stack);
                    GenericColorHelper.setColor(result, color);

                }

                generatedLoot.set(i, result);
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}