package uk.bradleyjones.worldgenerator.world.caves;


import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.World;

public class NoiseCaveGenerator extends CaveGenerator{
    private NoiseCaveConfig config;
    private OpenSimplexNoise noise;
    private World world;
    public NoiseCaveGenerator(int seed, NoiseCaveConfig config, World world) {
        super(config, world);
        this.config = config;
        noise = new OpenSimplexNoise(seed);
    }
    @Override
    public boolean isCave(int x, int y) {
        double val = noise.getNoise2D(x * config.scaleX, y * config.scaleY).getValue();
        return (val > config.lowerThreshold &&  val < config.upperThreshold) && super.isCave(x,y);
    }
}
