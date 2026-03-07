package uk.bradleyjones.worldgenerator.world;

import com.raylabz.opensimplex.OpenSimplexNoise;

public class TerrainHeightGenerator {

    private final OpenSimplexNoise noiseA;
    private final OpenSimplexNoise noiseB;
    private final OpenSimplexNoise noiseC;

    private final TerrainConfig config;
    private int worldHeight;

    public TerrainHeightGenerator(long seed, int worldHeight, TerrainConfig config) {
        this.config = config;
        this.worldHeight = worldHeight;
        noiseA = new OpenSimplexNoise(seed);
        noiseB = new OpenSimplexNoise(seed + 1);
        noiseC = new OpenSimplexNoise(seed + 2);
    }

    public int getHeight(int x) {
        int clampedBase = worldHeight - Math.min(config.baseHeight, worldHeight - 1);
        double h1 = noiseA.getNoise2D(x * config.scaleA, 0).getValue();
        double h2 = noiseB.getNoise2D(x * config.scaleB, 0).getValue();
        double h3 = noiseC.getNoise2D(x * config.scaleC, 0).getValue();

        double base = h1 * config.ampA;
        double detail = h2 * config.ampB * 0.3;
        double mountainMask = Math.max(0, h3);
        double mountains = Math.pow(mountainMask, 4) * -config.ampC;

        return clampedBase + (int)(base + detail + mountains);
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }
}