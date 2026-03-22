package uk.bradleyjones.worldgenerator.world.biomes;

import java.util.ArrayList;
import java.util.List;

public class BiomeGeneratorConfig {
    public float noiseScale = 0.05f;
    public List<BiomeEntry> biomes = new ArrayList<>();

    public BiomeGeneratorConfig() {
        // Defaults
        biomes.add(new BiomeEntry(Biome.DESERT, 1f));
        biomes.add(new BiomeEntry(Biome.PLAINS, 1f));
        biomes.add(new BiomeEntry(Biome.FOREST, 1f));
        biomes.add(new BiomeEntry(Biome.TUNDRA, 1f));
    }
}