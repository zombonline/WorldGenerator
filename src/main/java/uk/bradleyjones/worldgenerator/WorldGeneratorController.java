package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.CameraListener;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.world.World;

public class WorldGeneratorController implements CameraListener {
    @FXML
    public TextField noiseScaleAInput;
    @FXML
    public TextField noiseScaleBInput;
    @FXML
    public TextField noiseScaleCInput;
    @FXML
    public TextField noiseAmplitudeAInput;
    @FXML
    public TextField noiseAmplitudeBInput;
    @FXML
    public TextField noiseAmplitudeCInput;
    @FXML
    private Pane canvasPane;
    @FXML
    private Canvas worldCanvas;
    @FXML
    private TextField seedInput;
    @FXML
    private Button regenButton;

    public static World world;
    public static WorldRenderer renderer;
    public static Camera camera;

    @FXML
    public void initialize() {
        world = new World();
        renderer = new WorldRenderer();
        camera = new Camera();
        camera.addListener(this);

        // Bind canvas size to the parent Pane
        worldCanvas.widthProperty().bind(canvasPane.widthProperty());
        worldCanvas.heightProperty().bind(canvasPane.heightProperty());

        // Redraw whenever the canvas resizes
        worldCanvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
        worldCanvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());


        // Set text fields to current world parameters
        seedInput.setText(String.valueOf(world.getSeed()));
        noiseScaleAInput.setText(String.valueOf(world.terrainHeightGenerator.getScaleA()));
        noiseScaleBInput.setText(String.valueOf(world.terrainHeightGenerator.getScaleB()));
        noiseScaleCInput.setText(String.valueOf(world.terrainHeightGenerator.getScaleC()));
        noiseAmplitudeAInput.setText(String.valueOf(world.terrainHeightGenerator.getAmpA()));
        noiseAmplitudeBInput.setText(String.valueOf(world.terrainHeightGenerator.getAmpB()));
        noiseAmplitudeCInput.setText(String.valueOf(world.terrainHeightGenerator.getAmpC()));

        // Set button action
        regenButton.setOnAction(e -> handleInitializeWorld());

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


    public void handleInitializeWorld() {
        try {
            world.setSeed(Integer.parseInt(seedInput.getText()));

            world.terrainHeightGenerator.setAmpA(Double.parseDouble(noiseAmplitudeAInput.getText()));
            world.terrainHeightGenerator.setAmpB(Double.parseDouble(noiseAmplitudeBInput.getText()));
            world.terrainHeightGenerator.setAmpC(Double.parseDouble(noiseAmplitudeCInput.getText()));

            world.terrainHeightGenerator.setScaleA(Double.parseDouble(noiseScaleAInput.getText()));
            world.terrainHeightGenerator.setScaleB(Double.parseDouble(noiseScaleBInput.getText()));
            world.terrainHeightGenerator.setScaleC(Double.parseDouble(noiseScaleCInput.getText()));
            draw();
        } catch (NumberFormatException e) {
            // If the input is not a valid integer, reset it to the current seed
            seedInput.setText(String.valueOf(world.getSeed()));
        }
    }

    @Override
    public void onCameraUpdated(Camera camera) {
        draw();
    }
}
