package uk.bradleyjones.worldgenerator.input;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import uk.bradleyjones.worldgenerator.WorldGeneratorController;
import uk.bradleyjones.worldgenerator.render.Camera;

public class InputHandler {

    private double moveX = 0;
    private double moveY = 0;
    private final double moveSpeed = 5;

    private final Camera camera;

    public InputHandler() {
        camera = WorldGeneratorController.camera;

        // Start animation timer for smooth updates
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (moveX != 0 || moveY != 0) {
                    camera.move(moveX, moveY);
                }
            }
        };
        timer.start();
    }

    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case A -> moveX = -moveSpeed;
            case D -> moveX = moveSpeed;
            case W -> moveY = -moveSpeed;
            case S -> moveY = moveSpeed;
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case A, D -> moveX = 0;
            case W, S -> moveY = 0;
        }
    }

    public void handleScroll(ScrollEvent event) {
        double zoomSpeed = 0.1;
        double newZoom = camera.getZoom() + (event.getDeltaY() > 0 ? zoomSpeed : -zoomSpeed);
        newZoom = Math.max(0.5, Math.min(5.0, newZoom));
        camera.setZoom(newZoom);
    }
}
