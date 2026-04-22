package uk.bradleyjones.worldgenerator.world.caves;

public class DrunkardCaveConfig extends CaveConfig {
    public int walkerCount, steps;

    public DrunkardCaveConfig(DrunkardCaveConfig config) {
        this.walkerCount = config.walkerCount;
        this.steps = config.steps;
    }
    public DrunkardCaveConfig(){}
}
