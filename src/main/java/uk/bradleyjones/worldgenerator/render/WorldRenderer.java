package uk.bradleyjones.worldgenerator.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.World;

public class WorldRenderer {

    private static final int TILE_SIZE = 8;

    public void render(GraphicsContext gc, World world, double canvasWidth, double canvasHeight) {
        int tilesX = (int) Math.ceil(canvasWidth / TILE_SIZE);
        int tilesY = (int) Math.ceil(canvasHeight / TILE_SIZE);

        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
                TileType tile = world.getTile(x, y);

                gc.setFill(tile == TileType.GRASS ? Color.FORESTGREEN : Color.SANDYBROWN);
                gc.fillRect(
                        x * TILE_SIZE,
                        y * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        }
    }
}
