package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConwaysGameOfCobblestone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabContentsAdder {
    @SubscribeEvent
    public static void addCreativeModeTabContents(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(ConwaysGameOfCobblestone.EXAMPLE_BLOCK_ITEM);
    }
}
