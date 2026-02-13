package uk.bradleyjones.worldgenerator.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.World;

public class WorldRenderer {

    private static final int TILE_SIZE = 8;


    public void render(GraphicsContext gc, World world, Camera camera, double canvasWidth, double canvasHeight) {
        double scaledTileSize = TILE_SIZE * camera.getZoom();

        double topLeftX = camera.getX() - (canvasWidth / 2) / scaledTileSize;
        double topLeftY = camera.getY() - (canvasHeight / 2) / scaledTileSize;

        int tilesX = (int) Math.ceil(canvasWidth / scaledTileSize);
        int tilesY = (int) Math.ceil(canvasHeight / scaledTileSize);

        int baseTileX = (int) Math.floor(topLeftX);
        int baseTileY = (int) Math.floor(topLeftY);

        double offsetX = -(topLeftX - baseTileX) * scaledTileSize;
        double offsetY = -(topLeftY - baseTileY) * scaledTileSize;

        for (int y = 0; y < tilesY + 1; y++) {
            for (int x = 0; x < tilesX + 1; x++) {
                int worldX = baseTileX + x;
                int worldY = baseTileY + y;

                TileType tile = world.getTile(worldX, worldY);

                gc.setFill(colorFor(tile));
                gc.fillRect(
                        x * scaledTileSize + offsetX,
                        y * scaledTileSize + offsetY,
                        scaledTileSize,
                        scaledTileSize
                );
            }
        }
    }

    private Color colorFor(TileType tile) {
        return switch (tile) {
            case GRASS -> Color.FORESTGREEN;
            case SAND -> Color.SANDYBROWN;
            case WATER -> Color.DEEPSKYBLUE;
            case STONE -> Color.GRAY;
            case AIR -> Color.LIGHTBLUE;
        };
    }
}
