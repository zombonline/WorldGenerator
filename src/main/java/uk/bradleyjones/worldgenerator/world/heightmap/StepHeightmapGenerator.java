package uk.bradleyjones.worldgenerator.world.heightmap;

import java.util.Objects;
import java.util.Random;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class StepHeightmapGenerator implements HeightmapNode{

    private int minStepHeight, maxStepHeight, minStepGap, maxStepGap;
    private int[] heightMap;
    public StepHeightmapGenerator(int minStepHeight, int maxStepHeight, int minStepGap, int maxStepGap) {
        this.minStepHeight = minStepHeight;
        this.maxStepHeight = maxStepHeight;
        this.minStepGap = minStepGap;
        this.maxStepGap = maxStepGap;
    }

    @Override
    public int getHeight(int x) {
        if (heightMap == null) return 0;
        return heightMap[x];
    }

    @Override
    public void regenerate() {
        heightMap = new int[world.getWorldConfig().width];
        long configHash = Objects.hash(minStepHeight, maxStepHeight, minStepGap, maxStepGap);
        Random random = new Random(world.getWorldConfig().seed ^ configHash);
        int maxDrift = world.getWorldConfig().height / 4;
        int currentOffset = 0;
        int x = 0;
        while (x < heightMap.length) {
            int stepSize = random.nextInt(maxStepHeight - minStepHeight + 1) + minStepHeight;
            int dir = random.nextBoolean() ? 1 : -1;
            currentOffset += stepSize * dir;
            currentOffset = Math.max(-maxDrift, Math.min(currentOffset, maxDrift));
            int gap = random.nextInt(maxStepGap - minStepGap + 1) + minStepGap;
            for (int i = 0; i < gap && x < world.getWorldConfig().width; i++, x++) {
                heightMap[x] = currentOffset;
            }
        }
    }

    public int getMinStepHeight() {
        return minStepHeight;
    }

    public void setMinStepHeight(int minStepHeight) {
        this.minStepHeight = minStepHeight;
    }

    public int getMaxStepHeight() {
        return maxStepHeight;
    }

    public void setMaxStepHeight(int maxStepHeight) {
        this.maxStepHeight = maxStepHeight;
    }

    public int getMinStepGap() {
        return minStepGap;
    }

    public void setMinStepGap(int minStepGap) {
        this.minStepGap = minStepGap;
    }

    public int getMaxStepGap() {
        return maxStepGap;
    }

    public void setMaxStepGap(int maxStepGap) {
        this.maxStepGap = maxStepGap;
    }
}
