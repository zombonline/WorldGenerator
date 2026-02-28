package uk.bradleyjones.worldgenerator.world;

import com.raylabz.opensimplex.OpenSimplexNoise;

public class TerrainHeightGenerator {

    private final OpenSimplexNoise noiseA;
    private final OpenSimplexNoise noiseB;
    private final OpenSimplexNoise noiseC;

    // Exposed parameters
    private int baseHeight = 20;

    private double scaleA = 0.1;   // large hills
    private double scaleB = 0.4;   // small detail
    private double scaleC = 0.1;  // rare extremes

    private double ampA = 64;
    private double ampB = 32;
    private double ampC = 3000;

    public TerrainHeightGenerator(long seed) {
        noiseA = new OpenSimplexNoise(seed);
        noiseB = new OpenSimplexNoise(seed + 1);
        noiseC = new OpenSimplexNoise(seed + 2);
    }

    public int getHeight(int x) {
        double h1 = noiseA.getNoise2D(x * scaleA, 0).getValue();
        double h2 = noiseB.getNoise2D(x * scaleB, 0).getValue();
        double h3 = noiseC.getNoise2D(x * scaleC, 0).getValue();

        // h1 drives broad gentle terrain (-1 to 1, low frequency)
        double base = h1 * ampA;

        // h2 adds small surface detail, scaled down so it doesn't dominate
        double detail = h2 * ampB * 0.3;

        // h3 acts as a multiplier - only amplifies terrain where it's positive
        // this means mountains only appear where h3 "permits" them
        double mountainMask = Math.max(0, h3);
        double mountains = Math.pow(mountainMask, 4) * -ampC;

        return baseHeight + (int)(base + detail + mountains);
    }

    // ---- setters for UI later ----

    public void setBaseHeight(int baseHeight) {
        this.baseHeight = baseHeight;
    }

    public void setScaleA(double scaleA) {
        this.scaleA = scaleA;
    }

    public void setScaleB(double scaleB) {
        this.scaleB = scaleB;
    }

    public void setScaleC(double scaleC) {
        this.scaleC = scaleC;
    }

    public void setAmpA(double ampA) {
        this.ampA = ampA;
    }

    public void setAmpB(double ampB) {
        this.ampB = ampB;
    }

    public void setAmpC(double ampC) {
        this.ampC = ampC;
    }

    //getters

    public double getScaleA() {
        return scaleA;
    }

    public double getScaleB() {
        return scaleB;
    }

    public double getScaleC() {
        return scaleC;
    }

    public double getAmpA() {
        return ampA;
    }

    public double getAmpB() {
        return ampB;
    }

    public double getAmpC() {
        return ampC;
    }
}
