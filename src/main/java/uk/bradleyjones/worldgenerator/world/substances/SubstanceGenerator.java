package uk.bradleyjones.worldgenerator.world.substances;

import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.TileType;

import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceGenerator {

    private final List<SubstanceRule> rules;
    private final List<OpenSimplexNoise> noiseInstances;

    public SubstanceGenerator(List<SubstanceRule> rules) {
        long seed = world.getWorldConfig().seed;
        this.rules = rules;
        this.noiseInstances = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            noiseInstances.add(new OpenSimplexNoise(seed + (i + 1) * 1000L));
        }
    }

    public TileType getOverride(int x, int y, TileType base) {

        for (int i = 0; i < rules.size(); i++) {
            SubstanceRule rule = rules.get(i);
            OpenSimplexNoise noise = noiseInstances.get(i);
            if (rule.matches(x, y, base, noise)) return rule.output;
        }
        return base;
    }
}