package uk.bradleyjones.worldgenerator.world.heightmap;

public class HeightmapChild {
    public HeightmapNode node;
    public float weight;
    public boolean enabled;

    public HeightmapChild(HeightmapNode node, float weight) {
        this.node = node;
        this.weight = weight;
        this.enabled = true;
    }

    public HeightmapChild(HeightmapNode node) {
        this(node, 1.0f);
    }
}