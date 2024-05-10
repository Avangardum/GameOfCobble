package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, ConwaysGameOfCobblestoneMod.MOD_ID);

    public static final RegistryObject<Block> CONWAYS_GAME_OF_COBBLESTONE_BLOCK =
        registerBlock("conways_game_of_cobblestone_block", () -> new Block(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
        var blockRegistryObject = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties()));
        return blockRegistryObject;
    }
}
