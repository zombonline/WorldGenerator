package uk.bradleyjones.worldgenerator.world.lighting;

import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.World;

import java.util.ArrayDeque;
import java.util.Queue;

public class LightingGenerator {
    private  World world;
    double[][] lightingMap;
    private int maxLightingLevel = 50;
    public LightingGenerator(World world)
    {
        this.world = world;
        generate();
    }
    public void generate() {
        int width = world.getWorldConfig().width;
        int height = world.getWorldConfig().height;

        lightingMap = new double[width][height];

        Queue<int[]> queue = new ArrayDeque<>();

        //place lights
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (positionProvidesLighting(x, y)) {
                    lightingMap[x][y] = maxLightingLevel;
                    queue.add(new int[]{x, y, 0, 0});
                } else {
                    lightingMap[x][y] = 0;
                }
            }
        }

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1,1}, {-1,-1}, {-1,1}, {1, -1}
        };

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];

            double currentLight = lightingMap[x][y];

            int px = pos[2];
            int py = pos[3];

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

                if (newLight > lightingMap[nx][ny]) {
                    lightingMap[nx][ny] = newLight;
                    queue.add(new int[]{nx, ny, dx, dy});
                }
            }
        }
    }
    public boolean positionProvidesLighting(int x, int y )
    {
        if(world.getTile(x,y, true)== TileType.AIR && world.getDepthOfPosition(x,y) < 0)
            return true;
        if(world.getTile(x,y,false) == TileType.PINK_MUSHROOM)
            return true;
        return false;
    }

    public double getLightingLevel(int x, int y){
        return lightingMap[x][y]/maxLightingLevel;
    }
}
