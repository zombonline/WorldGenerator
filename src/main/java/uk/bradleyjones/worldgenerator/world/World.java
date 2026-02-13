package uk.bradleyjones.worldgenerator.world;

public class World {

    // Parameters to be controlled by the user
    private int seed = 3432433;
    private int groundHeight = 3;

    public TileType getTile(int x, int y) {
        var random = new java.util.Random(seed + x * 31L + y * 17L);
        if(y < groundHeight) {
            return TileType.AIR;
        } else {
            return random.nextDouble() < 0.5 ? TileType.SAND : TileType.GRASS;
        }
    }
}
