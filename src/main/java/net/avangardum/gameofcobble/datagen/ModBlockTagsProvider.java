package net.avangardum.gameofcobble.datagen;

import net.avangardum.gameofcobble.ConwaysGameOfCobblestoneMod;
import net.avangardum.gameofcobble.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

final class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(@NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @NotNull ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ConwaysGameOfCobblestoneMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get()
        );

        tag(BlockTags.NEEDS_STONE_TOOL).add(
                ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get()
        );
    }
}
