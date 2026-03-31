package uk.bradleyjones.worldgenerator.world.caves;


import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.World;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class NoiseCaveGenerator extends CaveGenerator{
    private NoiseCaveConfig config;
    private OpenSimplexNoise noise;
    public NoiseCaveGenerator(NoiseCaveConfig config) {
        super(config);
        this.config = config;
        noise = new OpenSimplexNoise(world.getWorldConfig().seed);
    }
    @Override
    public boolean isCave(int x, int y) {
        double val = noise.getNoise2D(x * config.scaleX, y * config.scaleY).getValue();
        return (val > config.lowerThreshold &&  val < config.upperThreshold) && super.isCave(x,y);
    }
}
