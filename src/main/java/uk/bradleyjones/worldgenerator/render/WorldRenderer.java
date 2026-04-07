package uk.bradleyjones.worldgenerator.render;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.world.GenerationPassType;
import uk.bradleyjones.worldgenerator.world.GenerationPassTypeSets;
import uk.bradleyjones.worldgenerator.world.TileType;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class WorldRenderer {

    private Map<TileType, Image> tileTypeImageMap = new HashMap<>();
    private WritableImage worldImage;
    private int imageWidth;
    private int imageHeight;
    private static final double IMAGE_CACHE_ZOOM_THRESHOLD = .5;
    private static final int TILE_SIZE = 8;

    public void loadImageMap() {
        TileType[] tileTypes = TileType.values();

        for (TileType tileType : tileTypes) {
            String name = "tile_" + tileType.toString().toLowerCase() + ".png";
            String path = "uk/bradleyjones/worldgenerator/images/" + name;

            try (InputStream stream = WorldRenderer.class
                    .getClassLoader()
                    .getResourceAsStream(path)) {

                if (stream == null) {
                    System.err.println("Not found: " + path);
                    continue;
                }

                Image image = new Image(stream);
                tileTypeImageMap.put(tileType, image);
                System.out.println("Loaded: " + name);

            } catch (Exception e) {
                System.err.println("Error loading: " + name);
                e.printStackTrace();
            }
        }
    }

    public void buildWorldImageAsync() {
        worldImage = null; // clear old image so tile renderer takes over

        Task<WritableImage> task = new Task<>() {
            @Override
            protected WritableImage call() {
                int w = world.getWorldConfig().width;
                int h = world.getWorldConfig().height;
                WritableImage img = new WritableImage(w, h);
                PixelWriter pw = img.getPixelWriter();
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        TileType tile = world.getTile(x, y, GenerationPassTypeSets.ALL);
                        Color colour = colorFor(tile, x, y);
                        pw.setColor(x, y, colour);
                    }
                }
                return img;
            }
        };

        task.setOnSucceeded(e -> {
            worldImage = task.getValue();
            imageWidth = world.getWorldConfig().width;
            imageHeight = world.getWorldConfig().height;
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void render(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        if (worldImage != null && camera.getZoom() < IMAGE_CACHE_ZOOM_THRESHOLD) {
            renderFromImage(gc, camera, canvasWidth, canvasHeight);
        } else {
            renderTiles(gc, camera, canvasWidth, canvasHeight);
        }
    }

    public void renderTiles(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
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

                if (worldX < 0 || worldX >= world.getWorldConfig().width || worldY < 0 || worldY >= world.getWorldConfig().height) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(
                            x * scaledTileSize + offsetX,
                            y * scaledTileSize + offsetY,
                            scaledTileSize,
                            scaledTileSize
                    );
                    continue;
                }

                TileType tile = world.getTile(worldX, worldY, GenerationPassTypeSets.ALL);
                double modifiedX = Math.floor(x * scaledTileSize + offsetX);
                double modifiedY = Math.floor(y * scaledTileSize + offsetY);
                placeTileImageAndColor(gc, tile, worldX, worldY, modifiedX, modifiedY, scaledTileSize);
            }
        }
    }

    private void placeTileImageAndColor(GraphicsContext gc, TileType tile, int worldX, int worldY, double modifiedX, double modifiedY, double scaledTileSize) {
        if(tileTypeImageMap.containsKey(tile)) {
            gc.setFill(colorFor(world.getTile(worldX,worldY, GenerationPassTypeSets.ALL), worldX, worldY));
            gc.fillRect(
                    modifiedX,
                    modifiedY,
                    Math.ceil(scaledTileSize),
                    Math.ceil(scaledTileSize)
            );
            gc.drawImage(tileTypeImageMap.get(tile), modifiedX, modifiedY, Math.ceil(scaledTileSize), Math.ceil(scaledTileSize));
        }
        else {
            gc.setFill(colorFor(tile, worldX, worldY));
            gc.fillRect(
                    modifiedX,
                    modifiedY,
                    Math.ceil(scaledTileSize),
                    Math.ceil(scaledTileSize)
            );
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


    private Color colorFor(TileType tile, int x, int y) {
        return switch (tile) {
            case GRASS -> Color.FORESTGREEN;
            case SAND -> Color.SANDYBROWN;
            case WATER -> Color.MIDNIGHTBLUE.interpolate(
                    world.getExposedLevel(x, y).color.interpolate(Color.DEEPSKYBLUE, 0.5),
                    world.getExposedLevel(x, y).normalizedIntensity
            );
            case STONE -> Color.GRAY;
            case AIR -> Color.BLACK.interpolate(world.getExposedLevel(x,y).color, world.getExposedLevel(x, y).normalizedIntensity);
            case DIRT -> Color.BROWN;
            case GRAVEL -> Color.LIGHTGRAY;
            case SNOW -> Color.WHITE;
            case LEAVES -> Color.DARKGREEN;
            case LOG -> Color.SADDLEBROWN;
            case CACTUS -> Color.LIMEGREEN;
            case COAL_ORE -> Color.DARKSLATEGRAY;
            case DIAMOND_ORE -> colorFor(TileType.STONE,x,y);
            case COPPER_ORE -> Color.rgb(184, 115, 51);   // warm orange-brown
            case LAPIS_ORE  -> Color.rgb(30, 60, 180);    // deep royal blue
            case AMETHYST_ORE -> Color.rgb(153, 50, 204); // rich purple
            case RED_CLAY   -> Color.rgb(180, 80, 60);    // terracotta rust
            case QUARTZ     -> Color.rgb(220, 215, 210);  // off-white cream
            default -> Color.HOTPINK;
        };
    }
}