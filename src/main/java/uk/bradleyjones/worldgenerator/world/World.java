package uk.bradleyjones.worldgenerator.world;

public class World {

    // Parameters to be controlled by the user
    private int seed = 3432433;
    public TerrainHeightGenerator terrainHeightGenerator = new TerrainHeightGenerator(seed);

    private int groundHeight = 3;

    public TileType getTile(int x, int y) {
        if(y < terrainHeightGenerator.getHeight(x)) {
            return TileType.AIR;
        } else {
            return pseudoRandom(x,y) < 0.5 ? TileType.SAND : TileType.GRASS;
        }
    }

    private double pseudoRandom(int x, int y) {
        long hash = x * 31L + y * 17L + seed;
        hash = (hash ^ (hash >> 13)) * 0x5DEECE66DL;
        return ((hash & 0xFFFFFFFFL) / (double) 0x100000000L);
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
