package uk.bradleyjones.worldgenerator.render;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
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
    private Map<TileType, Color[][]> spritePixelCache = new HashMap<>();

    private Map<Long, WritableImage> chunkImages = new HashMap<>();
    private static final int CHUNK_SIZE = 128;

    public void loadImageMap() {
        TileType[] tileTypes = TileType.values();

        for (TileType tileType : tileTypes) {
            String name = "tile_" + tileType.toString().toLowerCase() + ".png";
            String path = "uk/bradleyjones/worldgenerator/images/" + name;

            try (InputStream stream = WorldRenderer.class
                    .getClassLoader()
                    .getResourceAsStream(path)) {

                if (stream == null) {
//                    System.err.println("Not found: " + path);
                    continue;
                }

                Image image = new Image(stream);
                tileTypeImageMap.put(tileType, image);
                PixelReader pr = image.getPixelReader();
                int w = (int) image.getWidth();
                int h = (int) image.getHeight();
                Color[][] pixels = new Color[w][h];
                for (int py = 0; py < h; py++) {
                    for (int px = 0; px < w; px++) {
                        pixels[px][py] = pr.getColor(px, py);
                    }
                }
                spritePixelCache.put(tileType, pixels);
//                System.out.println("Loaded: " + name);

            } catch (Exception e) {
                System.err.println("Error loading: " + name);
                e.printStackTrace();
            }

        }

    }

    private long chunkKey(int cx, int cy) {
        return ((long) cx << 32) | (cy & 0xFFFFFFFFL);
    }
    private void blendImageIntoChunk(PixelWriter pw, Color[][] sprite, int spriteW, int spriteH, int tileX, int tileY) {
        int size = TILE_SIZE;
        for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
                int srcX = (int) ((px / (double) size) * spriteW);
                int srcY = (int) ((py / (double) size) * spriteH);
                Color c = sprite[srcX][srcY];
                if (c.getOpacity() > 0.01) {
                    pw.setColor(tileX * size + px, tileY * size + py, c);
                }
            }
        }
    }
    public void buildChunkImagesAsync() {
        chunkImages.clear();

        int w = world.getWorldConfig().width;
        int h = world.getWorldConfig().height;
        int chunksX = (int) Math.ceil((double) w / CHUNK_SIZE);
        int chunksY = (int) Math.ceil((double) h / CHUNK_SIZE);

        for (int cy = 0; cy < chunksY; cy++) {
            for (int cx = 0; cx < chunksX; cx++) {
                final int fcx = cx;
                final int fcy = cy;

                Task<WritableImage> task = new Task<>() {
                    @Override
                    protected WritableImage call() {
                        int startX = fcx * CHUNK_SIZE;
                        int startY = fcy * CHUNK_SIZE;
                        int endX = Math.min(startX + CHUNK_SIZE, w);
                        int endY = Math.min(startY + CHUNK_SIZE, h);
                        int chunkW = (endX - startX) * TILE_SIZE;
                        int chunkH = (endY - startY) * TILE_SIZE;

                        WritableImage img = new WritableImage(chunkW, chunkH);
                        PixelWriter pw = img.getPixelWriter();
                        for (int y = 0; y < (endY - startY); y++) {
                            for (int x = 0; x < (endX - startX); x++) {
                                TileType tile = world.getTile(startX + x, startY + y, GenerationPassTypeSets.ALL);
                                Color colour = colorFor(tile, startX + x, startY + y);
                                for (int py = 0; py < TILE_SIZE; py++) {
                                    for (int px = 0; px < TILE_SIZE; px++) {
                                        pw.setColor(x * TILE_SIZE + px, y * TILE_SIZE + py, colour);
                                    }
                                }
                                Color[][] sprite = spritePixelCache.get(tile);
                                if (sprite != null) {
                                    blendImageIntoChunk(pw, sprite, sprite.length, sprite[0].length, x, y);
                                }
                            }
                        }
                        return img;
                    }
                };

                task.setOnSucceeded(e -> chunkImages.put(chunkKey(fcx, fcy), task.getValue()));

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        }
    }

    private void renderFromChunks(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        gc.setImageSmoothing(false);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double scale = camera.getZoom() * TILE_SIZE;
        double chunkScale = camera.getZoom();

        double topLeftWorldX = camera.getX() - (canvasWidth / 2.0) / scale;
        double topLeftWorldY = camera.getY() - (canvasHeight / 2.0) / scale;

        int minCX = (int) Math.floor(topLeftWorldX / CHUNK_SIZE);
        int minCY = (int) Math.floor(topLeftWorldY / CHUNK_SIZE);
        int maxCX = (int) Math.ceil((topLeftWorldX + canvasWidth / scale) / CHUNK_SIZE);
        int maxCY = (int) Math.ceil((topLeftWorldY + canvasHeight / scale) / CHUNK_SIZE);
        System.out.println("chunks: " + chunkImages.size() + " zoom: " + camera.getZoom() + " cx range: " + minCX + "-" + maxCX + " cy range: " + minCY + "-" + maxCY);

        for (int cy = minCY; cy <= maxCY; cy++) {
            for (int cx = minCX; cx <= maxCX; cx++) {
                WritableImage chunk = chunkImages.get(chunkKey(cx, cy));
                if (chunk == null) continue;

                double dstW = (chunk.getWidth() / TILE_SIZE) * scale;
                double dstH = (chunk.getHeight() / TILE_SIZE) * scale;
                double dstX = (cx * CHUNK_SIZE - topLeftWorldX) * scale;
                double dstY = (cy * CHUNK_SIZE - topLeftWorldY) * scale;
                System.out.println("chunk w: " + chunk.getWidth() + " dstW: " + dstW + " scale: " + scale + " chunkScale: " + chunkScale);

                gc.drawImage(chunk, dstX, dstY, dstW, dstH);
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

        buildChunkImagesAsync();
    }

    public void render(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        if (camera.getZoom() < IMAGE_CACHE_ZOOM_THRESHOLD) {
            if (worldImage != null) renderFromImage(gc, camera, canvasWidth, canvasHeight);
        } else {
            if (!chunkImages.isEmpty()) renderFromChunks(gc, camera, canvasWidth, canvasHeight);
            else renderTiles(gc, camera, canvasWidth, canvasHeight);
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
        TileType bg = tile.getBackground();
        if (bg != tile) return colorFor(bg, x, y);

        return switch (tile) {
            case GRASS -> Color.FORESTGREEN;
            case SAND -> Color.SANDYBROWN;
            case WATER -> Color.MIDNIGHTBLUE.interpolate(
                    world.getExposedLevel(x, y).color.interpolate(Color.DEEPSKYBLUE, 0.5),
                    world.getExposedLevel(x, y).normalizedIntensity
            );
            case STONE -> Color.GRAY;
            case AIR -> {
                if (world.getDepthOfPosition(x, y) < 0) {
                    yield world.getSkyColor(x, y);
                }
                yield Color.BLACK.interpolate(world.getExposedLevel(x, y).color, world.getExposedLevel(x, y).normalizedIntensity);
            }            case DIRT -> Color.BROWN;
            case GRAVEL -> Color.LIGHTGRAY;
            case SNOW -> Color.WHITE;
            case LEAVES -> Color.DARKGREEN;
            case LOG -> Color.SADDLEBROWN;
            case CACTUS -> Color.LIMEGREEN;
            case COAL_ORE -> Color.DARKSLATEGRAY;
            case COPPER_ORE -> Color.rgb(184, 115, 51);   // warm orange-brown
            case LAPIS_ORE  -> Color.rgb(30, 60, 180);    // deep royal blue
            case AMETHYST_ORE -> Color.rgb(153, 50, 204); // rich purple
            case RED_CLAY   -> Color.rgb(180, 80, 60);    // terracotta rust
            case QUARTZ     -> Color.rgb(220, 215, 210);  // off-white cream
            case BEDROCK -> Color.rgb(20, 18, 24);
            default -> Color.HOTPINK;
        };
    }
}