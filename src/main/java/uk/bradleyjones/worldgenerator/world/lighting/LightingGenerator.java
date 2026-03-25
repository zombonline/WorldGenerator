package uk.bradleyjones.worldgenerator.world.lighting;

import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.World;

import java.util.ArrayDeque;
import java.util.Queue;

public class LightingGenerator {
    private  World world;
    Light[][] lightingMap;
    private int maxLightingLevel = 50;
    public LightingGenerator(World world)
    {
        this.world = world;
        generate();
    }
    public void generate() {
        int width = world.getWorldConfig().width;
        int height = world.getWorldConfig().height;

        lightingMap = new Light[width][height];
        Queue<LightNode> queue = new ArrayDeque<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                lightingMap[x][y] = new Light(0, Color.BLACK, 0);

                Light source = getLightSource(x, y);
                if (source != null) {
                    lightingMap[x][y] = new Light(
                            source.intensity,
                            source.color,
                            source.intensity/maxLightingLevel
                    );

                    queue.add(new LightNode(x, y, 0, 0, lightingMap[x][y]));
                }
            }
        }

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1,1}, {-1,-1}, {-1,1}, {1, -1}
        };

        while (!queue.isEmpty()) {
            LightNode node = queue.poll();
            int x = node.x;
            int y = node.y;

            Light current = lightingMap[x][y];
            double currentLight = current.intensity;

            int px = node.dx;
            int py = node.dy;

            for (int[] dir : directions) {
                int dx = dir[0];
                int dy = dir[1];

                int nx = x + dx;
                int ny = y + dy;

                if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue;

                double newLight = currentLight - 1;

                // diagonal path check
                if (Math.abs(dx + dy) != 1) {
                    if(world.getTile(x, ny, true)!=TileType.AIR || world.getTile(nx, y, true)!= TileType.AIR) {
                        continue;
                    }
                    newLight -= 0.5;
                }

                // turning penalty
                if (px != 0 || py != 0) {
                    if (dx != px || dy != py) {
                        newLight -= 1;
                    }
                }

                if (newLight <= 0) continue;
                if (world.getTile(nx, ny, true) != TileType.AIR) continue;

                Light target = lightingMap[nx][ny];

                if (newLight > 0) {

                    double existing = target.intensity;
                    double total = existing + newLight;
                    if (total > 0) {
                        double r =
                                (target.color.getRed() * existing + current.color.getRed() * newLight) / total;

                        double g =
                                (target.color.getGreen() * existing + current.color.getGreen() * newLight) / total;

                        double b =
                                (target.color.getBlue() * existing + current.color.getBlue() * newLight) / total;

                        target.color = Color.color(r, g, b);
                    }
                    // keep strongest intensity
                    if (newLight > existing) {
                        target.intensity = newLight;
                        target.normalizedIntensity = newLight / maxLightingLevel;

                        queue.add(new LightNode(nx, ny, dx, dy, target));
                    }
                }
            }
        }
    }
    public Light getLightSource(int x, int y) {
        // Sunlight (above ground air)
        if (world.getTile(x, y, true) == TileType.AIR && world.getDepthOfPosition(x, y) < 0) {
            return new Light(maxLightingLevel, Color.LIGHTBLUE);
        }

        // Mushroom glow
        if (world.getTile(x, y, false) == TileType.PINK_MUSHROOM) {
            return new Light(maxLightingLevel * 0.2, Color.PINK);
        }

        return null;
    }
    public Light getLight(int x, int y){
        return lightingMap[x][y];
    }
}
