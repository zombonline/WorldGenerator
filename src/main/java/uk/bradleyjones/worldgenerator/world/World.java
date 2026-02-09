package uk.bradleyjones.worldgenerator.world;

public class World {

    /**
     * Returns the tile at world coordinates (x, y)
     * Rule: (x + y) % 2 == 0 → BLUE, otherwise → GREEN
     */
    public TileType getTile(int x, int y) {
        if ((x + y) % 2 == 0) {
            return TileType.SAND;
        } else {
            return TileType.GRASS;
        }
    }
}
