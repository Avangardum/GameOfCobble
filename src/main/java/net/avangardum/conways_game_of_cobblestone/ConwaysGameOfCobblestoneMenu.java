package net.avangardum.conways_game_of_cobblestone;

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
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public class ConwaysGameOfCobblestoneMenu extends AbstractContainerMenu {
    private final int SLOT_SIZE = 18;

    private final ConwaysGameOfCobblestoneBlockEntity blockEntity;
    private final Level level;

    protected ConwaysGameOfCobblestoneMenu(int containerId, Inventory playerInventory,
            ConwaysGameOfCobblestoneBlockEntity blockEntity) {
        super(ModMenuTypes.CONWAYS_GAME_OF_COBBLESTONE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        level = playerInventory.player.level();
        addPlayerHotBarSlots(playerInventory);
        addBlockEntitySlots();
    }

    protected ConwaysGameOfCobblestoneMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory,
            (ConwaysGameOfCobblestoneBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player,
            ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get());
    }

    private void addPlayerHotBarSlots(Inventory playerInventory) {
        var slotCount = 9;
        var startX = 17;
        var y = 192;
        for (var i = 0; i < slotCount; i++) {
            int x = startX + i * SLOT_SIZE;
            addSlot(new Slot(playerInventory, i, x, y));
        }
    }

    private void addBlockEntitySlots() {
        var itemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
            .orElseThrow(() -> new RuntimeException("Item handler does not exist"));
        var startX = 8;
        var startY = 8;
        var height = ConwaysGameOfCobblestoneBlockEntity.GRID_HEIGHT;
        var width = ConwaysGameOfCobblestoneBlockEntity.GRID_WIDTH;
        for (var row = 0; row < height; row++) {
            for (var column = 0; column < width; column++) {
                var index = row * width + column;
                var x = startX + column * SLOT_SIZE;
                var y = startY + row * SLOT_SIZE;
                addSlot(new SlotItemHandler(itemHandler, index, x, y));
            }
        }
    }
}
