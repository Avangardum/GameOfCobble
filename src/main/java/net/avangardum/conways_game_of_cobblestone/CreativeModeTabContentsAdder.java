package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConwaysGameOfCobblestoneMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabContentsAdder {
    @SubscribeEvent
    public static void addCreativeModeTabContents(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK);
        }
    }
}
