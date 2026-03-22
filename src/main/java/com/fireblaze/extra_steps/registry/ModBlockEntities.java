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

    public static final RegistryObject<BlockEntityType<ColorableWoolBlockEntity>> COLORABLE_WOOL_BE =
            BLOCK_ENTITIES.register("colorable_wool",
                    () -> BlockEntityType.Builder.of(
                            ColorableWoolBlockEntity::new,
                            ModBlocks.COLORABLE_WOOL.get()
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