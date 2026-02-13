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
    private double ampC = 1280;

    public TerrainHeightGenerator(long seed) {
        noiseA = new OpenSimplexNoise(seed);
        noiseB = new OpenSimplexNoise(seed + 1);
        noiseC = new OpenSimplexNoise(seed + 2);
    }

    public int getHeight(int x) {
        double h1 = noiseA.getNoise2D(x * scaleA, 0).getValue();
        double h2 = noiseB.getNoise2D(x * scaleB, 0).getValue();
        double h3 = noiseC.getNoise2D(x * scaleC, 0).getValue();

        // Shape extremes so they're rare
        double extreme = Math.pow(Math.max(0, h3), 3);

        return baseHeight
                + (int)(h1 * ampA)
                + (int)(h2 * ampB)
                + (int)(extreme * -ampC);
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
