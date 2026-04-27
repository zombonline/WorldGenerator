package uk.bradleyjones.worldgenerator.world.heightmap;

import com.raylabz.opensimplex.OpenSimplexNoise;
import uk.bradleyjones.worldgenerator.world.World;

import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class HeightmapGroup implements HeightmapNode {

    public CombineMode mode;
    public float noiseScale;
    public float blendSharpness = 0.2f;
    public List<HeightmapChild> children = new ArrayList<>();
    private OpenSimplexNoise blendNoise;

    public HeightmapGroup(CombineMode mode, long seed) {
        this.mode = mode;
        this.noiseScale = 0.005f;
        this.blendNoise = new OpenSimplexNoise(seed);
    }

    public void regenerate() {
        this.blendNoise = new OpenSimplexNoise(world.getWorldConfig().seed);
        for (HeightmapChild child : children) {
            child.node.regenerate();
        }
    }

    public void add(HeightmapNode node) {
        children.add(new HeightmapChild(node));
    }

    public void add(HeightmapNode node, float weight) {
        children.add(new HeightmapChild(node, weight));
    }

    @Override
    public int getHeight(int x) {
        List<HeightmapChild> active = children.stream()
                .filter(c -> c.enabled)
                .toList();

        if (active.isEmpty()) return 0;

        return switch (mode) {
            case ADDITIVE -> {
                int sum = 0;
                for (HeightmapChild child : active) sum += child.node.getHeight(x);
                yield sum;
            }
            // This looks inverted but javafx is set up that larger y means lower on the screen
            case LOWEST -> {
                int min = Integer.MIN_VALUE;
                for (HeightmapChild child : active) min = Math.max(min, child.node.getHeight(x));
                yield min;
            }
            case HIGHEST -> {
                int max = Integer.MAX_VALUE;
                for (HeightmapChild child : active) max = Math.min(max, child.node.getHeight(x));
                yield max;
            }
            case AVERAGE -> {
                int sum = 0;
                for (HeightmapChild child : active) sum += child.node.getHeight(x);
                yield sum / active.size();
            }
            case NOISE_BLEND -> {
                double blendValue = (blendNoise.getNoise2D(x * noiseScale, 0).getValue() + 1) / 2.0;

                float totalWeight = 0;
                for (HeightmapChild child : active) totalWeight += child.weight;

                // blendsharpness of 1 gives a complete linear blend, lower value means a more harsh cut closer to the edges.
                float cursor = 0;
                for (int i = 0; i < active.size() - 1; i++) {
                    float segStart = cursor / totalWeight;
                    float segEnd = (cursor + active.get(i).weight) / totalWeight;
                    if (blendValue <= segEnd) {
                        double t = (blendValue - segStart) / (segEnd - segStart);
                        t = Math.clamp((t - (1 - blendSharpness) / 2.0) / blendSharpness, 0.0, 1.0);
                        yield (int)(active.get(i).node.getHeight(x) * (1 - t) +
                                active.get(i + 1).node.getHeight(x) * t);
                    }
                    cursor += active.get(i).weight;
                }
                yield active.get(active.size() - 1).node.getHeight(x);
            }
        };
    }
}
