package uk.bradleyjones.worldgenerator.saving;

import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.decorations.PlacementType;
import uk.bradleyjones.worldgenerator.world.heightmap.CombineMode;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;

import java.util.List;
import java.util.Map;

public class WorldSave {
    public String name;
    public WorldData world;
    public WaterData water;
    public HeightmapData heightmaps;
    public List<CaveData> caves;
    public List<DecorationData> decorations;
    public BiomeDistributionData biome_distribution;
    public BiomeOverrideData biome_overrides;
    public List<SubstanceData> substances;

    public static class WorldData {
        public long seed;
        public int width;
        public int height;
    }

    public static class WaterData {
        public int water_level;
        public int lake_min_width;
        public int ocean_min_width;
        public float pressure_per_depth;
        public float upward_cost;
        public float min_pressure_to_flood;
    }

    public static class HeightmapData {
        public int base_height;
        public int min_subsurface_depth;
        public int max_subsurface_depth;
        public HeightmapGroupData root_group;
    }

    public static class HeightmapGroupData {
        public CombineMode combine_mode;
        public float noise_scale;
        public List<HeightmapChildData> children;
    }

    public static class HeightmapChildData {
        public float weight;
        public boolean enabled;
        public String type; // "NOISE", "STEP", "GROUP"
        public NoiseGeneratorData noise_config;
        public StepGeneratorData step_config;
        public HeightmapGroupData group;
    }

    public static class NoiseGeneratorData {
        public double scale;
        public double amplitude;
        public double power;
        public boolean clamp_to_positive;
    }

    public static class StepGeneratorData {
        public int min_step_height;
        public int max_step_height;
        public int min_step_gap;
        public int max_step_gap;
    }

    public static class CaveData {
        public String desc;
        public boolean enabled;
        public CaveGeneratorType type;
        public CACaveData ca_config;
        public NoiseCaveData noise_config;
        public DrunkardCaveData drunkard_config;
    }

    public static class CACaveData {
        public int fill_percent;
        public int iterations;
        public int neighbor_threshold;
    }

    public static class NoiseCaveData {
        public float scale_x;
        public float scale_y;
        public float lower_threshold;
        public float upper_threshold;
    }

    public static class DrunkardCaveData {
        public int walker_count;
        public int walker_steps;
    }

    public static class DecorationData {
        public boolean enabled;
        public String desc;
        public List<String> ascii_rows;
        public Map<Character, TileType> char_map;
        public List<String> allowed_biomes;
        public TileType required_surface_tile;
        public float chance_to_spawn;
        public PlacementType placement_type;
    }

    public static class BiomeDistributionData {
        public float noise_scale;
        public List<BiomeEntryData> biomes;
    }

    public static class BiomeEntryData {
        public String id;
        public float weight;
    }

    public static class BiomeOverrideData {
        public int beach_width;
        public int mountain_height;
        public int peak_height;
    }

    public static class SubstanceData {
        public boolean enabled;
        public String desc;
        public TileType output_tile;
        public List<TileType> replaces;
        public double noise_scale_x;
        public double noise_scale_y;
        public double noise_threshold;
        public int min_depth;
        public int max_depth;
        public boolean requires_near_water;
    }
}