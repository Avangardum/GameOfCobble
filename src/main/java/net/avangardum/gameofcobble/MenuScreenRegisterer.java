package net.avangardum.gameofcobble;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = GameOfCobbleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
final class MenuScreenRegisterer {
    @SubscribeEvent
    public static void clientSetup(@NotNull FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.GAME_OF_COBBLE_MENU.get(),
                GameOfCobbleScreen::new);
        });
    }
}
