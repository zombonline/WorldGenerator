package uk.bradleyjones.worldgenerator.world.water;

public class WaterNode {
    public int x, y;
    public float pressure;

    public WaterNode(int x, int y, float pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
}