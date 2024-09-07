package net.avangardum.gameofcobble;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ModBlocks {
    public static final @NotNull DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GameOfCobbleMod.MOD_ID);

    public static final @NotNull RegistryObject<Block> GAME_OF_COBBLE_BLOCK =
            registerBlock("game_of_cobble_block", () -> new GameOfCobbleBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static void register(@NotNull IEventBus bus){
        BLOCKS.register(bus);
    }

    private static <T extends Block> @NotNull RegistryObject<T> registerBlock(@NotNull String name,
            @NotNull Supplier<T> blockSupplier) {
        var blockRegistryObject = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties()));
        return blockRegistryObject;
    }
}
