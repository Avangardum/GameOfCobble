package net.avangardum.gameofcobble;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

final class GameOfCobbleMenu extends AbstractContainerMenu {
    private static final int SLOT_SIZE = 18;
    private static final int INVENTORY_ROW_COUNT = 3;
    private static final int INVENTORY_COLUMN_COUNT = 9;
    private static final int HOTBAR_FIRST_SLOT_INDEX = 0;
    private static final int HOTBAR_LAST_SLOT_INDEX = HOTBAR_FIRST_SLOT_INDEX + INVENTORY_COLUMN_COUNT - 1;
    private static final int INVENTORY_FIRST_SLOT_INDEX = HOTBAR_LAST_SLOT_INDEX + 1;
    private static final int INVENTORY_LAST_SLOT_INDEX =
            INVENTORY_FIRST_SLOT_INDEX + INVENTORY_ROW_COUNT * INVENTORY_COLUMN_COUNT - 1;
    private static final int GAME_OF_COBBLE_FIRST_SLOT_INDEX = INVENTORY_LAST_SLOT_INDEX + 1;
    private static final int GAME_OF_COBBLE_LAST_SLOT_INDEX =
            GAME_OF_COBBLE_FIRST_SLOT_INDEX + GameOfCobbleBlockEntity.GRID_AREA - 1;

    private final GameOfCobbleBlockEntity blockEntity;
    private final Level level;

    GameOfCobbleMenu(int containerId, @NotNull Inventory playerInventory,
                     @NotNull GameOfCobbleBlockEntity blockEntity) {
        super(ModMenuTypes.GAME_OF_COBBLE_MENU_TYPE.get(), containerId);
        this.blockEntity = blockEntity;
        level = playerInventory.player.level();
        addPlayerHotbarSlots(playerInventory);
        addPlayerInventorySlots(playerInventory);
        addGameOfCobbleSlots();
    }

    GameOfCobbleMenu(int containerId, @NotNull Inventory inventory, @NotNull FriendlyByteBuf extraData) {
        this(containerId, inventory, getBlockEntity(inventory, extraData.readBlockPos()));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (!(GAME_OF_COBBLE_FIRST_SLOT_INDEX <= index &&
                index <= GAME_OF_COBBLE_LAST_SLOT_INDEX))
            return ItemStack.EMPTY;

        var slot = slots.get(index);
        var stack = slot.getItem();
        var stackCopy = stack.copy();
        var isSuccessful = moveItemStackTo(stack, HOTBAR_FIRST_SLOT_INDEX, INVENTORY_LAST_SLOT_INDEX + 1, false);
        return isSuccessful ? stackCopy : ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player,
                ModBlocks.GAME_OF_COBBLE_BLOCK.get());
    }

    private void addPlayerHotbarSlots(Inventory playerInventory) {
        var startX = 8;
        var y = 214;
        for (var index = 0; index < INVENTORY_COLUMN_COUNT; index++) {
            var x = startX + index * SLOT_SIZE;
            addSlot(new Slot(playerInventory, index, x, y));
        }
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        var startX = 8;
        var startY = 156;
        for (var row = 0; row < INVENTORY_ROW_COUNT; row++) {
            for (var column = 0; column < INVENTORY_COLUMN_COUNT; column++) {
                var x = startX + column * SLOT_SIZE;
                var y = startY + row * SLOT_SIZE;
                var slotIndex = (1 + row) * INVENTORY_COLUMN_COUNT + column;
                addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    private void addGameOfCobbleSlots() {
        var itemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new RuntimeException("Item handler does not exist"));
        var startX = 17;
        var startY = 8;
        var height = GameOfCobbleBlockEntity.GRID_SIDE;
        var width = GameOfCobbleBlockEntity.GRID_SIDE;
        for (var row = 0; row < height; row++) {
            for (var column = 0; column < width; column++) {
                var index = row * width + column;
                var x = startX + column * SLOT_SIZE;
                var y = startY + row * SLOT_SIZE;
                addSlot(new SlotItemHandler(itemHandler, index, x, y));
            }
        }
    }

    private static @NotNull GameOfCobbleBlockEntity getBlockEntity(
            @NotNull Inventory inventory, @NotNull BlockPos blockPos) {
        var blockEntity = (GameOfCobbleBlockEntity) inventory.player.level().getBlockEntity(blockPos);
        assert blockEntity != null;
        return blockEntity;
    }
}
