package uk.bradleyjones.worldgenerator.world.caves;


import com.raylabz.opensimplex.OpenSimplexNoise;

public class NoiseCaveGenerator implements CaveGenerator{
    private int width, height, seed;
    private NoiseCaveConfig config;
    private OpenSimplexNoise noise;
    public NoiseCaveGenerator(int width, int height, int seed, NoiseCaveConfig config) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.config = config;
        noise = new OpenSimplexNoise(seed);
    }
    @Override
    public boolean isCave(int x, int y) {
        double val = noise.getNoise2D(x * config.scaleX, y * config.scaleY).getValue();
        return (val > config.lowerThreshold &&  val < config.upperThreshold);
    }
}
