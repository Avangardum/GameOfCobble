package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ConwaysGameOfCobblestoneMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MenuScreenRegisterer {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.CONWAYS_GAME_OF_COBBLESTONE_MENU.get(),
                ConwaysGameOfCobblestoneScreen::new);
        });
    }
}
