package uk.bradleyjones.worldgenerator.world.lighting;

class LightNode {
    int x, y, dx, dy;
    Light light;
    public LightNode(int x, int y, int dx, int dy, Light light) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.light = light;
    }
}