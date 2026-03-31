package uk.bradleyjones.worldgenerator.world.heightmap;

public interface HeightmapNode {
    int getHeight(int x);
    void regenerate();
}