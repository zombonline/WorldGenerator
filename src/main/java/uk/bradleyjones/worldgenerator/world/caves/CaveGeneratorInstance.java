
package uk.bradleyjones.worldgenerator.world.caves;

import com.github.weisj.jsvg.attributes.Default;

import java.util.List;

public class CaveGeneratorInstance {
    public String desc;
    public CaveGeneratorType type;
    public boolean enabled = true;
    public CACaveConfig caConfig = new CACaveConfig();
    public NoiseCaveConfig noiseConfig = new NoiseCaveConfig();
    public DrunkardCaveConfig drunkardConfig = new DrunkardCaveConfig();

    public CaveGeneratorInstance(String desc, CaveGeneratorType type) {
        this.desc = desc;
        this.type = type;
    }

    public CaveGeneratorInstance(CaveGeneratorInstance other) {
        this.desc = other.desc;
        this.type = other.type;
        this.enabled = other.enabled;
        this.caConfig = new CACaveConfig(other.caConfig);
        this.noiseConfig = new NoiseCaveConfig(other.noiseConfig);
        this.drunkardConfig = new DrunkardCaveConfig(other.drunkardConfig);
    }

    public CaveConfig getConfig() {
        return switch (type) {
            case CA -> caConfig;
            case NOISE -> noiseConfig;
            case DRUNKARD -> drunkardConfig;
        };
    }

    public static final CaveGeneratorInstance DEFAULT_CA = new CaveGeneratorInstance("Default CA", CaveGeneratorType.CA);
    public static final CaveGeneratorInstance DEFAULT_NOISE = new CaveGeneratorInstance("Default Noise", CaveGeneratorType.NOISE);
    public static final CaveGeneratorInstance DEFAULT_DRUNKARD = new CaveGeneratorInstance("Default Drunkard", CaveGeneratorType.DRUNKARD);
    public static final CaveGeneratorInstance SPAGHETTI;
    static {
        SPAGHETTI = new CaveGeneratorInstance("Spaghetti", CaveGeneratorType.NOISE);
        SPAGHETTI.noiseConfig.scaleX = 1;
        SPAGHETTI.noiseConfig.scaleY = 1;
        SPAGHETTI.noiseConfig.lowerThreshold = -0.05f;
        SPAGHETTI.noiseConfig.upperThreshold = 0.05f;
    }


    public static List<CaveGeneratorInstance> defaults() {
        return List.of(DEFAULT_CA, DEFAULT_NOISE, DEFAULT_DRUNKARD, SPAGHETTI);
    }

}