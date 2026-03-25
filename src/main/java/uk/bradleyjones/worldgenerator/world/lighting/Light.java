package uk.bradleyjones.worldgenerator.world.lighting;

import javafx.scene.paint.Color;

public class Light {
    public double intensity;
    public double normalizedIntensity;
    public Color color;
    public Light(double intensity, Color color, double normalizedIntensity) {
        this.intensity = intensity;
        this.normalizedIntensity = normalizedIntensity;
        this.color = color;
    }
    public Light(double intensity, Color color)
    {
        this.intensity = intensity;
        this.color = color;
    }
}