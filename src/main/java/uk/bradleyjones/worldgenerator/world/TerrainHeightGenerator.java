package uk.bradleyjones.worldgenerator.world;

import com.raylabz.opensimplex.OpenSimplexNoise;

public class TerrainHeightGenerator {

    private final OpenSimplexNoise noiseA;
    private final OpenSimplexNoise noiseB;
    private final OpenSimplexNoise noiseC;

    private int baseHeight = 100;
    private int worldHeight;

    private double scaleA = 0.1;
    private double scaleB = 0.4;
    private double scaleC = 0.1;

    private double ampA = 64;
    private double ampB = 32;
    private double ampC = 3000;

    public TerrainHeightGenerator(long seed, int worldHeight) {
        noiseA = new OpenSimplexNoise(seed);
        noiseB = new OpenSimplexNoise(seed + 1);
        noiseC = new OpenSimplexNoise(seed + 2);
        this.worldHeight = worldHeight;
    }

    public int getHeight(int x) {

        double h1 = noiseA.getNoise2D(x * scaleA, 0).getValue();
        double h2 = noiseB.getNoise2D(x * scaleB, 0).getValue();
        double h3 = noiseC.getNoise2D(x * scaleC, 0).getValue();

        double base = h1 * ampA;
        double detail = h2 * ampB * 0.3;
        double mountainMask = Math.max(0, h3);
        double mountains = Math.pow(mountainMask, 4) * -ampC;

        return baseHeight + (int)(base + detail + mountains);
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
    }


    public void setScaleA(double scaleA) { this.scaleA = scaleA; }
    public void setScaleB(double scaleB) { this.scaleB = scaleB; }
    public void setScaleC(double scaleC) { this.scaleC = scaleC; }
    public void setAmpA(double ampA) { this.ampA = ampA; }
    public void setAmpB(double ampB) { this.ampB = ampB; }
    public void setAmpC(double ampC) { this.ampC = ampC; }
    public double getScaleA() { return scaleA; }
    public double getScaleB() { return scaleB; }
    public double getScaleC() { return scaleC; }
    public double getAmpA() { return ampA; }
    public double getAmpB() { return ampB; }
    public double getAmpC() { return ampC; }

    public void setBaseHeight(int baseHeight) {
         this.baseHeight = baseHeight;
    }

    public int getBaseHeight() {return baseHeight;}
}