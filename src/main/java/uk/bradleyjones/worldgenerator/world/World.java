package uk.bradleyjones.worldgenerator.world;

import uk.bradleyjones.worldgenerator.world.caves.CACaveConfig;
import uk.bradleyjones.worldgenerator.world.caves.CACaveGenerator;
import uk.bradleyjones.worldgenerator.world.caves.CaveGenerator;

public class World {

    public WorldConfig worldConfig = new WorldConfig();
    public TerrainConfig terrainConfig = new TerrainConfig();
    public CACaveConfig caveConfig = new CACaveConfig();

    private TerrainHeightGenerator terrainHeightGenerator;
    private CaveGenerator caveGenerator;

    public World() {
        regenerate();
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || x >= worldConfig.width || y < 0 || y >= worldConfig.height) {
            return TileType.AIR;
        }

        int surfaceY = terrainHeightGenerator.getHeight(x);
        int clampedWaterLevel = worldConfig.height - Math.min(worldConfig.waterLevel, worldConfig.height - 1);

        if (y < surfaceY) {
            return y > clampedWaterLevel ? TileType.WATER : TileType.AIR;
        }

        int depth = y - surfaceY;

        if (depth > 4 && caveConfig.enabled && caveGenerator != null && caveGenerator.isCave(x, y)) {
            return surfaceY >= clampedWaterLevel ? TileType.WATER : TileType.AIR;
        }

        if (depth == 0) return surfaceY > clampedWaterLevel ? TileType.SAND : TileType.GRASS;
        if (depth <= 4) return surfaceY > clampedWaterLevel ? TileType.SAND : TileType.DIRT;
        return TileType.STONE;
    }

    public void regenerate() {
        terrainHeightGenerator = new TerrainHeightGenerator(worldConfig.seed, worldConfig.height, terrainConfig);
        if (caveConfig.enabled) {
            caveGenerator = new CACaveGenerator(worldConfig.width, worldConfig.height, worldConfig.seed, caveConfig.fillPercent, caveConfig.iterations, caveConfig.neighbourThreshold);
        } else {
            caveGenerator = null;
        }
    }
}