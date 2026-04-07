package uk.bradleyjones.worldgenerator.world.biomes;

import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.water.WaterBodyType;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class BiomeGenerator {
    private final OpenSimplexNoise noise;
    private final BiomeGeneratorConfig config;
    private final BiomeOverrideConfig overrideConfig;
    private float totalWeight;
    public BiomeGenerator() {
        this.noise = new OpenSimplexNoise(world.getWorldConfig().seed);
        this.config = world.getBiomeGeneratorConfig();
        calculateTotalWeight();
        this.overrideConfig = world.getBiomeOverrideConfig();
    }

    private void calculateTotalWeight() {
        totalWeight = 0;
        for (BiomeEntry entry : config.biomes) {
            totalWeight += entry.weight;
        }
    }

    public Biome getBiome(int x) {
        double value = noise.getNoise2D(x * config.noiseScale, 0).getValue();

        // Remap noise to flatten distribution away from centre
        double remapped = Math.signum(value) * Math.pow(Math.abs(value), 0.5);

        // Normalise to 0..1
        double normalized = (remapped + 1) / 2.0;

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
        int surfaceY = world.getSurfaceY(x);
        int baseHeight = world.getHeightmapConfig().baseHeight;
        int waterLevel = world.getWorldConfig().waterLevel;
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
        return world.getWaterBodyType(x) == WaterBodyType.OCEAN;
    }

    private boolean isLake(int x) {
        return world.getWaterBodyType(x) == WaterBodyType.LAKE;
    }

    private boolean isBeach(int x, int surfaceY, int waterLevel) {
        if (surfaceY > waterLevel || surfaceY <= waterLevel - overrideConfig.beachWidth) return false;

        // Only beach if adjacent to an actual water body
        for (int i = x - overrideConfig.beachWidth; i <= x + overrideConfig.beachWidth; i++) {
            if (i < 0 || i >= world.getWorldConfig().width) continue;
            if (world.getWaterBodyType(x) != WaterBodyType.NONE) {
                return true;
            }
        }
        return false;
    }


}