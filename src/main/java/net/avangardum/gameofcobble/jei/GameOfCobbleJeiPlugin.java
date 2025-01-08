package net.avangardum.gameofcobble.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.avangardum.gameofcobble.GameOfCobbleMod;
import net.avangardum.gameofcobble.GameOfCobbleRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static net.avangardum.gameofcobble.Util.assertNotNull;

@JeiPlugin
public final class GameOfCobbleJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation(GameOfCobbleMod.MOD_ID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GameOfCobbleRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        var recipes = assertNotNull(Minecraft.getInstance().level).getRecipeManager()
                .getAllRecipesFor(GameOfCobbleRecipe.Type.INSTANCE);

        registration.addRecipes(GameOfCobbleRecipeCategory.RECIPE_TYPE, recipes);
    }
}
