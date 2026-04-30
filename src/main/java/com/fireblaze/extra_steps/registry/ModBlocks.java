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
                    () -> new ColorableBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)
                    ));

    public static final RegistryObject<Block> COLORABLE_STAINED_GLASS =
            registerBlock("colorable_stained_glass",
                    () -> new ColorableGlassBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS).noOcclusion()
                    ));

    public static final RegistryObject<Block> COLORABLE_STAINED_GLASS_PANE =
            registerBlock("colorable_stained_glass_pane",
                    () -> new ColorableGlassPaneBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS_PANE)
                    ));

    /*
    public static final RegistryObject<Block> COLORABLE_STAINED_GLASS_PANE =
            registerBlock("colorable_stained_glass_pane",
                    () -> new ColorableGlassPaneBlockAlt(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS_PANE)
                    ));

     */

    public static final RegistryObject<Block> COLORABLE_TERRACOTTA =
            registerBlock("colorable_terracotta",
                    () -> new ColorableBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_TERRACOTTA)
                    ));

    public static final RegistryObject<Block> COLORABLE_CONCRETE =
            registerBlock("colorable_concrete",
                    () -> new ColorableBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE)
                    ));

    public static final RegistryObject<Block> COLORABLE_CONCRETE_POWDER =
            registerBlock("colorable_concrete_powder",
                    () -> new ColorableConcretePowderBlock(
                            BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE_POWDER)
                    ));

    public static final RegistryObject<Block> WOODEN_CAULDRON =
            registerBlock("wooden_cauldron",
                    () -> new WoodenCauldronBlock(
                            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    ));

    public static final RegistryObject<Block> WOODEN_WATER_CAULDRON =
            BLOCKS.register("wooden_water_cauldron",
                    () -> new WoodenWaterCauldronBlock(
                            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    ));

    public static final RegistryObject<Block> WOODEN_LYE_WATER_CAULDRON =
            BLOCKS.register("wooden_lye_water_cauldron",
                    () -> new WoodenLyeWaterCauldronBlock(
                            BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
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