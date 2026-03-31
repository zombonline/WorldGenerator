package uk.bradleyjones.worldgenerator.world.heightmap;

public class HeightmapGeneratorInstance extends HeightmapChild {

    public HeightmapGeneratorType type;

    public NoiseHeightmapGenerator noiseGenerator;
    public StepHeightmapGenerator stepGenerator;

    public HeightmapGeneratorInstance(HeightmapGeneratorType type, long seed) {
        super(null);
        this.type = type;
        this.noiseGenerator = new NoiseHeightmapGenerator(0.1, 64, 1.0, false);
        this.stepGenerator = new StepHeightmapGenerator(1, 5, 1, 5);
        this.node = resolveNode();
    }

    private HeightmapNode resolveNode() {
        return switch (type) {
            case NOISE -> noiseGenerator;
            case STEPS -> stepGenerator;
        };
    }

    public void setType(HeightmapGeneratorType newType) {
        this.type = newType;
        this.node = resolveNode();
    }
}
