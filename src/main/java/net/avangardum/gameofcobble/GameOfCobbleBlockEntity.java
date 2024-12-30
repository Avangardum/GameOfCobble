package net.avangardum.gameofcobble;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

import static net.avangardum.gameofcobble.Util.*;

final class GameOfCobbleBlockEntity extends BlockEntity implements MenuProvider {
    // This block uses the mechanic of clusters. A cluster is a set of blocks that all face the same direction, lie in
    // the same vertical plane and are adjacent to each other at least with a corner.
    // In such a cluster, one of the horizontal coordinates (X or Z) is constant for all blocks in the cluster, it is
    // referred to as C, another horizontal coordinate is variable for different blocks and referred to as V.
    // A Game of Life grid is 2 cells bigger than its corresponding cluster. This creates a 1 cell thick border needed
    // to process drops.

    public static final int GRID_SIDE = 8;
    public static final int GRID_AREA = GRID_SIDE * GRID_SIDE;
    private static final String INVENTORY_SAVE_KEY = "inventory";

    private final ItemStackHandler itemHandler = new ItemStackHandler(GRID_AREA) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            var cluster = getCluster();
            if (cluster.getErrors().hasAny()) return false;
            if (cluster.getItem() != null) {
                return stack.is(cluster.getItem());
            }
            else {
                return Config.getUsableItems().contains(stack.getItem());
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private @NotNull LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private long lastRedstoneTickTime = -1;
    private @Nullable GameOfCobbleCluster cachedCluster;
    private long clusterCachingTime = -1;

    public GameOfCobbleBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(ModBlockEntityTypes.GAME_OF_COBBLE_BLOCK_ENTITY_TYPE.get(), blockPos, blockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Game of Cobble");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory,
            @NotNull Player player) {
        return new GameOfCobbleMenu(containerId, playerInventory, this);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();

        return super.getCapability(capability, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound(INVENTORY_SAVE_KEY));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public @NotNull GameOfLifeGrid getDisplayGrid() {
        if (getCluster().getErrors().hasAny()) return getErrorDisplayGrid();

        var flatCells = IntStream.range(0, GRID_AREA)
                .mapToObj(x -> !itemHandler.getStackInSlot(x).isEmpty())
                .toArray(Boolean[]::new);
        return new GameOfLifeGrid(GRID_SIDE, GRID_SIDE, flatCells);
    }

    private @NotNull GameOfLifeGrid getErrorDisplayGrid() {
        if (getCluster().getErrors().tooBig()) return ErrorDisplayGrids.TOO_BIG_CLUSTER;
        else if (getCluster().getErrors().illegalItem()) return ErrorDisplayGrids.ILLEGAL_ITEM;
        else if (getCluster().getErrors().mixedItems()) return ErrorDisplayGrids.MIXED_ITEMS;
        else throw new IllegalStateException("Unknown cluster error");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        pTag.put(INVENTORY_SAVE_KEY, itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    public void dropInventory() {
        var container = new SimpleContainer(GRID_AREA);
        for (int i = 0; i < GRID_AREA; i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert level != null;
        Containers.dropContents(level, worldPosition, container);
    }

    public void redstoneTick() {
        assert level != null;

        if (level.getGameTime() == lastRedstoneTickTime) return;

        var cluster = getCluster();
        if (cluster.getErrors().hasAny()) return;
        var grid = getGridFromCluster(cluster);
        grid.proceedToNextGeneration();
        setGridToCluster(grid, cluster);
        processDrops(grid, cluster);
    }

    private @NotNull GameOfCobbleCluster getCluster() {
        assert level != null;

        if (level.getGameTime() == clusterCachingTime) return assertNotNull(cachedCluster);

        var cluster = getClusterWithoutCaching();
        for (var blockEntity : cluster.getBlockEntities()) {
            blockEntity.cachedCluster = cluster;
            blockEntity.clusterCachingTime = level.getGameTime();
        }

        return cluster;
    }

    private @NotNull GameOfCobbleCluster getClusterWithoutCaching() {
        assert level != null;

        var blockEntitiesInCluster = new HashSet<GameOfCobbleBlockEntity>();
        blockEntitiesInCluster.add(this);
        Queue<GameOfCobbleBlockEntity> blockEntitiesToSearchForNeighbors = new ArrayDeque<>();
        blockEntitiesToSearchForNeighbors.add(this);
        var errors = GameOfCobbleCluster.Errors.NONE;

        whileBreakLabel:
        while (!blockEntitiesToSearchForNeighbors.isEmpty()) {
            var origin = blockEntitiesToSearchForNeighbors.remove();
            var originY = origin.getY();
            var originV = origin.getV();

            for (var neighborY = originY - 1; neighborY <= originY + 1; neighborY++) {
                for (var neighborV = originV - 1; neighborV <= originV + 1; neighborV++) {
                    if (neighborY == originY && neighborV == originV) continue;

                    var neighborX = getXFromV(neighborV);
                    var neighborZ = getZFromV(neighborV);
                    var neighbor = as(GameOfCobbleBlockEntity.class,
                            level.getBlockEntity(new BlockPos(neighborX, neighborY, neighborZ)));
                    if (neighbor == null) continue;

                    var isNeighborNew = blockEntitiesInCluster.add(neighbor);
                    if (isNeighborNew) blockEntitiesToSearchForNeighbors.add(neighbor);

                    if (blockEntitiesInCluster.size() > Config.getMaxClusterSize()) {
                        errors = errors.withTooBig();
                        break whileBreakLabel;
                    }
                }
            }
        }

        var getClusterItemResult = getClusterItem(blockEntitiesInCluster);
        var item = getClusterItemResult.item;
        if (getClusterItemResult.mixedItems()) errors = errors.withMixedItems();
        else if (item != null && !Config.getUsableItems().contains(item)) errors = errors.withIllegalItem();

        return new GameOfCobbleCluster(
            Collections.unmodifiableSet(blockEntitiesInCluster),
            getClusterStartPos(blockEntitiesInCluster),
            getClusterEndPos(blockEntitiesInCluster),
            item,
            errors
        );
    }

    private record GetClusterItemResult(@Nullable Item item, boolean mixedItems) {}

    private @NotNull GetClusterItemResult getClusterItem(Set<GameOfCobbleBlockEntity> blockEntities) {
        Item item = null;
        for (var blockEntity : blockEntities) {
            for (var slot = 0; slot < blockEntity.itemHandler.getSlots(); slot++) {
                var stackInSlot = blockEntity.itemHandler.getStackInSlot(slot);
                if (stackInSlot.isEmpty()) continue;
                var itemInSlot = stackInSlot.getItem();
                if (item == null) {
                    item = itemInSlot;
                }
                else if (item != itemInSlot) {
                    return new GetClusterItemResult(null, true);
                }
            }
        }
        return new GetClusterItemResult(item, false);
    }

    private @NotNull BlockPos getClusterStartPos(Set<GameOfCobbleBlockEntity> blockEntities) {
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int z = Integer.MAX_VALUE;
        for (var blockEntity : blockEntities) {
            if (blockEntity.getX() < x) x = blockEntity.getX();
            if (blockEntity.getY() < y) y = blockEntity.getY();
            if (blockEntity.getZ() < z) z = blockEntity.getZ();
        }
        return new BlockPos(x, y, z);
    }

    private @NotNull BlockPos getClusterEndPos(Set<GameOfCobbleBlockEntity> blockEntities) {
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;
        for (var blockEntity : blockEntities) {
            if (blockEntity.getX() > x) x = blockEntity.getX();
            if (blockEntity.getY() > y) y = blockEntity.getY();
            if (blockEntity.getZ() > z) z = blockEntity.getZ();
        }
        return new BlockPos(x, y, z);
    }

    private @NotNull GameOfLifeGrid getGridFromCluster(@NotNull GameOfCobbleCluster cluster) {
        assert !cluster.getErrors().hasAny();

        var clusterHeightInBlocks = cluster.getEndY() - cluster.getStartY() + 1;
        var clusterHeightInCells = clusterHeightInBlocks * GRID_SIDE;
        var clusterWidthInBlocks = cluster.getEndV() - cluster.getStartV() + 1;
        var clusterWidthInCells = clusterWidthInBlocks * GRID_SIDE;
        // Add 2 cells to each dimension to have a 1 cell thick border for processing drops.
        var grid = new GameOfLifeGrid(clusterHeightInCells + 2, clusterWidthInCells + 2);

        for (var blockEntity : cluster.getBlockEntities()) {
            var rowOffset = getRowOffset(blockEntity, cluster);
            var columnOffset = getColumnOffset(blockEntity, cluster);
            for (var row = 0; row < GRID_SIDE; row++) {
                for (var column = 0; column < GRID_SIDE; column++) {
                    var isLivingCell =
                            !blockEntity.itemHandler.getStackInSlot(getSlotFromLocalRowColumn(row, column)).isEmpty();
                    grid.setIsCellLiving(row + rowOffset, column + columnOffset, isLivingCell);
                }
            }
        }

        return grid;
    }

    private void setGridToCluster(@NotNull GameOfLifeGrid grid, @NotNull GameOfCobbleCluster cluster) {
        assert !cluster.getErrors().hasAny();
        assert level != null;

        for (var blockEntity : cluster.getBlockEntities()) {
            blockEntity.lastRedstoneTickTime = level.getGameTime();
            var rowOffset = getRowOffset(blockEntity, cluster);
            var columnOffset = getColumnOffset(blockEntity, cluster);
            for (var row = 0; row < GRID_SIDE; row++) {
                for (var column = 0; column < GRID_SIDE; column++) {
                    var isCellLiving = grid.isCellLiving(row + rowOffset, column + columnOffset);
                    blockEntity.itemHandler.setStackInSlot(getSlotFromLocalRowColumn(row, column),
                            isCellLiving ? new ItemStack(assertNotNull(cluster.getItem()), 1) : ItemStack.EMPTY);
                }
            }
        }
    }

    private int getSlotFromLocalRowColumn(int row, int column) {
        return row * GRID_SIDE + column;
    }

    private void processDrops(GameOfLifeGrid grid, GameOfCobbleCluster cluster) {
        assert !cluster.getErrors().hasAny();
        assert level != null;

        for (var row = 0; row < grid.getHeight(); row++) {
            for (var column = 0; column < grid.getWidth(); column++) {
                if (!grid.isCellLiving(row, column)) continue;
                var blockPos = getCorrespondingBlockPosFromGridRowColumn(cluster, row, column);
                if (isBlockPosOccupied(blockPos)) continue;
                var container = new SimpleContainer(new ItemStack(assertNotNull(cluster.getItem()), 1));
                Containers.dropContents(level, blockPos, container);
            }
        }
    }

    private BlockPos getCorrespondingBlockPosFromGridRowColumn(
        @NotNull GameOfCobbleCluster cluster,
        int row,
        int column
    ) {
        var y = cluster.getEndY() - Math.floor((double)(row - 1) / GRID_SIDE);
        var v = doesVAxisPointRight() ?
                cluster.getStartV() + Math.floor((double)(column - 1) / GRID_SIDE) :
                cluster.getEndV() - Math.floor((double)(column - 1) / GRID_SIDE);
        return getBlockPosAtVY((int)v, (int)y);
    }

    private boolean isBlockPosOccupied(@NotNull BlockPos blockPos) {
        assert level != null;
        var block = level.getBlockState(blockPos).getBlock();
        return block != Blocks.AIR;
        // TODO Add support for blocks other than air.
    }

    /**
     * For the given block entity in the given cluster determine the value that needs to be added to a local row
     * index in order to get the corresponding row index of the grid.
     */
    private int getRowOffset(@NotNull GameOfCobbleBlockEntity blockEntity, @NotNull GameOfCobbleCluster cluster) {
        return (cluster.getEndY() - blockEntity.worldPosition.getY()) * GRID_SIDE + 1;
    }

    /**
     * For the given block entity in the given cluster determine the value that needs to be added to a local column
     * index in order to get the corresponding column index of the grid.
     */
    private int getColumnOffset(@NotNull GameOfCobbleBlockEntity blockEntity, @NotNull GameOfCobbleCluster cluster) {
        if (doesVAxisPointRight()) {
            return (blockEntity.getV() - cluster.getStartV()) * GRID_SIDE + 1;
        }
        else {
            return (cluster.getEndV() - blockEntity.getV()) * GRID_SIDE + 1;
        }
    }

    /**
     * Determine if the V axis points right when a player is looking at a cluster's front.
     */
    private boolean doesVAxisPointRight() {
        return getHorizontalFacing() == Direction.SOUTH || getHorizontalFacing() == Direction.WEST;
    }

    private @NotNull BlockPos getBlockPosAtVY(int v, int y) {
        return new BlockPos(getXFromV(v), y, getZFromV(v));
    }

    public int getX() {
        return worldPosition.getX();
    }

    public int getY() {
        return worldPosition.getY();
    }

    public int getZ() {
        return worldPosition.getZ();
    }

    public int getV() {
        return getVFromXZ(getX(), getZ());
    }

    public int getC() {
        return isVAxisXAxis() ? worldPosition.getZ() : worldPosition.getX();
    }

    public int getXFromV(int v) {
        return isVAxisXAxis() ? v : getC();
    }

    public int getZFromV(int v) {
        return isVAxisXAxis() ? getC() : v;
    }

    public int getVFromXZ(int x, int z) {
        return isVAxisXAxis() ? x : z;
    }

    public boolean isVAxisXAxis() {
        return getHorizontalFacing() == Direction.NORTH || getHorizontalFacing() == Direction.SOUTH;
    }

    private @NotNull Direction getHorizontalFacing() {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
}