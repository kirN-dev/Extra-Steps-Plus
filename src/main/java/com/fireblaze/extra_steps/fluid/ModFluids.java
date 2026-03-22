package com.fireblaze.extra_steps.fluid;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.registry.ModBlocks;
import com.fireblaze.extra_steps.registry.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ExtraSteps.MODID);

    public static final RegistryObject<FlowingFluid> SOURCE_LYE_WATER = FLUIDS.register("lye_water_fluid", () -> new ForgeFlowingFluid.Source(ModFluids.LYE_WATER_FLUID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_LYE_WATER = FLUIDS.register("flowing_lye_water", () -> new ForgeFlowingFluid.Flowing(ModFluids.LYE_WATER_FLUID_PROPERTIES));

    public static final ForgeFlowingFluid.Properties LYE_WATER_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.LYE_WATER_FLUID_TYPE, SOURCE_LYE_WATER, FLOWING_LYE_WATER).slopeFindDistance(2).levelDecreasePerBlock(1).block(ModBlocks.LYE_WATER_BLOCK).bucket(ModItems.LYE_WATER_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
