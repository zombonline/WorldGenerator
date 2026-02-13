package uk.bradleyjones.worldgenerator.render;

public class Camera {
    private double x;
    private double y;
    private double zoom;

    public Camera() {
        this.x = 0;
        this.y = 0;
        this.zoom = 1;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        System.out.println("Zoom set to: " + zoom);
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }
}
