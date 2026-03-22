package uk.bradleyjones.worldgenerator.render;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.World;

public class WorldRenderer {

    private WritableImage worldImage;
    private int imageWidth;
    private int imageHeight;
    private static final double IMAGE_CACHE_ZOOM_THRESHOLD = .5;
    private static final int TILE_SIZE = 8;

    public void buildWorldImageAsync(World world) {
        worldImage = null; // clear old image so tile renderer takes over

        Task<WritableImage> task = new Task<>() {
            @Override
            protected WritableImage call() {
                int w = world.worldConfig.width;
                int h = world.worldConfig.height;
                WritableImage img = new WritableImage(w, h);
                PixelWriter pw = img.getPixelWriter();
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        TileType tile = world.getTile(x, y);
                        Color colour = colorFor(tile, world, x, y);
                        pw.setColor(x, y, colour);
                    }
                }
                return img;
            }
        };

        task.setOnSucceeded(e -> {
            worldImage = task.getValue();
            imageWidth = world.worldConfig.width;
            imageHeight = world.worldConfig.height;
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void render(GraphicsContext gc, World world, Camera camera, double canvasWidth, double canvasHeight) {
        if (worldImage != null && camera.getZoom() < IMAGE_CACHE_ZOOM_THRESHOLD) {
            renderFromImage(gc, camera, canvasWidth, canvasHeight);
        } else {
            renderTiles(gc, world, camera, canvasWidth, canvasHeight);
        }
    }

    public void renderTiles(GraphicsContext gc, World world, Camera camera, double canvasWidth, double canvasHeight) {
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

                if (worldX < 0 || worldX >= world.worldConfig.width || worldY < 0 || worldY >= world.worldConfig.height) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(
                            x * scaledTileSize + offsetX,
                            y * scaledTileSize + offsetY,
                            scaledTileSize,
                            scaledTileSize
                    );
                    continue;
                }

                TileType tile = world.getTile(worldX, worldY);
                gc.setFill(colorFor(tile, world, worldX, worldY));
                gc.fillRect(
                        Math.floor(x * scaledTileSize + offsetX),
                        Math.floor(y * scaledTileSize + offsetY),
                        Math.ceil(scaledTileSize),
                        Math.ceil(scaledTileSize)
                );
            }
        }
    }

    public void renderFromImage(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        if (worldImage == null) return;

        // Fill background black for out of bounds areas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double scaledTileSize = camera.getZoom();

        // Camera position in tile space
        double camX = camera.getX();
        double camY = camera.getY();

        // Visible region in tile space
        double visibleTilesX = canvasWidth / (scaledTileSize * 8);
        double visibleTilesY = canvasHeight / (scaledTileSize * 8);

        double srcX = camX - visibleTilesX / 2;
        double srcY = camY - visibleTilesY / 2;
        double srcW = visibleTilesX;
        double srcH = visibleTilesY;

        // Clamp source rect to image bounds
        double clampedSrcX = Math.max(0, srcX);
        double clampedSrcY = Math.max(0, srcY);
        double clampedSrcW = Math.min(srcW, imageWidth - clampedSrcX);
        double clampedSrcH = Math.min(srcH, imageHeight - clampedSrcY);

        // Adjust destination to account for clamping
        double dstX = (clampedSrcX - srcX) * scaledTileSize * 8;
        double dstY = (clampedSrcY - srcY) * scaledTileSize * 8;
        double dstW = clampedSrcW * scaledTileSize * 8;
        double dstH = clampedSrcH * scaledTileSize * 8;

        gc.drawImage(worldImage,
                clampedSrcX, clampedSrcY, clampedSrcW, clampedSrcH,
                dstX, dstY, dstW, dstH);
    }

    private Color colorFor(TileType tile, World world, int x, int y) {
        return switch (tile) {
            case GRASS -> Color.FORESTGREEN;
            case SAND -> Color.SANDYBROWN;
            case WATER -> Color.DEEPSKYBLUE;
            case STONE -> Color.GRAY;
            case AIR -> Color.LIGHTBLUE.interpolate(Color.BLACK, world.getExposedLevel(x, y));
            case DIRT -> Color.BROWN;
            case GRAVEL -> Color.LIGHTGRAY;
            case SNOW -> Color.WHITE;
            default -> Color.HOTPINK;
        };
    }
}