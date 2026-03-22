package com.fireblaze.extra_steps;

import com.fireblaze.extra_steps.blockentity.ColorableWoolBlockEntity;
import com.fireblaze.extra_steps.blockentity.LyeWaterCauldronBlockEntity;
import com.fireblaze.extra_steps.client.color.*;
import com.fireblaze.extra_steps.fluid.*;
import com.fireblaze.extra_steps.loot.ModLootModifiers;
import com.fireblaze.extra_steps.processing.ProcessingItemRegistry;
import com.fireblaze.extra_steps.registry.*;
import com.fireblaze.extra_steps.util.GenericColorHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtraSteps.MODID)
public class ExtraSteps
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "extra_steps";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ExtraSteps(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        // Fluid-Supplier aufsetzen *nach* Block-Registrierung
        ModFluids.register(modEventBus);
        ModFluidTypes.register(modEventBus);

        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        ModRecipeSerializers.SERIALIZERS.register(modEventBus);

        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        ModLootModifiers.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_LYE_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_LYE_WATER.get(), RenderType.translucent());

            Minecraft.getInstance().getBlockColors().register(
                    (state, level, pos, tintIndex) -> 0xAAC4C4CC,
                    ModBlocks.LYE_WATER_CAULDRON.get()
            );

            Minecraft.getInstance().getItemColors().register(
                    new DryingItemColor(),
                    ModItems.WET_HIDE.get(),
                    ModItems.WET_WOOL.get());

            Minecraft.getInstance().getItemColors().register(
                    new GenericItemColoring(),
                    ModItems.CRUMBLED_WOOL.get(),
                    ModItems.RAW_WOOL.get(),
                    ModItems.FIBERED_WOOL.get(),
                    ModItems.BRUSHED_WOOL.get(),
                    ModBlocks.COLORABLE_WOOL.get().asItem(),
                    ModItems.LYE_WATER_BUCKET.get(),

                    Items.LEATHER_HELMET,
                    Items.LEATHER_CHESTPLATE,
                    Items.LEATHER_LEGGINGS,
                    Items.LEATHER_BOOTS

            );

            Minecraft.getInstance().getBlockColors().register(
                    (state, level, pos, tintIndex) -> {
                        if(level != null && pos != null) {
                            var be = level.getBlockEntity(pos);
                            if(be instanceof ColorableWoolBlockEntity wool) {
                                return wool.getColor();
                            }
                        }
                        return 0xFFFFFF;
                    },
                    ModBlocks.COLORABLE_WOOL.get()
            );



            Minecraft.getInstance().getBlockColors().register(
                    (state, level, pos, tintIndex) -> {
                        if(level != null && pos != null) {
                            var be = level.getBlockEntity(pos);
                            if(be instanceof LyeWaterCauldronBlockEntity dyeCauldron) {
                                return dyeCauldron.getColor(); // die gemixte Farbe zurückgeben
                            }
                        }
                        return 0xFFFFFF; // default: weiß
                    },
                    ModBlocks.LYE_WATER_CAULDRON.get()
            );
        }
    }

    @Mod.EventBusSubscriber(modid = ExtraSteps.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLevelLoad(LevelEvent.Load event) {
            LevelAccessor levelAccessor = event.getLevel();

            if (!levelAccessor.isClientSide() && levelAccessor instanceof Level level) {
                ModCauldronInteractions.registerCauldronInteractions(level);
                //ModDyeCauldronInteractions.registerDyeCauldronInteractions();
            }
        }
    }
}
