package net.avangardum.gameofcobble.datagen;

import net.avangardum.gameofcobble.ModBlocks;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

final class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GAME_OF_COBBLE_BLOCK.get())
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', Items.IRON_NUGGET)
                .define('C', Blocks.COBBLESTONE)
                .unlockedBy("has_iron_ingot",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(writer);
    }
}
