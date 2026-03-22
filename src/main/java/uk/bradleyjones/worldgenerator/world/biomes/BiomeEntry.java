package uk.bradleyjones.worldgenerator.world.biomes;

public class BiomeEntry {
    public Biome type;
    public float weight;

    public BiomeEntry(Biome type, float weight) {
        this.type = type;
        this.weight = weight;
    }
}