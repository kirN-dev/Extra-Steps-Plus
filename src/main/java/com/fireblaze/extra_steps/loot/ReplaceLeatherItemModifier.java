package com.fireblaze.extra_steps.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ReplaceLeatherItemModifier extends LootModifier {

    // Codec lädt LootItemConditions + das Ziel-Item ("to") aus der JSON
    public static final Supplier<Codec<ReplaceLeatherItemModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst)
                            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("to").forGetter(m -> m.toItem))
                            .apply(inst, ReplaceLeatherItemModifier::new)
            )
    );

    private final Item toItem;

    public ReplaceLeatherItemModifier(LootItemCondition[] conditionsIn, Item toItem) {
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
            if (stack.is(Items.LEATHER)) {
                generatedLoot.set(i, new ItemStack(toItem, stack.getCount()));
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}