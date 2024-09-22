package net.avangardum.gameofcobble;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

final class GameOfCobbleBlock extends BaseEntityBlock {
    private static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public GameOfCobbleBlock(@NotNull Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new GameOfCobbleBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos position,
            @NotNull BlockState newBlockState, boolean isMovedByPiston) {
        if (blockState.getBlock() != newBlockState.getBlock()) {
            var blockEntity = getBlockEntity(level, position);
            blockEntity.dropInventory();
        }

        super.onRemove(blockState, level, position, newBlockState, isMovedByPiston);
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
        super.neighborChanged(blockState, level, blockPos, neighborBlock, neighborBlockPos, isMovedByPiston);
        var isTriggered = level.hasNeighborSignal(blockPos);
        var wasTriggered = blockState.getValue(TRIGGERED);
        if (!wasTriggered && isTriggered) {
            level.setBlock(blockPos, blockState.setValue(TRIGGERED, true), SetBlockFlags.PREVENT_RERENDER);
            var blockEntity = getBlockEntity(level, blockPos);
            blockEntity.redstoneTick();
        }
        else if (wasTriggered && !isTriggered) {
            level.setBlock(blockPos, blockState.setValue(TRIGGERED, false), SetBlockFlags.PREVENT_RERENDER);
        }
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
        builder.add(FACING);
    }

    private @NotNull GameOfCobbleBlockEntity getBlockEntity(@NotNull Level level, @NotNull BlockPos position) {
        var result = (GameOfCobbleBlockEntity)level.getBlockEntity(position);
        assert result != null;
        return result;
    }
}