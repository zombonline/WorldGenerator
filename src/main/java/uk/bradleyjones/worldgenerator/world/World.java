package uk.bradleyjones.worldgenerator.world;

import uk.bradleyjones.worldgenerator.world.caves.*;

import java.util.ArrayList;
import java.util.List;

public class World {

    public WorldConfig worldConfig = new WorldConfig();
    public TerrainConfig terrainConfig = new TerrainConfig();
    public List<CaveGeneratorInstance> caveInstances = new ArrayList<>();

    private TerrainHeightGenerator terrainHeightGenerator;
    private List<CaveGenerator> caveGenerators = new ArrayList<>();


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

        if (depth > 4 && isCave(x,y)) {
            return surfaceY >= clampedWaterLevel ? TileType.WATER : TileType.AIR;
        }

        if (depth == 0) return surfaceY > clampedWaterLevel ? TileType.SAND : TileType.GRASS;
        if (depth <= 4) return surfaceY > clampedWaterLevel ? TileType.SAND : TileType.DIRT;
        return TileType.STONE;
    }

    private boolean isCave(int x, int y) {
        return caveGenerators.stream().anyMatch(g -> g.isCave(x, y));
    }

    public void addCaveInstance(CaveGeneratorType type) {
        caveInstances.add(new CaveGeneratorInstance(type));
    }

    public void removeCaveInstance(CaveGeneratorInstance instance) {
        caveInstances.remove(instance);
    }

    public void regenerate() {
        terrainHeightGenerator = new TerrainHeightGenerator(worldConfig.seed, worldConfig.height, terrainConfig);

        caveGenerators.clear();
        for (CaveGeneratorInstance instance : caveInstances) {
            if (!instance.enabled) continue;
            switch (instance.type) {
                case CA -> caveGenerators.add(new CACaveGenerator(
                        worldConfig.width, worldConfig.height, worldConfig.seed,
                        instance.caConfig
                ));
                case NOISE -> caveGenerators.add(new NoiseCaveGenerator(
                        worldConfig.width, worldConfig.height, worldConfig.seed,
                        instance.noiseConfig
                        ));
                // NOISE and DRUNKARD cases added later
            }
        }
    }
}