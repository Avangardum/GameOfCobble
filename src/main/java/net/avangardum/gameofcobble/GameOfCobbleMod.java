package net.avangardum.gameofcobble;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GameOfCobbleMod.MOD_ID)
public final class GameOfCobbleMod
{
    public static final String MOD_ID = "gameofcobble";

    public GameOfCobbleMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
}
