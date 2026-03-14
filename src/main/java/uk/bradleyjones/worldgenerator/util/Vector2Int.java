package uk.bradleyjones.worldgenerator.util;

public class Vector2Int {
    public int x;
    public int y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2Int other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void clamp(int minX, int minY, int maxX, int maxY) {
        this.x = Math.max(minX, Math.min(maxX, this.x));
        this.y = Math.max(minY, Math.min(maxY, this.y));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static Vector2Int UP = new Vector2Int(0, 1), DOWN = new Vector2Int(0,-1), LEFT = new Vector2Int(-1, 0), RIGHT = new Vector2Int(1,0);
}