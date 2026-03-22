package uk.bradleyjones.worldgenerator.world.biomes;

import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.TerrainHeightGenerator;

public class BiomeGenerator {
    private final OpenSimplexNoise noise;
    private final BiomeGeneratorConfig config;
    private final BiomeOverrideConfig overrideConfig;
    private final TerrainHeightGenerator terrain;
    private final WaterBodyType[] waterBodyMap;
    private final int worldWidth;
    private final int worldHeight;

    public BiomeGenerator(int seed, BiomeGeneratorConfig config, BiomeOverrideConfig overrideConfig, TerrainHeightGenerator terrain, int worldWidth, int worldHeight) {
        this.noise = new OpenSimplexNoise(seed);
        this.config = config;
        this.overrideConfig = overrideConfig;
        this.terrain = terrain;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.waterBodyMap = computeWaterBodies();
    }

    public Biome getBiome(int x, int surfaceY, int baseHeight, int waterLevel) {
        double value = noise.getNoise2D(x * config.noiseScale, 0).getValue();

        // Remap noise to flatten distribution away from centre
        double remapped = Math.signum(value) * Math.pow(Math.abs(value), 0.5);

        // Normalise to 0..1
        double normalized = (remapped + 1) / 2.0;

        // Calculate total weight
        float totalWeight = 0;
        for (BiomeEntry entry : config.biomes) {
            totalWeight += entry.weight;
        }

        // Get base biome from noise
        Biome base = config.biomes.get(config.biomes.size() - 1).type;
        float cursor = 0;
        for (BiomeEntry entry : config.biomes) {
            cursor += entry.weight / totalWeight;
            if (normalized < cursor) {
                base = entry.type;
                break;
            }
        }

        // Apply override rules in priority order
        if (isMountainPeak(surfaceY, baseHeight)) return Biome.MOUNTAIN_PEAK;
        if (isMountain(surfaceY, baseHeight)) return Biome.MOUNTAIN;
        if (isOcean(x)) return Biome.OCEAN;
        if (isLake(x)) return Biome.LAKE;
        if (isBeach(x, surfaceY, waterLevel)) return Biome.BEACH;

        return base;
    }

    private boolean isMountainPeak(int surfaceY, int baseHeight) {
        return surfaceY < baseHeight - overrideConfig.peakHeight;
    }

    private boolean isMountain(int surfaceY, int baseHeight) {
        return surfaceY < baseHeight - overrideConfig.mountainHeight;
    }

    private boolean isOcean(int x) {
        return waterBodyMap[x] == WaterBodyType.OCEAN;
    }

    private boolean isLake(int x) {
        return waterBodyMap[x] == WaterBodyType.LAKE;
    }

    private boolean isBeach(int x, int surfaceY, int waterLevel) {
        if (surfaceY > waterLevel || surfaceY <= waterLevel - overrideConfig.beachWidth) return false;

        // Only beach if adjacent to an actual water body
        for (int i = x - overrideConfig.beachWidth; i <= x + overrideConfig.beachWidth; i++) {
            if (i < 0 || i >= waterBodyMap.length) continue;
            if (waterBodyMap[i] != WaterBodyType.NONE) {
                return true;
            }
        }
        return false;
    }

    private WaterBodyType[] computeWaterBodies() {
        WaterBodyType[] map = new WaterBodyType[worldWidth];
        int waterLevel = worldHeight - Math.min(overrideConfig.waterLevelRef, worldHeight - 1);

        int x = 0;
        while (x < worldWidth) {
            if (terrain.getHeight(x) <= waterLevel) {
                // Above water - not a water body
                map[x] = WaterBodyType.NONE;
                x++;
            } else {
                // Submerged region - find extent
                int start = x;
                while (x < worldWidth && terrain.getHeight(x) > waterLevel) {
                    x++;
                }
                int width = x - start;

                WaterBodyType type;
                if (width >= overrideConfig.oceanMinWidth) {
                    type = WaterBodyType.OCEAN;
                } else if (width >= overrideConfig.lakeMinWidth) {
                    type = WaterBodyType.LAKE;
                } else {
                    type = WaterBodyType.NONE;
                }

                for (int i = start; i < x; i++) {
                    map[i] = type;
                }
            }
        }
        return map;
    }
}