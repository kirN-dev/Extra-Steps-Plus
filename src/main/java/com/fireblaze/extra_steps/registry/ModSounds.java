package com.fireblaze.extra_steps.registry;

import com.fireblaze.extra_steps.ExtraSteps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExtraSteps.MODID);

    public static final RegistryObject<SoundEvent> STIR_1 =
            SOUND_EVENTS.register("stir1",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "stir1")
                    ));

    public static final RegistryObject<SoundEvent> STIR_2 =
            SOUND_EVENTS.register("stir2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "stir2")
                    ));

    public static final RegistryObject<SoundEvent> STIR_3 =
            SOUND_EVENTS.register("stir3",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "stir3")
                    ));

    public static final RegistryObject<SoundEvent> STIR_4 =
            SOUND_EVENTS.register("stir4",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "stir4")
                    ));

    public static final RegistryObject<SoundEvent> STIR_FINISH_1 =
            SOUND_EVENTS.register("stir_finish1",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "stir_finish1")
                    ));

    public static final RegistryObject<SoundEvent> STIR_FINISH_2 =
            SOUND_EVENTS.register("stir_finish2",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ExtraSteps.MODID, "stir_finish2")
                    ));
}