package uk.bradleyjones.worldgenerator.world;

import com.raylabz.opensimplex.OpenSimplexNoise;
import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.world.biomes.*;
import uk.bradleyjones.worldgenerator.world.caves.*;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationGenerator;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapConfig;
import uk.bradleyjones.worldgenerator.world.lighting.Light;
import uk.bradleyjones.worldgenerator.world.lighting.LightingGenerator;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceGenerator;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;
import uk.bradleyjones.worldgenerator.world.water.WaterBodyType;
import uk.bradleyjones.worldgenerator.world.water.WaterConfig;
import uk.bradleyjones.worldgenerator.world.water.WaterGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class World {

    //CONFIG FILES
    private final WorldConfig worldConfig = new WorldConfig();
    private final HeightmapConfig heightmapConfig = new HeightmapConfig(worldConfig.seed);
    private final BiomeGeneratorConfig biomeGeneratorConfig = new BiomeGeneratorConfig();
    private final BiomeOverrideConfig biomeOverrideConfig = new BiomeOverrideConfig();
    private final WaterConfig waterConfig = new WaterConfig();
    private final List<CaveGeneratorInstance> caveInstances = new ArrayList<>();
    private final List<DecorationInstance> decorationInstances = new ArrayList<>();
    private final List<SubstanceRule> substanceRules = new ArrayList<>();


    //GENERATORS
    private BiomeGenerator biomeGenerator;
    private WaterGenerator waterGenerator;
    private SubstanceGenerator substanceGenerator;
    private DecorationGenerator decorationGenerator;
    private LightingGenerator lightingGenerator;
    private final List<CaveGenerator> caveGenerators = new ArrayList<>();
    private final List<Decoration> activeDecorations = new ArrayList<>();

    //extra noise map for random elements;
    private OpenSimplexNoise noise;

    public World() {}

    public TileType getTile(int x, int y, EnumSet<GenerationPassType> passes) {
        if (x < 0 || x >= worldConfig.width || y < 0 || y >= worldConfig.height) return TileType.AIR;
        if (y == worldConfig.height - 1) return TileType.BEDROCK;
        if (y > worldConfig.height - worldConfig.bedrockHeight) {
            if (noise.getNoise2D(x * worldConfig.bedrockNoiseScale, y * worldConfig.bedrockNoiseScale).getValue() > 0)
                return TileType.BEDROCK;
        }

        if (passes.contains(GenerationPassType.DECORATIONS)) {
            TileType decoration = decorationGenerator != null ? decorationGenerator.getTile(x, y) : null;
            if (decoration != null) return decoration;
        }

        if (passes.contains(GenerationPassType.WATER) && waterGenerator.isWater(x, y)) return TileType.WATER;
        if (getDepthOfPosition(x, y) < 0) return TileType.AIR;
        if (passes.contains(GenerationPassType.CAVES) && isCave(x, y)) return TileType.AIR;

        int depth = getDepthOfPosition(x, y);
        boolean useBiome = passes.contains(GenerationPassType.BIOME);
        boolean useSubstance = passes.contains(GenerationPassType.SUBSTANCE);

        TileType surface = useBiome ? biomeGenerator.getBiome(x).getSurfaceTile() : TileType.GRASS;
        TileType subsurface = useBiome ? biomeGenerator.getBiome(x).getSubsurfaceTile() : TileType.DIRT;

        TileType raw = depth == 0 ? surface
                : depth <= getSubSurfaceDepth(x) ? subsurface
                : TileType.STONE;

        return useSubstance ? substanceGenerator.getOverride(x, y, raw) : raw;
    }

    public void regenerate() {
        noise = new OpenSimplexNoise(worldConfig.seed * 2L);
        heightmapConfig.heightmapGroup.regenerate();
        biomeGenerator = new BiomeGenerator();
        substanceGenerator = new SubstanceGenerator(substanceRules);

        caveGenerators.clear();
        for (CaveGeneratorInstance instance : caveInstances) {
            if (!instance.enabled) continue;
            switch (instance.type) {
                case CA -> caveGenerators.add(new CACaveGenerator(instance.caConfig));
                case NOISE -> caveGenerators.add(new NoiseCaveGenerator(instance.noiseConfig));
                case DRUNKARD -> caveGenerators.add(new DrunkardCaveGenerator(instance.drunkardConfig));
            }
        }

        waterGenerator = new WaterGenerator();

        activeDecorations.clear();
        for (DecorationInstance instance : decorationInstances) {
            if (instance.enabled) activeDecorations.add(instance.decoration);
        }
        decorationGenerator = new DecorationGenerator(worldConfig.seed, activeDecorations);
        lightingGenerator = new LightingGenerator();
    }

    // position/space queries
    public boolean isCave(int x, int y) {
        return caveGenerators.stream().anyMatch(g -> g.isCave(x, y));
    }

    public int getDepthOfPosition(int x, int y) {
        return y - getSurfaceY(x);
    }

    public int getDepthOfPositionBase(int x, int y) {
        return y - getBaseSurfaceY(x);
    }

    public int getSurfaceY(int x) {
        int clampedBase = worldConfig.height - Math.min(heightmapConfig.baseHeight, worldConfig.height - 1);
        return clampedBase + heightmapConfig.heightmapGroup.getHeight(x);
    }

    public int getBaseSurfaceY(int x) {
        return worldConfig.height - Math.min(heightmapConfig.baseHeight, worldConfig.height - 1);
    }

    public int getSubSurfaceDepth(int x) {
        double raw = noise.getNoise2D(x, 0).getValue();
        double normalized = (raw + 1) / 2;
        return (int) (normalized * (heightmapConfig.maxSubSurfaceDepth - heightmapConfig.minSubSurfaceDepth) + heightmapConfig.minSubSurfaceDepth);
    }

    public boolean isNearWater(int x, int y) {
        for (int nx = x - 5; nx <= x + 5; nx++)
            for (int ny = y - 5; ny <= y + 5; ny++)
                if (isWaterRaw(nx, ny)) return true;
        return false;
    }

    private boolean isWaterRaw(int x, int y) {
        if (x < 0 || x >= worldConfig.width || y < 0 || y >= worldConfig.height) return false;
        return waterGenerator.isWater(x, y);
    }

    //color queries
    public Color getSkyColor(int x, int y) {
        int skySpace = worldConfig.height - heightmapConfig.baseHeight;
        double baseDepth = (double) (-getDepthOfPositionBase(x, y)) / skySpace;
        double noiseOffset = noise.getNoise2D(x*.7, 0).getValue() * .008;
        double skyDepth = Math.clamp(baseDepth + noiseOffset, 0.0, 1.0);
        Color skyTop = Color.rgb(30, 60, 120);
        Color skyHorizon = Color.rgb(135, 206, 235);
        return skyHorizon.interpolate(skyTop, skyDepth);
    }
    public Color getWaterColor(int x, int y) {
        int depthBelowWaterLevel = y - waterConfig.waterLevel;
        double depthT = Math.clamp(depthBelowWaterLevel / 40.0, 0.0, 1.0);
        return Color.DEEPSKYBLUE.interpolate(Color.MIDNIGHTBLUE, depthT);
    }

    //pass through methods for worlds generators
    //lighting
    public Light getLight(int x, int y) {
        return lightingGenerator.getLight(x, y);
    }

    // water
    public WaterBodyType getWaterBodyType(int x) {
        return waterGenerator.getWaterBodyType(x);
    }

    //biome
    public Biome getBiomeAt(int x) {
        return biomeGenerator.getBiome(x);
    }

    //config getters for ui to set values on and other systems to read from
    public WorldConfig getWorldConfig() { return worldConfig; }
    public HeightmapConfig getHeightmapConfig() { return heightmapConfig; }
    public WaterConfig getWaterConfig() { return waterConfig; }
    public BiomeGeneratorConfig getBiomeGeneratorConfig() { return biomeGeneratorConfig; }
    public BiomeOverrideConfig getBiomeOverrideConfig() { return biomeOverrideConfig; }
    public List<CaveGeneratorInstance> getCaveInstances() { return caveInstances; }
    public List<DecorationInstance> getDecorationInstances() { return decorationInstances; }
    public List<SubstanceRule> getSubstanceRules() { return substanceRules; }

}