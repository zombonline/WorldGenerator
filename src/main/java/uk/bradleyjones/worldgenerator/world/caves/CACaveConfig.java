package uk.bradleyjones.worldgenerator.world.caves;

public class CACaveConfig extends CaveConfig {
    public int fillPercent = 77;
    public int iterations = 5;
    public int neighborThreshold = 6;

    public CACaveConfig(CACaveConfig config) {
        this.fillPercent = config.fillPercent;
        this.iterations = config.iterations;
        this.neighborThreshold = config.neighborThreshold;
    }
    public CACaveConfig(){}
}