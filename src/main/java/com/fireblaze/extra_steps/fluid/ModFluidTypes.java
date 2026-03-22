package com.fireblaze.extra_steps.fluid;

import com.fireblaze.extra_steps.ExtraSteps;
import com.fireblaze.extra_steps.registry.ModItems;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation LYE_WATER_STILL_RL = ResourceLocation.parse("block/water_still");
    public static final ResourceLocation LYE_WATER_FLOWING_RL = ResourceLocation.parse("block/water_flow");
    public static final ResourceLocation LYE_WATER_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "misc/in_soap_water");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ExtraSteps.MODID);

    public static final RegistryObject<FluidType> LYE_WATER_FLUID_TYPE = register("lye_water_fluid", FluidType.Properties.create().lightLevel(2).density(15).viscosity(5).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK));

    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(LYE_WATER_STILL_RL, LYE_WATER_FLOWING_RL, LYE_WATER_OVERLAY_RL, 0xAAC4C4CC, new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
