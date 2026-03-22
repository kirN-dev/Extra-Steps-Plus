package com.fireblaze.extra_steps.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ReplaceItemModifier extends LootModifier {

    public static final Supplier<Codec<ReplaceItemModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).and(inst.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("from").forGetter(m -> m.fromItem),
                    ForgeRegistries.ITEMS.getCodec().fieldOf("to").forGetter(m -> m.toItem)
            )).apply(inst, ReplaceItemModifier::new))
    );

    private final Item fromItem;
    private final Item toItem;

    public ReplaceItemModifier(LootItemCondition[] conditionsIn, Item fromItem, Item toItem) {
        super(conditionsIn);
        this.fromItem = fromItem;
        this.toItem = toItem;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {

        for (LootItemCondition condition : this.conditions) {
            if (!condition.test(context)) {
                return generatedLoot;
            }
        }

        for (int i = 0; i < generatedLoot.size(); i++) {

            ItemStack stack = generatedLoot.get(i);

            if (stack.is(fromItem)) {
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