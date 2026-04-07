package uk.bradleyjones.worldgenerator.world.substances;

import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.TileType;

import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceRule {
    public String desc;
    public TileType output;
    public List<TileType> replaces;
    public double noiseScaleX;
    public double noiseScaleY;
    public double noiseThreshold;
    public int minDepth;
    public int maxDepth;
    public boolean requiresNearWater;

    public SubstanceRule(String desc, TileType output, List<TileType> replaces, double noiseScaleX, double noiseScaleY, double noiseThreshold, int minDepth, int maxDepth, boolean requiresNearWater) {
        this.desc = desc;
        this.output = output;
        this.replaces = replaces;
        this.noiseScaleX = noiseScaleX;
        this.noiseScaleY = noiseScaleY;
        this.noiseThreshold = noiseThreshold;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
        this.requiresNearWater = requiresNearWater;
    }
    public SubstanceRule(SubstanceRule other) {
        this.desc = other.desc;
        this.output = other.output;
        this.replaces = new ArrayList<>(other.replaces);
        this.noiseScaleX = other.noiseScaleX;
        this.noiseScaleY = other.noiseScaleY;
        this.noiseThreshold = other.noiseThreshold;
        this.minDepth = other.minDepth;
        this.maxDepth = other.maxDepth;
        this.requiresNearWater = other.requiresNearWater;
    }
    public SubstanceRule() {

    }

    public boolean matches(int x, int y, TileType base, OpenSimplexNoise noise) {
        if (!replaces.contains(base)) return false;
        int depth = world.getDepthOfPosition(x, y);
        if (depth < minDepth || depth > maxDepth) return false;
        if (requiresNearWater && !world.isNearWater(x, y)) return false;
        double n = noise.getNoise2D(x * noiseScaleX, y * noiseScaleY).getValue();
        return n > noiseThreshold;
    }

    public static SubstanceRule COAL = new SubstanceRule("Coal", TileType.COAL_ORE, List.of(TileType.STONE), 4, 4, 0.4, 5, Integer.MAX_VALUE, false);
    public static SubstanceRule DIAMOND = new SubstanceRule("Diamond", TileType.DIAMOND_ORE, List.of(TileType.STONE), 3, 3, 0.6, 40, Integer.MAX_VALUE, false);
    public static SubstanceRule GRAVEL = new SubstanceRule("Gravel", TileType.GRAVEL, List.of(TileType.STONE, TileType.DIRT), 2, 2, 0.45, 2, Integer.MAX_VALUE, false);
    public static SubstanceRule CLAY = new SubstanceRule("Clay", TileType.CLAY, List.of(TileType.DIRT, TileType.SAND), 3, 3, 0.4, 0, 15, true);
    public static SubstanceRule COPPER = new SubstanceRule("Copper", TileType.COPPER_ORE, List.of(TileType.STONE), 3.5, 3.5, 0.45, 8, 40, false);
    public static SubstanceRule LAPIS = new SubstanceRule("Lapis", TileType.LAPIS_ORE, List.of(TileType.STONE), 3, 3, 0.55, 30, Integer.MAX_VALUE, false);
    public static SubstanceRule AMETHYST = new SubstanceRule("Amethyst", TileType.AMETHYST_ORE, List.of(TileType.STONE), 2.5, 2.5, 0.58, 35, Integer.MAX_VALUE, false);
    public static SubstanceRule RED_CLAY = new SubstanceRule("Red Clay", TileType.RED_CLAY, List.of(TileType.DIRT, TileType.SAND), 3, 3, 0.42, 0, 12, false);
    public static SubstanceRule QUARTZ = new SubstanceRule("Quartz", TileType.QUARTZ, List.of(TileType.STONE), 4, 1, 0.5, 25, Integer.MAX_VALUE, false);
    public static List<SubstanceRule> defaults() {
        return List.of(COAL,DIAMOND,GRAVEL,CLAY, COPPER, LAPIS, AMETHYST, RED_CLAY, QUARTZ);
    }


}