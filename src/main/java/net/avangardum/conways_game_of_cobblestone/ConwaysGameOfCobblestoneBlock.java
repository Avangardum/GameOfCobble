package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConwaysGameOfCobblestoneBlock extends BaseEntityBlock {
    public ConwaysGameOfCobblestoneBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ConwaysGameOfCobblestoneBlockEntity(blockPos, blockState);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}