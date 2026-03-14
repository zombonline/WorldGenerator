package uk.bradleyjones.worldgenerator.world.caves;

public class CaveGeneratorInstance {
    public CaveGeneratorType type;
    public boolean enabled = true;
    public CACaveConfig caConfig = new CACaveConfig();
    public NoiseCaveConfig noiseConfig = new NoiseCaveConfig();
    public DrunkardCaveConfig drunkardConfig = new DrunkardCaveConfig();

    public CaveGeneratorInstance(CaveGeneratorType type) {
        this.type = type;
    }
}