package net.avangardum.gameofcobble;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

final class GameOfCobbleCluster {
    // Read the top comment in GameOfCobbleBlockEntity for explanation.

    public record Errors(boolean mixedItems, boolean illegalItem, boolean tooBig) {
        public static final Errors NONE = new Errors(false, false, false);

        public boolean hasAny() {
            return mixedItems || illegalItem || tooBig;
        }

        public @NotNull Errors withMixedItems() {
            return new Errors(true, illegalItem, tooBig);
        }

        public @NotNull Errors withIllegalItem() {
            return new Errors(mixedItems, true, tooBig);
        }

        public @NotNull Errors withTooBig() {
            return new Errors(mixedItems, illegalItem, true);
        }
    }

    private final Set<GameOfCobbleBlockEntity> blockEntities;
    private final GameOfCobbleBlockEntity calculator;
    private final BlockPos startPos;
    private final BlockPos endPos;
    private final Item item;
    private final Errors errors;

    public GameOfCobbleCluster(
            @NotNull Set<GameOfCobbleBlockEntity> blockEntities,
            @NotNull BlockPos startPos,
            @NotNull BlockPos endPos,
            @Nullable Item item,
            @NotNull GameOfCobbleCluster.Errors errors
    ) {
        this.blockEntities = blockEntities;
        this.startPos = startPos;
        this.endPos = endPos;
        this.item = item;
        this.errors = errors;

        var optionalCalculator = blockEntities.stream().findFirst();
        assert optionalCalculator.isPresent();
        this.calculator = optionalCalculator.get();
    }

    public @NotNull Set<GameOfCobbleBlockEntity> getBlockEntities() {
        return blockEntities;
    }

    public int getStartX() {
        return startPos.getX();
    }

    public int getStartY() {
        return startPos.getY();
    }

    public int getStartZ() {
        return startPos.getZ();
    }

    public int getStartV() {
        return calculator.getVFromXZ(getStartX(), getStartZ());
    }

    public int getEndX() {
        return endPos.getX();
    }

    public int getEndY() {
        return endPos.getY();
    }

    public int getEndZ() {
        return endPos.getZ();
    }

    public int getEndV() {
        return calculator.getVFromXZ(getEndX(), getEndZ());
    }

    public @Nullable Item getItem() {
        return item;
    }

    public @NotNull GameOfCobbleCluster.Errors getErrors() {
        return errors;
    }
}
