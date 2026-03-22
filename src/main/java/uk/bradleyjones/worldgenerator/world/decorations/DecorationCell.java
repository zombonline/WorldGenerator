package uk.bradleyjones.worldgenerator.world.decorations;

import uk.bradleyjones.worldgenerator.world.TileType;

public class DecorationCell {
    public final int dx, dy;
    public final TileType tileType;

    public DecorationCell(int dx, int dy, TileType tileType) {
        this.dx = dx;
        this.dy = dy;
        this.tileType = tileType;
    }
}
