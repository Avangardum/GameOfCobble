package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

final class ConwaysGameOfCobblestoneBlock extends BaseEntityBlock {
    private static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public ConwaysGameOfCobblestoneBlock(@NotNull Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ConwaysGameOfCobblestoneBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos position,
            @NotNull BlockState newState, boolean isMovedByPiston) {
        var blockEntity = getBlockEntity(level, position);
        blockEntity.dropInventory();
        super.onRemove(blockState, level, position, newState, isMovedByPiston);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos position,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            var blockEntity = getBlockEntity(level, position);
            NetworkHooks.openScreen((ServerPlayer)player, blockEntity, position);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(
            @NotNull BlockState blockState,
            @NotNull Level level,
            @NotNull BlockPos blockPos,
            @NotNull Block neighborBlock,
            @NotNull BlockPos neighborBlockPos,
            boolean isMovedByPiston) {
        assert level instanceof ServerLevel;
        var serverLevel = (ServerLevel) level;

        super.neighborChanged(blockState, level, blockPos, neighborBlock, neighborBlockPos, isMovedByPiston);
        var isTriggered = level.hasNeighborSignal(blockPos);
        var wasTriggered = blockState.getValue(TRIGGERED);
        if (!wasTriggered && isTriggered) {
            level.setBlock(blockPos, blockState.setValue(TRIGGERED, true), SetBlockFlags.PREVENT_RERENDER);
            var blockEntity = (ConwaysGameOfCobblestoneBlockEntity)
                    new BlockSourceImpl(serverLevel, blockPos).getEntity();
            blockEntity.tick();
        }
        else if (wasTriggered && !isTriggered) {
            level.setBlock(blockPos, blockState.setValue(TRIGGERED, false), SetBlockFlags.PREVENT_RERENDER);
        }
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    private @NotNull ConwaysGameOfCobblestoneBlockEntity getBlockEntity(@NotNull Level level, @NotNull BlockPos position) {
        var result = (ConwaysGameOfCobblestoneBlockEntity)level.getBlockEntity(position);
        assert result != null;
        return result;
    }
}