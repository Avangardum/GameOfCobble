package net.avangardum.gameofcobble;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

final class ModBlockEntityTypes {
    public static final @NotNull DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ConwaysGameOfCobblestoneMod.MOD_ID);

    // Suppress the nullability warning caused by passing null to build. Forge documentation allows passing null there.
    @SuppressWarnings("DataFlowIssue")
    public static final @NotNull RegistryObject<BlockEntityType<ConwaysGameOfCobblestoneBlockEntity>>
            CONWAYS_GAME_OF_COBBLESTONE_BET =
            BLOCK_ENTITY_TYPES.register("conways_game_of_cobblestone_bet", () ->
            BlockEntityType.Builder.of(ConwaysGameOfCobblestoneBlockEntity::new,
            ModBlocks.CONWAYS_GAME_OF_COBBLESTONE_BLOCK.get()).build(null));

    public static void register(@NotNull IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
