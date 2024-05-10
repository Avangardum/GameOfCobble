package net.avangardum.conways_game_of_cobblestone.datagen;

import net.avangardum.conways_game_of_cobblestone.ConwaysGameOfCobblestoneMod;
import net.avangardum.conways_game_of_cobblestone.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ConwaysGameOfCobblestoneMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
            ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get()
        );

        tag(BlockTags.NEEDS_STONE_TOOL).add(
            ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get()
        );
    }
}
