package net.avangardum.gameofcobble.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.avangardum.gameofcobble.GameOfCobbleMod;
import net.avangardum.gameofcobble.GameOfCobbleRecipe;
import net.avangardum.gameofcobble.ModBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class GameOfCobbleRecipeCategory implements IRecipeCategory<GameOfCobbleRecipe> {
    private static final ResourceLocation UID = new ResourceLocation(GameOfCobbleMod.MOD_ID, "game_of_cobble");
    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation(GameOfCobbleMod.MOD_ID, "textures/gui/game_of_cobble_jei_gui.png");
    public static final RecipeType<GameOfCobbleRecipe> RECIPE_TYPE = new RecipeType<>(UID, GameOfCobbleRecipe.class);
    private static final int WIDTH = 113;
    private static final int HEIGHT = 60;

    private final IDrawable icon;

    public GameOfCobbleRecipeCategory(IGuiHelper helper) {
        icon = helper.createDrawableItemLike(ModBlocks.GAME_OF_COBBLE_BLOCK.get());
    }

    @Override
    public @NotNull RecipeType<GameOfCobbleRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("game_of_cobble");
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(
        @NotNull IRecipeLayoutBuilder builder,
        @NotNull GameOfCobbleRecipe recipe,
        @NotNull IFocusGroup focuses
    ) {
        builder.addInputSlot(92, 22).addItemLike(recipe.getItem());
        builder.addOutputSlot(92, 22).addItemLike(recipe.getItem());
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(
        @NotNull GameOfCobbleRecipe recipe,
        @NotNull IRecipeSlotsView recipeSlotsView,
        @NotNull GuiGraphics guiGraphics,
        double mouseX,
        double mouseY
    ) {
        guiGraphics.blit(BACKGROUND_TEXTURE, 0, 0, 0, 0, WIDTH, HEIGHT);
    }
}
