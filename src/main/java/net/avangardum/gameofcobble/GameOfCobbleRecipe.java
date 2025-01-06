package net.avangardum.gameofcobble;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A recipe for the Game of Cobble. Consists only of a single item and marks that this item is usable in the game.
 * The Game of Cobble block entity only uses these recipes to get a list of usable items, therefore, some methods are
 * not supported.
 */
public final class GameOfCobbleRecipe implements Recipe<SimpleContainer> {
    public static class Type implements RecipeType<GameOfCobbleRecipe> {
        public static final @NotNull Type INSTANCE = new Type();
        private Type() {}
    }

    public static class Serializer implements RecipeSerializer<GameOfCobbleRecipe> {
        public static final @NotNull Serializer INSTANCE = new Serializer();

        private Serializer() {}

        @Override
        public @NotNull GameOfCobbleRecipe fromJson(
                @NotNull ResourceLocation recipeId,
                @NotNull JsonObject serializedRecipe
        ) {
            var item = ShapedRecipe.itemFromJson(serializedRecipe);
            return new GameOfCobbleRecipe(recipeId, item);
        }

        @Override
        public @Nullable GameOfCobbleRecipe fromNetwork(
                @NotNull ResourceLocation recipeId,
                @NotNull FriendlyByteBuf buffer
        ) {
            var itemStack = buffer.readItem();
            return new GameOfCobbleRecipe(recipeId, itemStack.getItem());
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull GameOfCobbleRecipe recipe) {
            buffer.writeItem(recipe.getResultItem(null));
        }
    }

    private final @NotNull ResourceLocation id;
    private final @NotNull Item item;

    public GameOfCobbleRecipe(@NotNull ResourceLocation id, @NotNull Item item) {
        this.id = id;
        this.item = item;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer container, @NotNull Level level) {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess registryAccess) {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public @NotNull ItemStack getResultItem(@Nullable RegistryAccess registryAccess) {
        return new ItemStack(item, 1);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public @NotNull Item getItem() {
        return item;
    }
}
