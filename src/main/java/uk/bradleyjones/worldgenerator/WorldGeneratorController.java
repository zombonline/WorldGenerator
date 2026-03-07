package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    @FXML public VBox caParamsBox;
    @FXML public CheckBox caCavesEnabledCheckbox;
    @FXML public TextField caFillPercentInput;
    @FXML public TextField caIterationsInput;
    @FXML public TextField caNeighbourThresholdInput;

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

        // Populate fields from config objects
        seedInput.setText(String.valueOf(world.worldConfig.seed));
        worldWidthInput.setText(String.valueOf(world.worldConfig.width));
        worldHeightInput.setText(String.valueOf(world.worldConfig.height));
        waterLevelInput.setText(String.valueOf(world.worldConfig.waterLevel));

        baseHeightInput.setText(String.valueOf(world.terrainConfig.baseHeight));
        noiseScaleAInput.setText(String.valueOf(world.terrainConfig.scaleA));
        noiseScaleBInput.setText(String.valueOf(world.terrainConfig.scaleB));
        noiseScaleCInput.setText(String.valueOf(world.terrainConfig.scaleC));
        noiseAmplitudeAInput.setText(String.valueOf(world.terrainConfig.ampA));
        noiseAmplitudeBInput.setText(String.valueOf(world.terrainConfig.ampB));
        noiseAmplitudeCInput.setText(String.valueOf(world.terrainConfig.ampC));

        caCavesEnabledCheckbox.setSelected(world.caveConfig.enabled);
        caFillPercentInput.setText(String.valueOf(world.caveConfig.fillPercent));
        caIterationsInput.setText(String.valueOf(world.caveConfig.iterations));
        caNeighbourThresholdInput.setText(String.valueOf(world.caveConfig.neighbourThreshold));

        caParamsBox.setDisable(!world.caveConfig.enabled);
        caCavesEnabledCheckbox.selectedProperty().addListener((obs, oldVal, newVal) ->
                caParamsBox.setDisable(!newVal)
        );

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
            world.worldConfig.seed = Integer.parseInt(seedInput.getText());
            world.worldConfig.width = Integer.parseInt(worldWidthInput.getText());
            world.worldConfig.height = Integer.parseInt(worldHeightInput.getText());
            world.worldConfig.waterLevel = Integer.parseInt(waterLevelInput.getText());

            world.terrainConfig.baseHeight = Integer.parseInt(baseHeightInput.getText());
            world.terrainConfig.ampA = Double.parseDouble(noiseAmplitudeAInput.getText());
            world.terrainConfig.ampB = Double.parseDouble(noiseAmplitudeBInput.getText());
            world.terrainConfig.ampC = Double.parseDouble(noiseAmplitudeCInput.getText());
            world.terrainConfig.scaleA = Double.parseDouble(noiseScaleAInput.getText());
            world.terrainConfig.scaleB = Double.parseDouble(noiseScaleBInput.getText());
            world.terrainConfig.scaleC = Double.parseDouble(noiseScaleCInput.getText());

            world.caveConfig.enabled = caCavesEnabledCheckbox.isSelected();
            world.caveConfig.fillPercent = Integer.parseInt(caFillPercentInput.getText());
            world.caveConfig.iterations = Integer.parseInt(caIterationsInput.getText());
            world.caveConfig.neighbourThreshold = Integer.parseInt(caNeighbourThresholdInput.getText());

            world.regenerate();
            draw();
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        }
    }

    @Override
    public void onCameraUpdated(Camera camera) {
        draw();
    }
}