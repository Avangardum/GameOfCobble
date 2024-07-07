package net.avangardum.conways_game_of_cobblestone;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ConwaysGameOfCobblestoneBlockEntity extends BlockEntity implements MenuProvider {
    public static final int GRID_HEIGHT = 10;
    public static final int GRID_WIDTH = 10;
    private static final int GRID_SIZE = GRID_HEIGHT * GRID_WIDTH;

    private final String INVENTORY_SAVE_KEY = "inventory";

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

    public ConwaysGameOfCobblestoneBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(ModBlockEntityTypes.CONWAYS_GAME_OF_COBBLESTONE_BET.get(), blockPos, blockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Conway's Game of Cobblestone");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory,
            @NotNull Player player) {
        return new ConwaysGameOfCobblestoneMenu(containerId, playerInventory, this);
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

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        pTag.put(INVENTORY_SAVE_KEY, itemHandler.serializeNBT());
        super.saveAdditional(pTag);
    }
}