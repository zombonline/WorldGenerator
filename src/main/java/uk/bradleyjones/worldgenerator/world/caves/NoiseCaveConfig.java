package uk.bradleyjones.worldgenerator.world.caves;

public class NoiseCaveConfig extends CaveConfig {
    public float scaleX = 1f;
    public float scaleY = 1f;
    public float lowerThreshold = 0.2f;
    public float upperThreshold = 0.6f;

    public NoiseCaveConfig(NoiseCaveConfig config) {
        this.scaleX = config.scaleX;
        this.scaleY = config.scaleY;
        this.lowerThreshold = config.lowerThreshold;
        this.upperThreshold = config.upperThreshold;
    }
    public NoiseCaveConfig(){}
}
