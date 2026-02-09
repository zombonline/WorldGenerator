package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.world.World;

public class WorldGeneratorController {
    @FXML
    private Pane canvasPane;
    @FXML
    private Canvas worldCanvas;

    private World world;
    private WorldRenderer renderer;

    @FXML
    public void initialize() {
        world = new World();
        renderer = new WorldRenderer();

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

        renderer.render(gc, world, worldCanvas.getWidth(), worldCanvas.getHeight());
    }

}
