package uk.bradleyjones.worldgenerator.world;

import uk.bradleyjones.worldgenerator.world.biomes.BiomeGenerator;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeGeneratorConfig;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeOverrideConfig;
import uk.bradleyjones.worldgenerator.world.caves.*;

import java.util.ArrayList;
import java.util.List;

public class World {

    public WorldConfig worldConfig = new WorldConfig();
    public TerrainConfig terrainConfig = new TerrainConfig();
    public BiomeGeneratorConfig biomeGeneratorConfig = new BiomeGeneratorConfig();
    public BiomeOverrideConfig biomeOverrideConfig = new BiomeOverrideConfig();
    public List<CaveGeneratorInstance> caveInstances = new ArrayList<>();

    private TerrainHeightGenerator terrainHeightGenerator;
    private BiomeGenerator biomeGenerator;
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
        int flippedBaseHeight = worldConfig.height - Math.min(terrainConfig.baseHeight, worldConfig.height - 1);
        if (y < surfaceY) {
            return y > clampedWaterLevel ? TileType.WATER : TileType.AIR;
        }

        int depth = y - surfaceY;

        if (depth > 4 && isCave(x,y)) {
            return TileType.AIR;
        }

        if (depth == 0) return biomeGenerator.getBiome(x, surfaceY, flippedBaseHeight, clampedWaterLevel).surfaceTile;
        if (depth <= 4) return biomeGenerator.getBiome(x, surfaceY, flippedBaseHeight, clampedWaterLevel).subsurfaceTile;
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

        biomeOverrideConfig.waterLevelRef = worldConfig.waterLevel;
        biomeGenerator = new BiomeGenerator(
                worldConfig.seed + 100,
                biomeGeneratorConfig,
                biomeOverrideConfig,
                terrainHeightGenerator,
                worldConfig.width,
                worldConfig.height
        );

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
                case DRUNKARD -> caveGenerators.add(new DrunkardCaveGenerator(
                        worldConfig.width, worldConfig.height, worldConfig.seed,
                        instance.drunkardConfig
                ));
            }
        }
    }

    public double getExposedLevel(int x, int y) {
        if (getTile(x, y) != TileType.AIR) return 0;
        int surfaceY = terrainHeightGenerator.getHeight(x);
        return y > surfaceY ? 1.0 : 0.0;
    }

}