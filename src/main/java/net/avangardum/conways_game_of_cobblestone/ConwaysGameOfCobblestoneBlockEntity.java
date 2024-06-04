package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConwaysGameOfCobblestoneBlockEntity extends BlockEntity {
    public ConwaysGameOfCobblestoneBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.CONWAYS_GAME_OF_COBBLESTONE_BET.get(), blockPos, blockState);
    }
}
