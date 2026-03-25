package uk.bradleyjones.worldgenerator.world.caves;

import uk.bradleyjones.worldgenerator.world.World;

public abstract class CaveGenerator {
    public boolean effectsSurface;
    protected final World world;
    public CaveGenerator(CaveConfig config, World world) {
        effectsSurface = config.effectsSurface;
        this.world = world;
    }

    public boolean isCave(int x, int y)
    {
        if(!effectsSurface)
            return world.getDepthOfPosition(x,y) > world.getSubSurfaceDepth(x);
        return true;
    };
}