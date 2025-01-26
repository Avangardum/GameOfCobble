package net.avangardum.gameofcobble;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = GameOfCobbleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
final class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_CLUSTER_SIZE = BUILDER
            .comment("Maximum amount of Game of Cobble blocks that can form a cluster. " +
                    "Clusters of a bigger size won't work.")
            .defineInRange("maxClusterSize", 1000, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static int maxClusterSize;

    public static int getMaxClusterSize() {
        return maxClusterSize;
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event)
    {
        maxClusterSize = MAX_CLUSTER_SIZE.get();
    }

    private static boolean validateItemNames(@Nullable Object value) {
        if (!(value instanceof List<?> names)) return false;
        return names.stream().allMatch(nameObj -> nameObj instanceof String nameStr && getItemByName(nameStr) != null);
    }

    private static @Nullable Item getItemByName(@NotNull String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
    }
}
