package net.avangardum.conways_game_of_cobblestone;

import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConwaysGameOfCobblestone.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabContentsAdder {
    @SubscribeEvent
    public static void addCreativeModeTabContents(BuildCreativeModeTabContentsEvent event)
    {

    }
}
