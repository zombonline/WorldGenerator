package uk.bradleyjones.worldgenerator.world.heightmap;


public class HeightmapConfig {
    public int baseHeight = 100;
    public int minSubSurfaceDepth = 2;
    public int maxSubSurfaceDepth = 30;

    public HeightmapGroup heightmapGroup;

    public HeightmapConfig(long seed) {
        heightmapGroup = new HeightmapGroup(CombineMode.ADDITIVE, seed);
    }
}