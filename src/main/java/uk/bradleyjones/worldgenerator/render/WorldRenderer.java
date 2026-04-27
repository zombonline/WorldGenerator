package uk.bradleyjones.worldgenerator.render;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.world.GenerationPassTypeSets;
import uk.bradleyjones.worldgenerator.world.TileType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class WorldRenderer {

    private static final double IMAGE_CACHE_ZOOM_THRESHOLD = 0.5;
    private static final int TILE_SIZE = 8;
    private static final int CHUNK_SIZE = 128;

    private final Map<TileType, Image> tileTypeImageMap = new HashMap<>();
    private final Map<TileType, Color[][]> spritePixelCache = new HashMap<>();
    private final Map<Long, WritableImage> chunkImages = new HashMap<>();

    private WritableImage worldImage;
    private int imageWidth;
    private int imageHeight;

    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public void loadImageMap() {
        for (TileType tileType : TileType.values()) {
            String name = "tile_" + tileType.toString().toLowerCase() + ".png";
            String path = "uk/bradleyjones/worldgenerator/images/" + name;
            try (InputStream stream = WorldRenderer.class.getClassLoader().getResourceAsStream(path)) {
                if (stream == null) continue;
                Image image = new Image(stream);
                tileTypeImageMap.put(tileType, image);
                spritePixelCache.put(tileType, cachePixels(image));
            } catch (Exception e) {
                System.err.println("Error loading: " + name);
            }
        }
    }

    private Color[][] cachePixels(Image image) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        Color[][] pixels = new Color[w][h];
        PixelReader pr = image.getPixelReader();
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                pixels[x][y] = pr.getColor(x, y);
        return pixels;
    }
    public void buildAsync(Runnable onComplete) {
        worldImage = null;
        chunkImages.clear();

        Task<WritableImage> task = new Task<>() {
            @Override
            protected WritableImage call() {
                int w = world.getWorldConfig().width;
                int h = world.getWorldConfig().height;
                WritableImage img = new WritableImage(w, h);
                PixelWriter pw = img.getPixelWriter();
                for (int y = 0; y < h; y++)
                    for (int x = 0; x < w; x++)
                        pw.setColor(x, y, colorFor(world.getTile(x, y, GenerationPassTypeSets.ALL), x, y));
                return img;
            }
        };

        task.setOnSucceeded(e -> {
            worldImage = task.getValue();
            imageWidth = world.getWorldConfig().width;
            imageHeight = world.getWorldConfig().height;
            if (onComplete != null) Platform.runLater(onComplete);
        });

        executor.submit(task);
        buildChunkImagesAsync();
    }


    private void buildChunkImagesAsync() {
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
                        return buildChunk(fcx, fcy, w, h);
                    }
                };
                task.setOnSucceeded(e -> chunkImages.put(chunkKey(fcx, fcy), task.getValue()));
                executor.submit(task);
            }
        }
    }

    private WritableImage buildChunk(int cx, int cy, int worldW, int worldH) {
        int startX = cx * CHUNK_SIZE;
        int startY = cy * CHUNK_SIZE;
        int endX = Math.min(startX + CHUNK_SIZE, worldW);
        int endY = Math.min(startY + CHUNK_SIZE, worldH);
        int chunkW = (endX - startX) * TILE_SIZE;
        int chunkH = (endY - startY) * TILE_SIZE;

        WritableImage img = new WritableImage(chunkW, chunkH);
        PixelWriter pw = img.getPixelWriter();

        for (int y = 0; y < (endY - startY); y++) {
            for (int x = 0; x < (endX - startX); x++) {
                TileType tile = world.getTile(startX + x, startY + y, GenerationPassTypeSets.ALL);
                Color colour = colorFor(tile, startX + x, startY + y);
                fillTile(pw, x, y, colour);
                Color[][] sprite = spritePixelCache.get(tile);
                if (sprite != null) blendSprite(pw, sprite, x, y);
            }
        }
        return img;
    }

    private void fillTile(PixelWriter pw, int tileX, int tileY, Color colour) {
        for (int py = 0; py < TILE_SIZE; py++)
            for (int px = 0; px < TILE_SIZE; px++)
                pw.setColor(tileX * TILE_SIZE + px, tileY * TILE_SIZE + py, colour);
    }

    private void blendSprite(PixelWriter pw, Color[][] sprite, int tileX, int tileY) {
        int spriteW = sprite.length;
        int spriteH = sprite[0].length;
        for (int py = 0; py < TILE_SIZE; py++) {
            for (int px = 0; px < TILE_SIZE; px++) {
                int srcX = (int) ((px / (double) TILE_SIZE) * spriteW);
                int srcY = (int) ((py / (double) TILE_SIZE) * spriteH);
                Color c = sprite[srcX][srcY];
                if (c.getOpacity() > 0.01)
                    pw.setColor(tileX * TILE_SIZE + px, tileY * TILE_SIZE + py, c);
            }
        }
    }

    public void render(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        if (camera.getZoom() < IMAGE_CACHE_ZOOM_THRESHOLD) {
            if (worldImage != null) renderFromImage(gc, camera, canvasWidth, canvasHeight);
        } else {
            if (!chunkImages.isEmpty()) renderFromChunks(gc, camera, canvasWidth, canvasHeight);
            else renderTiles(gc, camera, canvasWidth, canvasHeight);
        }
    }

    private void renderFromImage(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double zoom = camera.getZoom();
        double visibleW = canvasWidth / (zoom * TILE_SIZE);
        double visibleH = canvasHeight / (zoom * TILE_SIZE);
        double srcX = camera.getX() - visibleW / 2;
        double srcY = camera.getY() - visibleH / 2;

        double clampedSrcX = Math.max(0, srcX);
        double clampedSrcY = Math.max(0, srcY);
        double clampedSrcW = Math.min(visibleW, imageWidth - clampedSrcX);
        double clampedSrcH = Math.min(visibleH, imageHeight - clampedSrcY);

        double dstX = (clampedSrcX - srcX) * zoom * TILE_SIZE;
        double dstY = (clampedSrcY - srcY) * zoom * TILE_SIZE;

        gc.drawImage(worldImage,
                clampedSrcX, clampedSrcY, clampedSrcW, clampedSrcH,
                dstX, dstY, clampedSrcW * zoom * TILE_SIZE, clampedSrcH * zoom * TILE_SIZE);
    }

    private void renderFromChunks(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        gc.setImageSmoothing(false);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double scale = camera.getZoom() * TILE_SIZE;
        double topLeftX = camera.getX() - (canvasWidth / 2.0) / scale;
        double topLeftY = camera.getY() - (canvasHeight / 2.0) / scale;

        int minCX = (int) Math.floor(topLeftX / CHUNK_SIZE);
        int minCY = (int) Math.floor(topLeftY / CHUNK_SIZE);
        int maxCX = (int) Math.ceil((topLeftX + canvasWidth / scale) / CHUNK_SIZE);
        int maxCY = (int) Math.ceil((topLeftY + canvasHeight / scale) / CHUNK_SIZE);

        for (int cy = minCY; cy <= maxCY; cy++) {
            for (int cx = minCX; cx <= maxCX; cx++) {
                WritableImage chunk = chunkImages.get(chunkKey(cx, cy));
                if (chunk == null) continue;
                double dstX = (cx * CHUNK_SIZE - topLeftX) * scale;
                double dstY = (cy * CHUNK_SIZE - topLeftY) * scale;
                double dstW = (chunk.getWidth() / TILE_SIZE) * scale;
                double dstH = (chunk.getHeight() / TILE_SIZE) * scale;
                gc.drawImage(chunk, dstX, dstY, dstW, dstH);
            }
        }
    }

    public void renderTiles(GraphicsContext gc, Camera camera, double canvasWidth, double canvasHeight) {
        double scaledTileSize = TILE_SIZE * camera.getZoom();
        double topLeftX = camera.getX() - (canvasWidth / 2) / scaledTileSize;
        double topLeftY = camera.getY() - (canvasHeight / 2) / scaledTileSize;
        int baseTileX = (int) Math.floor(topLeftX);
        int baseTileY = (int) Math.floor(topLeftY);
        double offsetX = -(topLeftX - baseTileX) * scaledTileSize;
        double offsetY = -(topLeftY - baseTileY) * scaledTileSize;
        int tilesX = (int) Math.ceil(canvasWidth / scaledTileSize);
        int tilesY = (int) Math.ceil(canvasHeight / scaledTileSize);

        for (int y = 0; y <= tilesY; y++) {
            for (int x = 0; x <= tilesX; x++) {
                int worldX = baseTileX + x;
                int worldY = baseTileY + y;
                double drawX = Math.floor(x * scaledTileSize + offsetX);
                double drawY = Math.floor(y * scaledTileSize + offsetY);

                if (worldX < 0 || worldX >= world.getWorldConfig().width ||
                        worldY < 0 || worldY >= world.getWorldConfig().height) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(drawX, drawY, scaledTileSize, scaledTileSize);
                    continue;
                }

                TileType tile = world.getTile(worldX, worldY, GenerationPassTypeSets.ALL);
                placeTile(gc, tile, worldX, worldY, drawX, drawY, scaledTileSize);
            }
        }
    }

    private void placeTile(GraphicsContext gc, TileType tile, int worldX, int worldY, double x, double y, double size) {
        gc.setFill(colorFor(tile, worldX, worldY));
        gc.fillRect(x, y, Math.ceil(size), Math.ceil(size));
        Image sprite = tileTypeImageMap.get(tile);
        if (sprite != null) gc.drawImage(sprite, x, y, Math.ceil(size), Math.ceil(size));
    }
    public void saveScreenshot(Stage stage) {
        if (worldImage == null) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Screenshot");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        chooser.setInitialFileName("world.png");
        File file = chooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(worldImage, null), "png", file);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    private Color colorFor(TileType tile, int x, int y) {
        TileType bg = tile.getBackground();
        if (bg != tile) return colorFor(bg, x, y);

        return switch (tile) {
            case GRASS -> Color.FORESTGREEN;
            case SAND -> Color.SANDYBROWN;
            case WATER -> world.getWaterColor(x, y).interpolate(
                    world.getLight(x, y).color,
                    world.getLight(x, y).normalizedIntensity * 0.5);
            case STONE -> Color.GRAY;
            case AIR -> world.getDepthOfPosition(x, y) < 0
                    ? world.getSkyColor(x, y)
                    : Color.BLACK.interpolate(world.getLight(x, y).color, world.getLight(x, y).normalizedIntensity);
            case DIRT -> Color.BROWN;
            case GRAVEL -> Color.LIGHTGRAY;
            case SNOW -> Color.WHITE;
            case LEAVES -> Color.DARKGREEN;
            case LOG -> Color.SADDLEBROWN;
            case CACTUS -> Color.LIMEGREEN;
            case COAL_ORE -> Color.DARKSLATEGRAY;
            case COPPER_ORE -> Color.rgb(184, 115, 51);
            case LAPIS_ORE -> Color.rgb(30, 60, 180);
            case AMETHYST_ORE -> Color.rgb(153, 50, 204);
            case RED_CLAY -> Color.rgb(180, 80, 60);
            case QUARTZ -> Color.rgb(220, 215, 210);
            case BEDROCK -> Color.rgb(20, 18, 24);
            default -> Color.HOTPINK;
        };
    }

    private long chunkKey(int cx, int cy) {
        return ((long) cx << 32) | (cy & 0xFFFFFFFFL);
    }
}