package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.CameraListener;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.world.World;

public class WorldGeneratorController implements CameraListener {
    @FXML public TextField noiseScaleAInput;
    @FXML public TextField noiseScaleBInput;
    @FXML public TextField noiseScaleCInput;
    @FXML public TextField noiseAmplitudeAInput;
    @FXML public TextField noiseAmplitudeBInput;
    @FXML public TextField noiseAmplitudeCInput;
    @FXML public TextField worldWidthInput;
    @FXML public TextField worldHeightInput;
    @FXML public TextField waterLevelInput;
    @FXML public TextField baseHeightInput;
    @FXML private Pane canvasPane;
    @FXML private Canvas worldCanvas;
    @FXML private TextField seedInput;
    @FXML private Button regenButton;

    public static World world;
    public static WorldRenderer renderer;
    public static Camera camera;

    @FXML
    public void initialize() {
        world = new World();
        renderer = new WorldRenderer();
        camera = new Camera();
        camera.addListener(this);

        worldCanvas.widthProperty().bind(canvasPane.widthProperty());
        worldCanvas.heightProperty().bind(canvasPane.heightProperty());
        worldCanvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
        worldCanvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());

        seedInput.setText(String.valueOf(world.getSeed()));
        worldWidthInput.setText(String.valueOf(world.getWidth()));
        worldHeightInput.setText(String.valueOf(world.getHeight()));
        waterLevelInput.setText(String.valueOf(world.getWaterLevel()));
        baseHeightInput.setText(String.valueOf(world.terrainHeightGenerator.getBaseHeight()));
        noiseScaleAInput.setText(String.valueOf(world.terrainHeightGenerator.getScaleA()));
        noiseScaleBInput.setText(String.valueOf(world.terrainHeightGenerator.getScaleB()));
        noiseScaleCInput.setText(String.valueOf(world.terrainHeightGenerator.getScaleC()));
        noiseAmplitudeAInput.setText(String.valueOf(world.terrainHeightGenerator.getAmpA()));
        noiseAmplitudeBInput.setText(String.valueOf(world.terrainHeightGenerator.getAmpB()));
        noiseAmplitudeCInput.setText(String.valueOf(world.terrainHeightGenerator.getAmpC()));

        regenButton.setOnAction(e -> handleInitializeWorld());
        draw();
    }

    public void draw() {
        GraphicsContext gc = worldCanvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.LIGHTBLUE);
        gc.fillRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
        renderer.render(gc, world, camera, worldCanvas.getWidth(), worldCanvas.getHeight());
    }

    public void handleInitializeWorld() {
        try {
            world.setSeed(Integer.parseInt(seedInput.getText()));
            world.setWidth(Integer.parseInt(worldWidthInput.getText()));
            world.setHeight(Integer.parseInt(worldHeightInput.getText()));
            world.setWaterLevel(Integer.parseInt(waterLevelInput.getText()));
            world.terrainHeightGenerator.setBaseHeight(Integer.parseInt(baseHeightInput.getText()));
            world.terrainHeightGenerator.setAmpA(Double.parseDouble(noiseAmplitudeAInput.getText()));
            world.terrainHeightGenerator.setAmpB(Double.parseDouble(noiseAmplitudeBInput.getText()));
            world.terrainHeightGenerator.setAmpC(Double.parseDouble(noiseAmplitudeCInput.getText()));
            world.terrainHeightGenerator.setScaleA(Double.parseDouble(noiseScaleAInput.getText()));
            world.terrainHeightGenerator.setScaleB(Integer.parseInt(noiseScaleBInput.getText()));
            world.terrainHeightGenerator.setScaleC(Integer.parseInt(noiseScaleCInput.getText()));
            draw();
        } catch (NumberFormatException e) {
            seedInput.setText(String.valueOf(world.getSeed()));
        }
    }

    @Override
    public void onCameraUpdated(Camera camera) {
        draw();
    }
}