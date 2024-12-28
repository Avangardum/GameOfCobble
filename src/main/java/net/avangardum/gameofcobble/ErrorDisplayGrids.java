package net.avangardum.gameofcobble;

final class ErrorDisplayGrids {
    public static final GameOfLifeGrid TOO_BIG_CLUSTER =
            new GameOfLifeGrid(GameOfCobbleBlockEntity.GRID_SIDE, GameOfCobbleBlockEntity.GRID_SIDE, new Boolean[] {
                    true , true , true , false, false, false, false, false,
                    true , true , false, false, false, false, false, false,
                    true , false, true , false, false, false, false, false,
                    false, false, false, true , false, false, false, false,
                    false, false, false, false, true , false, false, false,
                    false, false, false, false, false, true , false, true ,
                    false, false, false, false, false, false, true , true ,
                    false, false, false, false, false, true , true , true
            });

    public static final GameOfLifeGrid MIXED_ITEMS =
            new GameOfLifeGrid(GameOfCobbleBlockEntity.GRID_SIDE, GameOfCobbleBlockEntity.GRID_SIDE, new Boolean[] {
                    true , false, false, false, false, true , true , true ,
                    false, true , false, false, false, false, true , true ,
                    false, false, true , false, false, true , false, true ,
                    false, false, false, true , true , false, false, false,
                    false, false, false, true , true , false, false, false,
                    false, false, true , false, false, true , false, true ,
                    false, true , false, false, false, false, true , true ,
                    true , false, false, false, false, true , true , true
            });

    public static final GameOfLifeGrid ILLEGAL_ITEM =
            new GameOfLifeGrid(GameOfCobbleBlockEntity.GRID_SIDE, GameOfCobbleBlockEntity.GRID_SIDE, new Boolean[] {
                    false, false, false, false, false, false, false, false,
                    false, false, true , true , true , true , false, false,
                    false, true , true , false, false, false, true , false,
                    false, true , false, true , false, false, true , false,
                    false, true , false, false, true , false, true , false,
                    false, true , false, false, false, true , true , false,
                    false, false, true , true , true , true , false, false,
                    false, false, false, false, false, false, false, false
            });
}
