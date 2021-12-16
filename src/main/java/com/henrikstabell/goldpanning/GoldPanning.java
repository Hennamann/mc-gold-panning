package com.henrikstabell.goldpanning;

import com.henrikstabell.goldpanning.item.ItemDiamondFragment;
import com.henrikstabell.goldpanning.item.ItemGoldDust;
import com.henrikstabell.goldpanning.item.ItemGoldPan;
import com.henrikstabell.goldpanning.item.ItemIronDust;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GoldPanning.MOD_ID)
public class GoldPanning {

    public static final String MOD_ID = "goldpanning";

    public static IEventBus MOD_EVENT_BUS;

    public static ItemGoldPan gold_pan;
    public static ItemGoldDust gold_dust;
    public static ItemIronDust iron_dust;
    public static ItemDiamondFragment diamond_fragment;

    public GoldPanning() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemsRegistration(RegistryEvent.Register<Item> itemRegisterEvent) {
        gold_pan = new ItemGoldPan();
        gold_pan.setRegistryName("gold_pan");
        itemRegisterEvent.getRegistry().register(gold_pan);

        gold_dust = new ItemGoldDust();
        gold_dust.setRegistryName("gold_dust");
        itemRegisterEvent.getRegistry().register(gold_dust);

        iron_dust = new ItemIronDust();
        iron_dust.setRegistryName("iron_dust");
        itemRegisterEvent.getRegistry().register(iron_dust);

        diamond_fragment = new ItemDiamondFragment();
        diamond_fragment.setRegistryName("diamond_fragment");
        itemRegisterEvent.getRegistry().register(diamond_fragment);
    }

    private void setup(final FMLCommonSetupEvent event) {}

    private void doClientStuff(final FMLClientSetupEvent event) {}

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {}
}
