package net.avangardum.conways_game_of_cobblestone;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ConwaysGameOfCobblestoneMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ConwaysGameOfCobblestoneBlockEntity>>
        CONWAYS_GAME_OF_COBBLESTONE_BET =
        BLOCK_ENTITY_TYPES.register("conways_game_of_cobblestone_bet", () ->
        BlockEntityType.Builder.of(ConwaysGameOfCobblestoneBlockEntity::new,
        ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
