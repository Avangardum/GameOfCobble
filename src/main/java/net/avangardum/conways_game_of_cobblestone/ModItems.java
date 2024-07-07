package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

final class ModItems {
    public static final @NotNull DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ConwaysGameOfCobblestoneMod.MOD_ID);

    public static void register(@NotNull IEventBus bus) {
        ITEMS.register(bus);
    }
}
