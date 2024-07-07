package net.avangardum.conways_game_of_cobblestone;

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
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ConwaysGameOfCobblestoneMod.MOD_ID);

    public static final @NotNull RegistryObject<MenuType<ConwaysGameOfCobblestoneMenu>> CONWAYS_GAME_OF_COBBLESTONE_MENU =
            registerMenuType(ConwaysGameOfCobblestoneMenu::new, "conways_game_of_cobblestone_menu");

    public static void register(@NotNull IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    private static <T extends AbstractContainerMenu> @NotNull RegistryObject<MenuType<T>> registerMenuType(
            @NotNull IContainerFactory<T> factory, @NotNull String name) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }
}
