package uk.bradleyjones.worldgenerator.world.heightmap;

import com.raylabz.opensimplex.OpenSimplexNoise;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class NoiseHeightmapGenerator implements HeightmapNode {

    private OpenSimplexNoise noise;
    private double scale;
    private double amplitude;
    private double power;
    private boolean clampToPositive;

    public NoiseHeightmapGenerator(double scale, double amplitude, double power, boolean clampToPositive) {
        this.noise = new OpenSimplexNoise(world.getWorldConfig().seed);
        this.scale = scale;
        this.amplitude = amplitude;
        this.power = power;
        this.clampToPositive = clampToPositive;
    }

    @Override
    public int getHeight(int x) {
        double value = noise.getNoise2D(x * scale, 0).getValue();
        if (clampToPositive) value = Math.max(0, value);
        return (int)(Math.pow(value, power) * amplitude);
    }

    public void regenerate() {
        this.noise = new OpenSimplexNoise(world.getWorldConfig().seed);
    }

    // Getters and setters for UI
    public double getScale() { return scale; }
    public double getAmplitude() { return amplitude; }
    public double getPower() { return power; }
    public boolean isClampToPositive() { return clampToPositive; }
    public void setScale(double scale) { this.scale = scale; }
    public void setAmplitude(double amplitude) { this.amplitude = amplitude; }
    public void setPower(double power) { this.power = power; }
    public void setClampToPositive(boolean clampToPositive) { this.clampToPositive = clampToPositive; }
}