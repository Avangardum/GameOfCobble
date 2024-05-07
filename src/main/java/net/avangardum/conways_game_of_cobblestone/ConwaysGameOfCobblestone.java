package net.avangardum.conways_game_of_cobblestone;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ConwaysGameOfCobblestone.MOD_ID)
public class ConwaysGameOfCobblestone
{
    public static final String MOD_ID = "conways_game_of_cobblestone";

    public ConwaysGameOfCobblestone()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
