package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.world.World;

import javax.swing.*;
import java.io.IOError;

public class WorldGeneratorController {
    @FXML
    private Pane canvasPane;
    @FXML
    private Canvas worldCanvas;

    private World world;
    private WorldRenderer renderer;
    private Camera camera;

    @FXML
    public void initialize() {
        world = new World();
        renderer = new WorldRenderer();
        camera = new Camera();

        // Bind canvas size to the parent Pane
        worldCanvas.widthProperty().bind(canvasPane.widthProperty());
        worldCanvas.heightProperty().bind(canvasPane.heightProperty());

        // Redraw whenever the canvas resizes
        worldCanvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
        worldCanvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());

        // draw initial world
        draw();
    }


    public void draw(){
        GraphicsContext gc = worldCanvas.getGraphicsContext2D();

        // clear background
        gc.setFill(javafx.scene.paint.Color.LIGHTBLUE);
        gc.fillRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());

        renderer.render(gc, world, camera, worldCanvas.getWidth(), worldCanvas.getHeight());
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        double speed = 1f;

        switch (keyEvent.getCode()) {
            case A -> camera.move(-speed, 0);
            case D -> camera.move(speed, 0);
            case W -> camera.move(0, -speed);
            case S -> camera.move(0, speed);
        }
        draw();
    }

    public void handleScroll(ScrollEvent e) {
        double zoomSpeed = 0.1;
        float newZoom = (float) (camera.getZoom() + (e.getDeltaY() > 0 ? zoomSpeed : -zoomSpeed));
        //clamp zoom
        newZoom = Math.max(0.1f, Math.min(5f, newZoom));
        camera.setZoom(newZoom);
        System.out.println(camera.getZoom());
        draw();
    }
}
