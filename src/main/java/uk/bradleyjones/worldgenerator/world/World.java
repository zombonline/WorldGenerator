package uk.bradleyjones.worldgenerator.world;

public class World {

    private int width = 1000;
    private int height = 400;
    private int seed = 3432433;
    private int waterLevel = 80;

    public TerrainHeightGenerator terrainHeightGenerator = new TerrainHeightGenerator(seed, height);

    public TileType getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return TileType.AIR;
        }

        int surfaceY = terrainHeightGenerator.getHeight(x);
        int clampedWaterLevel = Math.min(waterLevel, height - 1);

        if (y < surfaceY) {
            return y > clampedWaterLevel ? TileType.WATER : TileType.AIR;
        }

        int depth = y - surfaceY;

        if (depth == 0) return surfaceY > clampedWaterLevel ? TileType.SAND : TileType.GRASS;
        if (depth <= 4) return surfaceY > clampedWaterLevel ? TileType.SAND : TileType.DIRT;
        return TileType.STONE;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSeed() { return seed; }
    public int getWaterLevel() { return waterLevel; }

    public void setWidth(int width) { this.width = width; }

    public void setHeight(int height) {
        this.height = height;
        terrainHeightGenerator.setWorldHeight(height);
    }

    public void setWaterLevel(int waterLevel) { this.waterLevel = waterLevel; }

    public void setSeed(int seed) {
        this.seed = seed;
        this.terrainHeightGenerator = new TerrainHeightGenerator(seed, height);
    }
}