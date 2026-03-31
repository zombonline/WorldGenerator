package uk.bradleyjones.worldgenerator.world.heightmap;

import com.raylabz.opensimplex.OpenSimplexNoise;

import java.util.ArrayList;
import java.util.List;

public class HeightmapGroup implements HeightmapNode {

    public CombineMode mode;
    public float noiseScale;
    public List<HeightmapChild> children = new ArrayList<>();
    private OpenSimplexNoise blendNoise;

    public HeightmapGroup(CombineMode mode, long seed) {
        this.mode = mode;
        this.noiseScale = 0.005f;
        this.blendNoise = new OpenSimplexNoise(seed);
    }

    public void refreshSeed(long seed) {
        this.blendNoise = new OpenSimplexNoise(seed);
        for (HeightmapChild child : children) {
            child.node.refreshSeed(seed);
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
            case HIGHEST -> {
                int max = Integer.MIN_VALUE;
                for (HeightmapChild child : active) max = Math.max(max, child.node.getHeight(x));
                yield max;
            }
            case LOWEST -> {
                int min = Integer.MAX_VALUE;
                for (HeightmapChild child : active) min = Math.min(min, child.node.getHeight(x));
                yield min;
            }
            case AVERAGE -> {
                int sum = 0;
                for (HeightmapChild child : active) sum += child.node.getHeight(x);
                yield sum / active.size();
            }
            case NOISE_BLEND -> {
                // Normalise noise to 0..1
                double blendValue = (blendNoise.getNoise2D(x * noiseScale, 0).getValue() + 1) / 2.0;

                // Calculate total weight
                float totalWeight = 0;
                for (HeightmapChild child : active) totalWeight += child.weight;

                // Walk children and interpolate
                float cursor = 0;
                for (int i = 0; i < active.size() - 1; i++) {
                    float segStart = cursor / totalWeight;
                    float segEnd = (cursor + active.get(i).weight) / totalWeight;
                    if (blendValue <= segEnd) {
                        double t = (blendValue - segStart) / (segEnd - segStart);
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