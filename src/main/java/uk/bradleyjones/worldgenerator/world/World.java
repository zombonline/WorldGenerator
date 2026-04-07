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
import uk.bradleyjones.worldgenerator.world.substances.SubstanceGenerator;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;
import uk.bradleyjones.worldgenerator.world.water.WaterBodyType;
import uk.bradleyjones.worldgenerator.world.water.WaterConfig;
import uk.bradleyjones.worldgenerator.world.water.WaterGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class World {

    private WorldConfig worldConfig = new WorldConfig();
    private HeightmapConfig heightmapConfig = new HeightmapConfig(worldConfig.seed);
    private BiomeGeneratorConfig biomeGeneratorConfig = new BiomeGeneratorConfig();
    private BiomeOverrideConfig biomeOverrideConfig = new BiomeOverrideConfig();
    private WaterConfig waterConfig = new WaterConfig();
    private List<CaveGeneratorInstance> caveInstances = new ArrayList<>();
    private List<DecorationInstance> decorationInstances = new ArrayList<>();
    private List<SubstanceRule> substanceRules = new ArrayList<>();

    private BiomeGenerator biomeGenerator;
    private WaterGenerator waterGenerator;
    private List<CaveGenerator> caveGenerators = new ArrayList<>();
    private DecorationGenerator decorationGenerator;
    private LightingGenerator lightingGenerator;
    private List<Decoration> activeDecorations = new ArrayList<>();
    private OpenSimplexNoise noise;
    private SubstanceGenerator substanceGenerator;



    public World() {

    }

    public TileType getTile(int x, int y, EnumSet<GenerationPassType> passes) {
        if (x < 0 || x >= worldConfig.width || y < 0 || y >= worldConfig.height) {
            return TileType.AIR;
        }

        if (passes.contains(GenerationPassType.DECORATIONS)) {
            TileType decoration = decorationGenerator != null ? decorationGenerator.getTile(x, y) : null;
            if (decoration != null) return decoration;
        }

        if (passes.contains(GenerationPassType.WATER) && waterGenerator.isWater(x, y)) {
            return TileType.WATER;
        }

        if (getDepthOfPosition(x, y) < 0) return TileType.AIR;

        if (passes.contains(GenerationPassType.CAVES) && isCave(x, y)) {
            return TileType.AIR;
        }

        int depth = getDepthOfPosition(x, y);
        boolean useBiome = passes.contains(GenerationPassType.BIOME);
        boolean useSubstance = passes.contains(GenerationPassType.SUBSTANCE);

        TileType surface = useBiome ? biomeGenerator.getBiome(x).surfaceTile : TileType.GRASS;
        TileType subsurface = useBiome ? biomeGenerator.getBiome(x).subsurfaceTile : TileType.DIRT;
        TileType base = TileType.STONE;

        TileType raw = depth == 0 ? surface
                : depth <= getSubSurfaceDepth(x) ? subsurface
                : base;

        return useSubstance ? substanceGenerator.getOverride(x, y, raw) : raw;
    }
    public boolean isCave(int x, int y) {
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

    public void addSubstanceRule(SubstanceRule rule) {
        substanceRules.add(rule);
    }

    public void removeSubstanceRule(SubstanceRule rule) {
        substanceRules.remove(rule);
    }

    public List<SubstanceRule> getSubstanceRules() {
        return substanceRules;
    }


    public void regenerate() {
        noise = new OpenSimplexNoise(worldConfig.seed*2L);
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
        for(var instance : decorationInstances) {
            if (!instance.enabled) continue;
            activeDecorations.add(instance.decoration);
        }
        decorationGenerator = new DecorationGenerator(worldConfig.seed, activeDecorations);
        lightingGenerator = new LightingGenerator();
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

    public WaterBodyType getWaterBodyType(int x) {
        return waterGenerator.getWaterBodyType(x);
    }

    private boolean isWaterRaw(int x, int y) {
        if(x < 0 || x >= worldConfig.width || y < 0 || y >= getWorldConfig().height)
            return false;
        return waterGenerator.isWater(x, y);
    }

    public boolean isNearWater(int x, int y) {
        for (int nx = x - 5; nx <= x + 5; nx++) {
            for (int ny = y - 5; ny <= y + 5; ny++) {
                if (isWaterRaw(nx, ny)) return true;
            }
        }
        return false;
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
    public WaterConfig getWaterConfig() {
        return waterConfig;
    }



}