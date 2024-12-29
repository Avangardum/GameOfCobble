package net.avangardum.gameofcobble;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

final class ModMenuTypes {
    public static final @NotNull DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, GameOfCobbleMod.MOD_ID);

    public static final @NotNull RegistryObject<MenuType<GameOfCobbleMenu>> GAME_OF_COBBLE_MENU_TYPE =
            registerMenuType(GameOfCobbleMenu::new, "game_of_cobble_menu_type");

    public static void register(@NotNull IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    private static <T extends AbstractContainerMenu> @NotNull RegistryObject<MenuType<T>> registerMenuType(
            @NotNull IContainerFactory<T> factory, @NotNull String name) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }
}
