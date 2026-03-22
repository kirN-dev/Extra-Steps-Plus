package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.block.*;

import com.fireblaze.extra_steps.fluid.ModFluids;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExtraSteps.MODID);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {

        RegistryObject<T> blockObj = BLOCKS.register(name, block);

        ModItems.ITEMS.register(name,
                () -> new BlockItem(blockObj.get(), new Item.Properties()));

        return blockObj;
    }

    public static final RegistryObject<Block> DRYING_RACK =
            registerBlock("drying_rack",
                    () -> new DryingRackBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.WOOD)
                                    .strength(1.5f)
                    ));

    /*
    public static final RegistryObject<Block> HANGING_DRYING_RACK =
            registerBlock("hanging_drying_rack",
                    () -> new HangingDryingRackBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.WOOD)
                                    .strength(1.5f)
                    ));

     */

    public static final RegistryObject<Block> BASKET =
            registerBlock("basket",
                    () -> new BasketBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2f)
                    ));

    public static final RegistryObject<Block> COLORABLE_WOOL =
            registerBlock("colorable_wool",
                    () -> new ColorableWoolBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)
                    ));

    public static final RegistryObject<Block> LYE_WATER_CAULDRON =
            BLOCKS.register("lye_water_cauldron",
                    () -> new LyeWaterCauldronBlock(
                            BlockBehaviour.Properties.copy(Blocks.CAULDRON)
                    ));

    public static final RegistryObject<LiquidBlock> LYE_WATER_BLOCK =
            BLOCKS.register("lye_water_block", () -> new LiquidBlock(ModFluids.SOURCE_LYE_WATER, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}