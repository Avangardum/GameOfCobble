package net.avangardum.gameofcobble.datagen;

import net.avangardum.gameofcobble.GameOfCobbleMod;
import net.avangardum.gameofcobble.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

final class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(@NotNull PackOutput output, @NotNull ExistingFileHelper exFileHelper) {
        super(output, GameOfCobbleMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK);
    }

    private void blockWithItem(@NotNull RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
