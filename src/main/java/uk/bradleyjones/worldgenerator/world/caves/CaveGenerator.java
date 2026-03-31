package uk.bradleyjones.worldgenerator.world.caves;

import uk.bradleyjones.worldgenerator.world.World;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public abstract class CaveGenerator {
    public boolean effectsSurface;
    public CaveGenerator(CaveConfig config) {
        effectsSurface = config.effectsSurface;
    }

    public boolean isCave(int x, int y)
    {
        if(!effectsSurface)
            return world.getDepthOfPosition(x,y) > world.getSubSurfaceDepth(x);
        return true;
    };
}