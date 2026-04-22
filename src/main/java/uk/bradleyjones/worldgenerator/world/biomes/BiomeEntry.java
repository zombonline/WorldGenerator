package uk.bradleyjones.worldgenerator.world.biomes;

public class BiomeEntry {
    public Biome biome;
    public float weight;

    public BiomeEntry(Biome biome, float weight) {
        this.biome = biome;
        this.weight = weight;
    }
}