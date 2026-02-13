package uk.bradleyjones.worldgenerator.render;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Camera {
    private double x;
    private double y;
    private double zoom;

    private final List<CameraListener> listeners = new ArrayList<>();


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
        notifyUpdated();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        notifyUpdated();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        System.out.println("Zoom set to: " + zoom);
        notifyUpdated();
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
        notifyUpdated();
    }

    // ---- listener management ----

    public void addListener(CameraListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CameraListener listener) {
        listeners.remove(listener);
    }

    private void notifyUpdated() {
        for (CameraListener l : listeners) {
            l.onCameraUpdated(this);
        }
    }
}
