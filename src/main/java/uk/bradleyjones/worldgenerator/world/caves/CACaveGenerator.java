package uk.bradleyjones.worldgenerator.world.caves;

import java.util.Random;

public class CACaveGenerator implements CaveGenerator {

    private final boolean[][] caveGrid;
    private final int width;
    private final int height;

    // Tunable parameters
    private final int fillPercent;
    private final int iterations;
    private final int neighbourThreshold;

    public CACaveGenerator(int width, int height, long seed, int fillPercent, int iterations, int neighbourThreshold) {
        this.width = width;
        this.height = height;
        this.fillPercent = fillPercent;
        this.iterations = iterations;
        this.neighbourThreshold = neighbourThreshold;
        this.caveGrid = new boolean[width][height];
        generate(seed);
    }

    private void generate(long seed) {
        Random random = new Random(seed);

        // Step 1 - randomly fill grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                caveGrid[x][y] = random.nextInt(100) < fillPercent;
            }
        }

        // Step 2 - run CA iterations
        for (int i = 0; i < iterations; i++) {
            applyRule();
        }
    }

    private void applyRule() {
        boolean[][] newGrid = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int stoneNeighbours = countStoneNeighbours(x, y);
                newGrid[x][y] = stoneNeighbours >= neighbourThreshold;
            }
        }

        // Copy new grid into caveGrid
        for (int x = 0; x < width; x++) {
            System.arraycopy(newGrid[x], 0, caveGrid[x], 0, height);
        }
    }

    private int countStoneNeighbours(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // skip self

                int nx = x + dx;
                int ny = y + dy;

                // Treat out of bounds as stone - this keeps cave edges solid
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                    count++;
                } else if (caveGrid[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public boolean isCave(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return !caveGrid[x][y]; // false in grid = air = cave
    }
}