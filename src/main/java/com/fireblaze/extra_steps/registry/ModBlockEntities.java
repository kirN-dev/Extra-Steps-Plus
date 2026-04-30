package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExtraSteps.MODID);

    public static final RegistryObject<BlockEntityType<DryingRackBlockEntity>> DRYING_RACK_BE =
            BLOCK_ENTITIES.register("drying_rack",
                    () -> BlockEntityType.Builder.of(
                            DryingRackBlockEntity::new,
                            ModBlocks.DRYING_RACK.get()
                            //ModBlocks.HANGING_DRYING_RACK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<BasketBlockEntity>> BASKET_BE =
            BLOCK_ENTITIES.register("basket",
                    () -> BlockEntityType.Builder.of(
                            BasketBlockEntity::new,
                            ModBlocks.BASKET.get()
                    ).build(null));

    // THIS IS JUST FOR COMPATIBILITY WITH WORLDS FROM OLDER MOD VERSIONS, DON'T USE IT ANYMORE FROM 1.1.0 ON
    public static final RegistryObject<BlockEntityType<ColorableBlockEntity>> COLORABLE_WOOL_BE_DEPRECATED =
            BLOCK_ENTITIES.register("colorable_wool",
                    () -> BlockEntityType.Builder.of(
                            ColorableBlockEntity::new
                    ).build(null));

    public static final RegistryObject<BlockEntityType<ColorableBlockEntity>> COLORABLE_BE =
            BLOCK_ENTITIES.register("colorable_block",
                    () -> BlockEntityType.Builder.of(
                            ColorableBlockEntity::new,
                            ModBlocks.COLORABLE_WOOL.get(),
                            ModBlocks.COLORABLE_TERRACOTTA.get(),
                            ModBlocks.COLORABLE_CONCRETE_POWDER.get(),
                            ModBlocks.COLORABLE_CONCRETE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<ColorableGlassBlockEntity>> COLORABLE_GLASS_BE =
            BLOCK_ENTITIES.register("colorable_glass_block",
                    () -> BlockEntityType.Builder.of(
                            ColorableGlassBlockEntity::new,
                            ModBlocks.COLORABLE_STAINED_GLASS.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<ColorableGlassPaneBlockEntity>> COLORABLE_GLASS_PANE_BE =
            BLOCK_ENTITIES.register("colorable_glass_pane_block",
                    () -> BlockEntityType.Builder.of(
                            ColorableGlassPaneBlockEntity::new,
                            ModBlocks.COLORABLE_STAINED_GLASS_PANE.get()
                    ).build(null));


    public static final RegistryObject<BlockEntityType<WoodenCauldronBlockEntity>> WOODEN_CAULDRON_BE =
            BLOCK_ENTITIES.register("wooden_cauldron",
                    () -> BlockEntityType.Builder.of(
                            WoodenCauldronBlockEntity::new,
                            ModBlocks.WOODEN_CAULDRON.get(),
                            ModBlocks.WOODEN_WATER_CAULDRON.get()
                    ).build(null));


    public static final RegistryObject<BlockEntityType<WoodenLyeWaterCauldronBlockEntity>> WOODEN_LYE_WATER_CAULDRON_BE =
            BLOCK_ENTITIES.register("wooden_lye_water_cauldron",
                    () -> BlockEntityType.Builder.of(
                            WoodenLyeWaterCauldronBlockEntity::new,
                            ModBlocks.WOODEN_LYE_WATER_CAULDRON.get()
                    ).build(null));


    public static final RegistryObject<BlockEntityType<LyeWaterCauldronBlockEntity>> LYE_WATER_CAULDRON_BE =
            BLOCK_ENTITIES.register("lye_water_cauldron",
                    () -> BlockEntityType.Builder.of(
                            LyeWaterCauldronBlockEntity::new,
                            ModBlocks.LYE_WATER_CAULDRON.get()
                    ).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}