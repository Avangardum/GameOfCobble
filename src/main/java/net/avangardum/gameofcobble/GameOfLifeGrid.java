package net.avangardum.gameofcobble;

final class GameOfLifeGrid {
    private final int width;
    private final int height;
    private final Boolean[] flatCells;

    public GameOfLifeGrid(int height, int width) {
        this.height = height;
        this.width = width;
        this.flatCells = new Boolean[height * width];
        for (var row = 0; row < height; row++) {
            for (var column = 0; column < width; column++) {
                setIsCellLiving(row, column, false);
            }
        }
    }

    public GameOfLifeGrid(int height, int width, Boolean[] flatCells) {
        assert height * width == flatCells.length;
        this.height = height;
        this.width = width;
        this.flatCells = flatCells.clone();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isCellLiving(int row, int column) {
        if (!areValidCoordinates(row, column)) return false;
        var flatIndex = getFlatIndex(row, column);
        return flatCells[flatIndex];
    }

    public void setIsCellLiving(int row, int column, boolean isLiving) {
        if (!areValidCoordinates(row, column))
            throw new IllegalArgumentException(String.format("Invalid coordinates (%s;%s).", row, column));
        var flatIndex = getFlatIndex(row, column);
        flatCells[flatIndex] = isLiving;
    }

    public void proceedToNextGeneration() {
        var previousGrid = copy();
        for (var row = 0; row < height; row++) {
            for (var column = 0; column < width; column++) {
                var previousLivingNeighborCount = previousGrid.countLivingNeighbors(row, column);
                var previousIsCellLiving = previousGrid.isCellLiving(row, column);
                var nextIsCellLiving = previousLivingNeighborCount == 3 ||
                        (previousLivingNeighborCount == 2 && previousIsCellLiving);
                setIsCellLiving(row, column, nextIsCellLiving);
            }
        }
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder("GameOfLifeGrid\n");
        for (var row = 0; row < height; row++) {
            for (var column = 0; column < width; column++){
                stringBuilder.append(isCellLiving(row, column) ? "*" : ".");
            }
            if (row < height - 1) stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private int countLivingNeighbors(int row, int column) {
        var count = 0;
        for (var neighborRow = row - 1; neighborRow <= row + 1; neighborRow++) {
            for (var neighborColumn = column - 1; neighborColumn <= column + 1; neighborColumn++) {
                if (neighborRow == row && neighborColumn == column) continue;
                if (isCellLiving(neighborRow, neighborColumn)) count++;
            }
        }
        return count;
    }

    private boolean areValidCoordinates(int row, int column) {
        return row >= 0 && row < height && column >= 0 && column < width;
    }

    private int getFlatIndex(int row, int column) {
        return row * width + column;
    }

    private GameOfLifeGrid copy() {
        return new GameOfLifeGrid(height, width, flatCells);
    }
}
