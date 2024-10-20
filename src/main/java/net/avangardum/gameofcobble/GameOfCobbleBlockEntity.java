package net.avangardum.gameofcobble;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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

final class GameOfCobbleBlockEntity extends BlockEntity implements MenuProvider {
    // This block uses the mechanic of clusters. A cluster is a set of blocks that all face the same direction and are
    // adjacent to each other at least with a corner.
    // In such a cluster, one of the horizontal coordinates (X or Z) is constant for all blocks in the cluster, it is
    // referred to as C, another horizontal coordinate is variable for different blocks and referred to as V.

    private record Cluster(
        Set<GameOfCobbleBlockEntity> blockEntities,
        BlockPos startPos,
        BlockPos endPos,
        Vec3i vUnit
    ) {}

    public static final int GRID_HEIGHT = 10;
    public static final int GRID_WIDTH = 10;
    public static final int GRID_SIZE = GRID_HEIGHT * GRID_WIDTH;
    private static final String INVENTORY_SAVE_KEY = "inventory";

    private final ItemStackHandler itemHandler = new ItemStackHandler(GRID_SIZE) {
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
            return stack.is(Blocks.COBBLESTONE.asItem());
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private @NotNull LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private long lastRedstoneTickTime = -1;

    public GameOfCobbleBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(ModBlockEntityTypes.CONWAYS_GAME_OF_COBBLESTONE_BET.get(), blockPos, blockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Conway's Game of Cobblestone");
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

    public void dropInventory() {
        var container = new SimpleContainer(GRID_SIZE);
        for (int i = 0; i < GRID_SIZE; i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert level != null;
        Containers.dropContents(level, worldPosition, container);
    }

    public void redstoneTick() {
        assert level != null;
        if (level.getGameTime() == lastRedstoneTickTime) return;
        var cluster = getCluster();
        var grid = getGridFromCluster(cluster);
        grid.proceedToNextGeneration();
        setGridToCluster(grid, cluster);
    }

    private Cluster getCluster() {
        var horizontalFacing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        var vUnit = horizontalFacing == Direction.NORTH || horizontalFacing == Direction.SOUTH ?
                UnitVec3i.X : UnitVec3i.Z;
        var c = vUnit == UnitVec3i.X ? worldPosition.getZ() : worldPosition.getX();

        var startX = worldPosition.getX();
        var endX = worldPosition.getX();
        var startY = worldPosition.getY();
        var endY = worldPosition.getY();
        var startZ = worldPosition.getZ();
        var endZ = worldPosition.getZ();
        var blockEntitiesInCluster = new HashSet<GameOfCobbleBlockEntity>();
        blockEntitiesInCluster.add(this);
        Queue<GameOfCobbleBlockEntity> blockEntitiesToSearchForNeighbors = new ArrayDeque<>();
        blockEntitiesToSearchForNeighbors.add(this);

        while (!blockEntitiesToSearchForNeighbors.isEmpty()) {
            var origin = blockEntitiesToSearchForNeighbors.remove();
            var originY = origin.worldPosition.getY();
            var originV = vUnit == UnitVec3i.X ?
                    origin.worldPosition.getX() : origin.worldPosition.getZ();

            for (var neighborY = originY - 1; neighborY <= originY + 1; neighborY++) {
                for (var neighborV = originV - 1; neighborV <= originV + 1; neighborV++) {
                    if (neighborY == originY && neighborV == originV) continue;
                    var neighborX = vUnit == UnitVec3i.X ? neighborV : c;
                    var neighborZ = vUnit == UnitVec3i.Z ? neighborV : c;
                    assert origin.level != null;
                    var neighbor = origin.level.getBlockEntity(new BlockPos(neighborX, neighborY, neighborZ));
                    var gameOfCobbleNeighbor = neighbor instanceof GameOfCobbleBlockEntity ?
                            (GameOfCobbleBlockEntity)neighbor : null;
                    if (gameOfCobbleNeighbor == null) continue;

                    var isNeighborNew = blockEntitiesInCluster.add(gameOfCobbleNeighbor);
                    if (isNeighborNew) blockEntitiesToSearchForNeighbors.add(gameOfCobbleNeighbor);

                    if (neighborX < startX) startX = neighborX;
                    else if (neighborX > endX) endX = neighborX;
                    if (neighborY < startY) startY = neighborY;
                    else if (neighborY > endY) endY = neighborY;
                    if (neighborZ < startZ) startZ = neighborZ;
                    else if (neighborZ > endZ) endZ = neighborZ;
                }
            }
        }

        return new Cluster(
            Collections.unmodifiableSet(blockEntitiesInCluster),
            new BlockPos(startX, startY, startZ),
            new BlockPos(endX, endY, endZ),
            vUnit
        );
    }

    private GameOfLifeGrid getGridFromCluster(Cluster cluster) {
        var heightInBlocks = cluster.endPos.getY() - cluster.startPos.getY() + 1;
        var heightInCells = heightInBlocks * GRID_HEIGHT;
        var widthInBlocks = (cluster.vUnit == UnitVec3i.X ?
                cluster.endPos.getX() - cluster.startPos.getX() : cluster.endPos.getZ() - cluster.startPos.getZ()) + 1;
        var widthInCells = widthInBlocks * GRID_WIDTH;
        var grid = new GameOfLifeGrid(widthInCells, heightInCells);

        for (var blockEntity : cluster.blockEntities) {
            var rowOffset = getRowOffset(blockEntity, cluster);
            var columnOffset = getColumnOffset(blockEntity, cluster);
            for (var row = 0; row < GRID_HEIGHT; row++) {
                for (var column = 0; column < GRID_WIDTH; column++) {
                    var slot = row * GRID_WIDTH + column;
                    var isLivingCell = !blockEntity.itemHandler.getStackInSlot(slot).isEmpty();
                    grid.setIsCellLiving(row + rowOffset, column + columnOffset, isLivingCell);
                }
            }
        }

        return grid;
    }

    private void setGridToCluster(GameOfLifeGrid grid, Cluster cluster) {
        assert level != null;
        for (var blockEntity : cluster.blockEntities) {
            blockEntity.lastRedstoneTickTime = level.getGameTime();
            var rowOffset = getRowOffset(blockEntity, cluster);
            var columnOffset = getColumnOffset(blockEntity, cluster);
            for (var row = 0; row < GRID_HEIGHT; row++) {
                for (var column = 0; column < GRID_WIDTH; column++) {
                    var slot = row * GRID_WIDTH + column;
                    var isCellLiving = grid.isCellLiving(row + rowOffset, column + columnOffset);
                    blockEntity.itemHandler.setStackInSlot(slot,
                            isCellLiving ? new ItemStack(Blocks.COBBLESTONE, 1) : ItemStack.EMPTY);
                }
            }
        }
    }

    private int getRowOffset(GameOfCobbleBlockEntity blockEntity, Cluster cluster) {
        return (cluster.endPos.getY() - blockEntity.worldPosition.getY()) * GRID_HEIGHT;
    }

    private int getColumnOffset(GameOfCobbleBlockEntity blockEntity, Cluster cluster) {
        var blockVPos = cluster.vUnit == UnitVec3i.X ? blockEntity.worldPosition.getX() :
                blockEntity.worldPosition.getZ();
        var horizontalFacing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        // Determine if a V axis points right when a player is looking at a cluster's front.
        var doesVAxisPointRight = horizontalFacing == Direction.SOUTH || horizontalFacing == Direction.WEST;
        if (doesVAxisPointRight) {
            var clusterStartVPos = cluster.vUnit == UnitVec3i.X ? cluster.startPos.getX() : cluster.startPos.getZ();
            return (blockVPos - clusterStartVPos) * GRID_WIDTH;
        }
        else {
            var clusterEndVPos = cluster.vUnit == UnitVec3i.X ? cluster.endPos.getX() : cluster.endPos.getZ();
            return (clusterEndVPos - blockVPos) * GRID_WIDTH;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        pTag.put(INVENTORY_SAVE_KEY, itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }
}