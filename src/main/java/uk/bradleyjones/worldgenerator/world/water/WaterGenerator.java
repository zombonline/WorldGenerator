package uk.bradleyjones.worldgenerator.world.water;

import java.util.ArrayDeque;
import java.util.Deque;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class WaterGenerator {

    private WaterBodyType[] waterBodyMap;
    private boolean[][] waterGrid;

    public WaterGenerator() {
        waterGrid = new boolean[world.getWorldConfig().width][world.getWorldConfig().height];
        waterBodyMap = computeWaterBodies();
        placeSurfaceWater();
        findEntryPointsAndFlood();
    }

    public WaterBodyType getWaterBodyType(int x) {
        if (x < 0 || x >= waterBodyMap.length) return WaterBodyType.NONE;
        return waterBodyMap[x];
    }

    public boolean isWater(int x, int y) {
        return waterGrid[x][y];
    }

    private void placeSurfaceWater() {
        int worldHeight = world.getWorldConfig().height;
        int waterLevel = worldHeight - Math.min(world.getWaterConfig().waterLevel, worldHeight - 1);

        for (int x = 0; x < world.getWorldConfig().width; x++) {
            if (waterBodyMap[x] == WaterBodyType.NONE) continue;
            int surfaceY = world.getSurfaceY(x);
            for (int y = waterLevel; y < surfaceY; y++) {
                if(y > waterGrid[x].length)
                    continue;
                waterGrid[x][y] = true;
            }
        }
    }


    private void findEntryPointsAndFlood() {
        int worldHeight = world.getWorldConfig().height;
        int waterLevel = worldHeight - Math.min(world.getWaterConfig().waterLevel, worldHeight - 1);

        Deque<WaterNode> queue = new ArrayDeque<>();

        // Find cave entry points at bottom of surface water columns
        for (int x = 0; x < world.getWorldConfig().width; x++) {
            if (waterBodyMap[x] == WaterBodyType.NONE) continue;

            int surfaceY = world.getSurfaceY(x);
            int waterDepth = surfaceY - waterLevel;
            float startPressure = waterDepth * world.getWaterConfig().pressurePerDepth;

            if (world.isCave(x, surfaceY) && startPressure > 0) {
                queue.add(new WaterNode(x, surfaceY, startPressure));
            }
        }


        // Pressure flood fill
        float[][] pressureMap = new float[world.getWorldConfig().width][world.getWorldConfig().height];


        while (!queue.isEmpty()) {
            WaterNode node = queue.poll();
            int x = node.x;
            int y = node.y;
            float pressure = node.pressure;

            if (pressure <= world.getWaterConfig().minPressureToFlood) continue;
            if (x < 0 || x >= world.getWorldConfig().width || y < 0 || y >= world.getWorldConfig().height) continue;
            if (!isPassable(x, y)) continue;
            if (pressureMap[x][y] >= pressure) continue;

            pressureMap[x][y] = pressure;
            waterGrid[x][y] = true;

            // Spread down — no pressure cost
            queue.addFirst(new WaterNode(x, y + 1, pressure));

            boolean tileUnder = !world.isCave(x,y+1);
            int waterTilesAbove = 0;
            int yCheck = y-1;
            while(yCheck > 0) {
                if(!waterGrid[x][yCheck])
                    break;
                waterTilesAbove++;
                yCheck--;
            }

            float falloff = waterTilesAbove*.15f;
            falloff = Math.clamp(falloff, 0, 1);

            float sidewaysPressure = tileUnder ? pressure : pressure * falloff;

            queue.addLast(new WaterNode(x + 1, y, sidewaysPressure));
            queue.addLast(new WaterNode(x - 1, y, sidewaysPressure));

            // Spread up — costs upwardCost, only if enough pressure
            if (pressure - world.getWaterConfig().upwardCost > world.getWaterConfig().minPressureToFlood) {
                queue.addLast(new WaterNode(x, y - 1, pressure - world.getWaterConfig().upwardCost));
            }
        }
    }

    private boolean isPassable(int x, int y) {
        if (x < 0 || x >= world.getWorldConfig().width ||
                y < 0 || y >= world.getWorldConfig().height) return false;
        if (waterGrid[x][y]) return false; // already water
        if (world.getDepthOfPosition(x, y) < 0) return false; // above surface
        return world.isCave(x, y); // only flood cave tiles
    }

    private WaterBodyType[] computeWaterBodies() {
        int worldWidth = world.getWorldConfig().width;
        int worldHeight = world.getWorldConfig().height;
        WaterBodyType[] map = new WaterBodyType[worldWidth];
        int waterLevel = worldHeight - Math.min(world.getWaterConfig().waterLevel , worldHeight - 1);

        int x = 0;
        while (x < worldWidth) {
            if (world.getSurfaceY(x) <= waterLevel) {
                // Above water - not a water body
                map[x] = WaterBodyType.NONE;
                x++;
            } else {
                // Submerged region - find extent
                int start = x;
                while (x < worldWidth && world.getSurfaceY(x) > waterLevel) {
                    x++;
                }
                int width = x - start;

                WaterBodyType type;
                if (width >= world.getWaterConfig().oceanMinWidth) {
                    type = WaterBodyType.OCEAN;
                } else if (width >= world.getWaterConfig().lakeMinWidth) {
                    type = WaterBodyType.LAKE;
                } else {
                    type = WaterBodyType.NEGLIGIBLE;
                }

                for (int i = start; i < x; i++) {
                    map[i] = type;
                }
            }
        }
        return map;
    }
}
