package uk.bradleyjones.worldgenerator.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeEntry;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;
import uk.bradleyjones.worldgenerator.world.heightmap.*;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class WorldSaveManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save(World world, String name, Path path) throws IOException {
        WorldSave save = toSave(world, name);
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(save, writer);
        }
    }

    public static void load(World world, Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            WorldSave save = GSON.fromJson(reader, WorldSave.class);
            fromSave(world, save);
        }
    }

    // ---- SAVE ----

    private static WorldSave toSave(World world, String name) {
        WorldSave save = new WorldSave();
        save.name = name;
        save.world = worldData(world);
        save.water = waterData(world);
        save.heightmaps = heightmapData(world);
        save.caves = caveData(world);
        save.decorations = decorationData(world);
        save.biome_distribution = biomeDistributionData(world);
        save.biome_overrides = biomeOverrideData(world);
        save.substances = substanceData(world);
        return save;
    }

    private static WorldSave.WorldData worldData(World world) {
        WorldSave.WorldData d = new WorldSave.WorldData();
        d.seed = world.getWorldConfig().seed;
        d.width = world.getWorldConfig().width;
        d.height = world.getWorldConfig().height;
        return d;
    }

    private static WorldSave.WaterData waterData(World world) {
        WorldSave.WaterData d = new WorldSave.WaterData();
        var c = world.getWaterConfig();
        d.water_level = c.waterLevel;
        d.lake_min_width = c.lakeMinWidth;
        d.ocean_min_width = c.oceanMinWidth;
        d.pressure_per_depth = c.pressurePerDepth;
        d.upward_cost = c.upwardCost;
        d.min_pressure_to_flood = c.minPressureToFlood;
        return d;
    }

    private static WorldSave.HeightmapData heightmapData(World world) {
        WorldSave.HeightmapData d = new WorldSave.HeightmapData();
        var hc = world.getHeightmapConfig();
        d.base_height = hc.baseHeight;
        d.min_subsurface_depth = hc.minSubSurfaceDepth;
        d.max_subsurface_depth = hc.maxSubSurfaceDepth;
        d.root_group = groupData(hc.heightmapGroup);
        return d;
    }

    private static WorldSave.HeightmapGroupData groupData(HeightmapGroup group) {
        WorldSave.HeightmapGroupData d = new WorldSave.HeightmapGroupData();
        d.combine_mode = group.mode;
        d.noise_scale = group.noiseScale;
        d.children = new ArrayList<>();
        for (HeightmapChild child : group.children) {
            WorldSave.HeightmapChildData cd = new WorldSave.HeightmapChildData();
            cd.weight = child.weight;
            cd.enabled = child.enabled;
            if (child.node instanceof NoiseHeightmapGenerator n) {
                cd.type = "NOISE";
                WorldSave.NoiseGeneratorData nd = new WorldSave.NoiseGeneratorData();
                nd.scale = n.getScale();
                nd.amplitude = n.getAmplitude();
                nd.power = n.getPower();
                nd.clamp_to_positive = n.isClampToPositive();
                cd.noise_config = nd;
            } else if (child.node instanceof StepHeightmapGenerator s) {
                cd.type = "STEP";
                WorldSave.StepGeneratorData sd = new WorldSave.StepGeneratorData();
                sd.min_step_height = s.getMinStepHeight();
                sd.max_step_height = s.getMaxStepHeight();
                sd.min_step_gap = s.getMinStepGap();
                sd.max_step_gap = s.getMaxStepGap();
                cd.step_config = sd;
            } else if (child.node instanceof HeightmapGroup g) {
                cd.type = "GROUP";
                cd.group = groupData(g);
            }
            d.children.add(cd);
        }
        return d;
    }

    private static List<WorldSave.CaveData> caveData(World world) {
        List<WorldSave.CaveData> list = new ArrayList<>();
        for (CaveGeneratorInstance inst : world.getCaveInstances()) {
            WorldSave.CaveData d = new WorldSave.CaveData();
            d.desc = inst.desc;
            d.enabled = inst.enabled;
            d.type = inst.type;
            WorldSave.CACaveData ca = new WorldSave.CACaveData();
            ca.fill_percent = inst.caConfig.fillPercent;
            ca.iterations = inst.caConfig.iterations;
            ca.neighbor_threshold = inst.caConfig.neighborThreshold;
            d.ca_config = ca;
            WorldSave.NoiseCaveData noise = new WorldSave.NoiseCaveData();
            noise.scale_x = inst.noiseConfig.scaleX;
            noise.scale_y = inst.noiseConfig.scaleY;
            noise.lower_threshold = inst.noiseConfig.lowerThreshold;
            noise.upper_threshold = inst.noiseConfig.upperThreshold;
            d.noise_config = noise;
            WorldSave.DrunkardCaveData drunkard = new WorldSave.DrunkardCaveData();
            drunkard.walker_count = inst.drunkardConfig.walkerCount;
            drunkard.walker_steps = inst.drunkardConfig.steps;
            d.drunkard_config = drunkard;
            list.add(d);
        }
        return list;
    }

    private static List<WorldSave.DecorationData> decorationData(World world) {
        List<WorldSave.DecorationData> list = new ArrayList<>();
        for (DecorationInstance inst : world.getDecorationInstances()) {
            WorldSave.DecorationData d = new WorldSave.DecorationData();
            d.enabled = inst.enabled;
            d.desc = inst.decoration.desc;
            d.ascii_rows = inst.decoration.asciiRows;
            d.char_map = inst.decoration.charMap;
            d.allowed_biomes = inst.decoration.allowedBiomes;
            d.required_surface_tile = inst.decoration.requiredSurface;
            d.chance_to_spawn = inst.decoration.chance;
            d.placement_type = inst.decoration.placementType;
            list.add(d);
        }
        return list;
    }

    private static WorldSave.BiomeDistributionData biomeDistributionData(World world) {
        WorldSave.BiomeDistributionData d = new WorldSave.BiomeDistributionData();
        d.noise_scale = world.getBiomeGeneratorConfig().noiseScale;
        d.biomes = new ArrayList<>();
        for (BiomeEntry entry : world.getBiomeGeneratorConfig().biomes) {
            WorldSave.BiomeEntryData bed = new WorldSave.BiomeEntryData();
            bed.id = entry.biome.id;
            bed.weight = entry.weight;
            d.biomes.add(bed);
        }
        return d;
    }

    private static WorldSave.BiomeOverrideData biomeOverrideData(World world) {
        WorldSave.BiomeOverrideData d = new WorldSave.BiomeOverrideData();
        var c = world.getBiomeOverrideConfig();
        d.beach_width = c.beachWidth;
        d.mountain_height = c.mountainHeight;
        d.peak_height = c.peakHeight;
        return d;
    }

    private static List<WorldSave.SubstanceData> substanceData(World world) {
        List<WorldSave.SubstanceData> list = new ArrayList<>();
        for (SubstanceRule rule : world.getSubstanceRules()) {
            WorldSave.SubstanceData d = new WorldSave.SubstanceData();
            d.enabled = true;
            d.desc = rule.desc;
            d.output_tile = rule.output;
            d.replaces = rule.replaces;
            d.noise_scale_x = rule.noiseScaleX;
            d.noise_scale_y = rule.noiseScaleY;
            d.noise_threshold = rule.noiseThreshold;
            d.min_depth = rule.minDepth;
            d.max_depth = rule.maxDepth;
            d.requires_near_water = rule.requiresNearWater;
            list.add(d);
        }
        return list;
    }

    // ---- LOAD ----

    private static void fromSave(World world, WorldSave save) {
        // World
        world.getWorldConfig().seed = save.world.seed;
        world.getWorldConfig().width = save.world.width;
        world.getWorldConfig().height = save.world.height;

        // Water
        world.getWaterConfig().waterLevel = save.water.water_level;
        world.getWaterConfig().lakeMinWidth = save.water.lake_min_width;
        world.getWaterConfig().oceanMinWidth = save.water.ocean_min_width;
        world.getWaterConfig().pressurePerDepth = save.water.pressure_per_depth;
        world.getWaterConfig().upwardCost = save.water.upward_cost;
        world.getWaterConfig().minPressureToFlood = save.water.min_pressure_to_flood;

        // Heightmap
        var hc = world.getHeightmapConfig();
        hc.baseHeight = save.heightmaps.base_height;
        hc.minSubSurfaceDepth = save.heightmaps.min_subsurface_depth;
        hc.maxSubSurfaceDepth = save.heightmaps.max_subsurface_depth;
        hc.heightmapGroup = loadGroup(save.heightmaps.root_group);

        // Caves
        world.getCaveInstances().clear();
        for (WorldSave.CaveData cd : save.caves) {
            CaveGeneratorInstance inst = new CaveGeneratorInstance(cd.desc, cd.type);
            inst.enabled = cd.enabled;
            inst.caConfig.fillPercent = cd.ca_config.fill_percent;
            inst.caConfig.iterations = cd.ca_config.iterations;
            inst.caConfig.neighborThreshold = cd.ca_config.neighbor_threshold;
            inst.noiseConfig.scaleX = cd.noise_config.scale_x;
            inst.noiseConfig.scaleY = cd.noise_config.scale_y;
            inst.noiseConfig.lowerThreshold = cd.noise_config.lower_threshold;
            inst.noiseConfig.upperThreshold = cd.noise_config.upper_threshold;
            inst.drunkardConfig.walkerCount = cd.drunkard_config.walker_count;
            inst.drunkardConfig.steps = cd.drunkard_config.walker_steps;
            world.getCaveInstances().add(inst);
        }

        // Decorations
        world.getDecorationInstances().clear();
        for (WorldSave.DecorationData dd : save.decorations) {
            Decoration dec = new Decoration(dd.desc, dd.ascii_rows, dd.char_map,
                    dd.allowed_biomes, dd.required_surface_tile, dd.chance_to_spawn, dd.placement_type);
            world.getDecorationInstances().add(new DecorationInstance(dec, dd.enabled));
        }

        // Biome distribution
        world.getBiomeGeneratorConfig().noiseScale = save.biome_distribution.noise_scale;
        world.getBiomeGeneratorConfig().biomes.clear();
        for (WorldSave.BiomeEntryData bed : save.biome_distribution.biomes) {
            var biome = uk.bradleyjones.worldgenerator.world.biomes.Biome.getById(bed.id);
            if (biome != null)
                world.getBiomeGeneratorConfig().biomes.add(new BiomeEntry(biome, bed.weight));
        }

        // Biome overrides
        world.getBiomeOverrideConfig().beachWidth = save.biome_overrides.beach_width;
        world.getBiomeOverrideConfig().mountainHeight = save.biome_overrides.mountain_height;
        world.getBiomeOverrideConfig().peakHeight = save.biome_overrides.peak_height;

        // Substances
        world.getSubstanceRules().clear();
        for (WorldSave.SubstanceData sd : save.substances) {
            SubstanceRule rule = new SubstanceRule(sd.enabled, sd.desc, sd.output_tile, sd.replaces,
                    sd.noise_scale_x, sd.noise_scale_y, sd.noise_threshold,
                    sd.min_depth, sd.max_depth, sd.requires_near_water);
            world.getSubstanceRules().add(rule);
        }
    }

    private static HeightmapGroup loadGroup(WorldSave.HeightmapGroupData data) {
        HeightmapGroup group = new HeightmapGroup(data.combine_mode, 0);
        group.noiseScale = data.noise_scale;
        for (WorldSave.HeightmapChildData cd : data.children) {
            HeightmapNode node = switch (cd.type) {
                case "NOISE" -> new NoiseHeightmapGenerator(
                        cd.noise_config.scale, cd.noise_config.amplitude,
                        cd.noise_config.power, cd.noise_config.clamp_to_positive);
                case "STEP" -> new StepHeightmapGenerator(
                        cd.step_config.min_step_height, cd.step_config.max_step_height,
                        cd.step_config.min_step_gap, cd.step_config.max_step_gap);
                case "GROUP" -> loadGroup(cd.group);
                default -> throw new IllegalArgumentException("Unknown heightmap node type: " + cd.type);
            };
            HeightmapChild child = new HeightmapChild(node, cd.weight);
            child.enabled = cd.enabled;
            group.children.add(child);
        }
        return group;
    }
}