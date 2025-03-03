package net.avangardum.gameofcobble;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GameOfCobbleMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<GameOfCobbleRecipe>> GAME_OF_COBBLE_SERIALIZER =
            SERIALIZERS.register("game_of_cobble", () -> GameOfCobbleRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
