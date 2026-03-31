package uk.bradleyjones.worldgenerator.world;

import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.biomes.Biome;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeGenerator;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeGeneratorConfig;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeOverrideConfig;
import uk.bradleyjones.worldgenerator.world.caves.*;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationGenerator;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapConfig;
import uk.bradleyjones.worldgenerator.world.lighting.Light;
import uk.bradleyjones.worldgenerator.world.lighting.LightingGenerator;

import java.util.ArrayList;
import java.util.List;

public class World {

    private WorldConfig worldConfig = new WorldConfig();
    private HeightmapConfig heightmapConfig = new HeightmapConfig(worldConfig.seed);
    private BiomeGeneratorConfig biomeGeneratorConfig = new BiomeGeneratorConfig();
    private BiomeOverrideConfig biomeOverrideConfig = new BiomeOverrideConfig();
    private List<CaveGeneratorInstance> caveInstances = new ArrayList<>();
    private List<DecorationInstance> decorationInstances = new ArrayList<>();

    private BiomeGenerator biomeGenerator;
    private List<CaveGenerator> caveGenerators = new ArrayList<>();
    private DecorationGenerator decorationGenerator;
    private LightingGenerator lightingGenerator;
    private List<Decoration> activeDecorations = new ArrayList<>();
    private OpenSimplexNoise noise;

    public World() {
        regenerate();
    }

    public TileType getTile(int x, int y, boolean ignoreDecorations) {
        if (x < 0 || x >= worldConfig.width || y < 0 || y >= worldConfig.height) {
            return TileType.AIR;
        }
        if(!ignoreDecorations) {
            TileType decoration = decorationGenerator != null ? decorationGenerator.getTile(x, y) : null;
            if (decoration != null) return decoration;
        }
        int clampedWaterLevel = worldConfig.height - Math.min(worldConfig.waterLevel, worldConfig.height - 1);
        if (getDepthOfPosition(x,y) < 0) {
            return y > clampedWaterLevel ? TileType.WATER : TileType.AIR;
        }

        if (isCave(x,y)) {
            return TileType.AIR;
        }
        if (getDepthOfPosition(x,y) == 0) return biomeGenerator.getBiome(x).surfaceTile;
        if (getDepthOfPosition(x,y) <= getSubSurfaceDepth(x)) return biomeGenerator.getBiome(x).subsurfaceTile;
        return TileType.STONE;
    }

    private boolean isCave(int x, int y) {
        return caveGenerators.stream().anyMatch(g -> g.isCave(x, y));
    }

    public Biome getBiomeAt(int x) {
        return biomeGenerator.getBiome(x);
    }

    public void addCaveInstance(CaveGeneratorType type) {
        caveInstances.add(new CaveGeneratorInstance(type));
    }

    public void removeCaveInstance(CaveGeneratorInstance instance) {
        caveInstances.remove(instance);
    }

    public void addDecorationInstance(DecorationInstance instance) {
        decorationInstances.add(instance);
    }

    public void removeDecorationInstance(DecorationInstance instance) {
        decorationInstances.remove(instance);
    }


    public void regenerate() {
        noise = new OpenSimplexNoise(worldConfig.seed*2L);
        heightmapConfig.heightmapGroup.refreshSeed(worldConfig.seed);
        biomeOverrideConfig.waterLevelRef = worldConfig.waterLevel;
        biomeGenerator = new BiomeGenerator(
                worldConfig.seed + 100,
                biomeGeneratorConfig,
                biomeOverrideConfig,
                this
        );

        caveGenerators.clear();
        for (CaveGeneratorInstance instance : caveInstances) {
            if (!instance.enabled) continue;
            switch (instance.type) {
                case CA -> caveGenerators.add(new CACaveGenerator(
                        worldConfig.width, worldConfig.height, worldConfig.seed,
                        instance.caConfig, this
                ));
                case NOISE -> caveGenerators.add(new NoiseCaveGenerator(
                        worldConfig.seed, instance.noiseConfig, this
                ));
                case DRUNKARD -> caveGenerators.add(new DrunkardCaveGenerator(
                        worldConfig.width, worldConfig.height, worldConfig.seed,
                        instance.drunkardConfig, this
                ));
            }
        }
        activeDecorations.clear();
        for(var instance : decorationInstances) {
            if (!instance.enabled) continue;
            activeDecorations.add(instance.decoration);
        }
        decorationGenerator = new DecorationGenerator(this, worldConfig.seed, activeDecorations);
        lightingGenerator = new LightingGenerator(this);
    }

    public Light getExposedLevel(int x, int y) {
        return lightingGenerator.getLight(x, y);
    }

    public int getDepthOfPosition(int x, int y) {
        return y - getSurfaceY(x);
    }

    public int getSurfaceY(int x) {
        int clampedBase = worldConfig.height - Math.min(heightmapConfig.baseHeight, worldConfig.height - 1);
        return clampedBase + heightmapConfig.heightmapGroup.getHeight(x);
    }

    //World getters to reach into it's generators
    public int getSubSurfaceDepth(int x) {
        double raw = noise.getNoise2D(x,0).getValue();
        double normalized = (raw + 1)/2;
        int subsurfaceY =(int)(normalized * (heightmapConfig.maxSubSurfaceDepth-heightmapConfig.minSubSurfaceDepth) + heightmapConfig.minSubSurfaceDepth);
        return subsurfaceY;
    }

    //GET CONFIGS, should only be used by UI/Controller

    public List<DecorationInstance> getDecorationInstances() {
        return decorationInstances;
    }

    public WorldConfig getWorldConfig() {
        return worldConfig;
    }

    public HeightmapConfig getHeightmapConfig() {
        return heightmapConfig;
    }

    public List<CaveGeneratorInstance> getCaveInstances() {
        return caveInstances;
    }

    public BiomeGeneratorConfig getBiomeGeneratorConfig() {
        return biomeGeneratorConfig;
    }

    public BiomeOverrideConfig getBiomeOverrideConfig() {
        return biomeOverrideConfig;
    }
}