package uk.bradleyjones.worldgenerator.world;

public class World {

    // Parameters to be controlled by the user
    private int seed = 3432433;
    public TerrainHeightGenerator terrainHeightGenerator = new TerrainHeightGenerator(seed);

    private int waterLevel = 15;

    public TileType getTile(int x, int y) {
        int surfaceY = terrainHeightGenerator.getHeight(x);

        if (y < surfaceY) {
            return y > waterLevel ? TileType.WATER : TileType.AIR;
        }

        int depth = y - surfaceY;

        if (depth == 0) return surfaceY > waterLevel ? TileType.SAND : TileType.GRASS;
        if (depth <= 4) return surfaceY > waterLevel ? TileType.SAND : TileType.DIRT;
        return TileType.STONE;
    }

    //Getters

    public int getSeed() {
        return seed;
    }

    //Setters

    public void setSeed(int seed) {
        this.seed = seed;
        this.terrainHeightGenerator = new TerrainHeightGenerator(seed);
    }
}
