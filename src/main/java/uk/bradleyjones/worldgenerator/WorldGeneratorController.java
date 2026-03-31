package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.CameraListener;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.ui.CaveListUIComponent;
import uk.bradleyjones.worldgenerator.ui.DecorationListUIComponent;
import uk.bradleyjones.worldgenerator.ui.HeightmapGroupUIComponent;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeEntry;


public class WorldGeneratorController implements CameraListener {

    Stage stage;

    @FXML public TextField worldWidthInput;
    @FXML public TextField worldHeightInput;
    @FXML public TextField waterLevelInput;

    @FXML public TextField baseHeightInput;
    @FXML public TextField minSubsurfaceInput;
    @FXML public TextField maxSubsurfaceInput;

    @FXML public VBox heightmapInstancesBox;

    @FXML private Pane canvasPane;
    @FXML private Canvas worldCanvas;
    @FXML private TextField seedInput;
    @FXML private Button regenButton;

    @FXML public VBox caveInstancesBox;

    @FXML public VBox decorationInstancesBox;

    @FXML public TextField biomeNoiseScaleInput;
    @FXML public TextField beachWidthInput;
    @FXML public TextField mountainHeightInput;
    @FXML public TextField peakHeightInput;
    @FXML public TextField lakeMinWidthInput;
    @FXML public TextField oceanMinWidthInput;
    @FXML public VBox biomeWeightsBox;

    public static World world;
    public static WorldRenderer renderer;
    public static Camera camera;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        world = new World();
        world.regenerate();
        renderer = new WorldRenderer();
        renderer.loadImageMap();
        renderer.buildWorldImageAsync();
        camera = new Camera();
        camera.addListener(this);

        worldCanvas.widthProperty().bind(canvasPane.widthProperty());
        worldCanvas.heightProperty().bind(canvasPane.heightProperty());
        worldCanvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
        worldCanvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());

        // Populate fields from config objects
        seedInput.setText(String.valueOf(world.getWorldConfig().seed));
        worldWidthInput.setText(String.valueOf(world.getWorldConfig().width));
        worldHeightInput.setText(String.valueOf(world.getWorldConfig().height));
        waterLevelInput.setText(String.valueOf(world.getWorldConfig().waterLevel));

        baseHeightInput.setText(String.valueOf(world.getHeightmapConfig().baseHeight));
        minSubsurfaceInput.setText(String.valueOf(world.getHeightmapConfig().minSubSurfaceDepth));
        maxSubsurfaceInput.setText(String.valueOf(world.getHeightmapConfig().maxSubSurfaceDepth));
        
        heightmapInstancesBox.getChildren().add(new HeightmapGroupUIComponent(world.getHeightmapConfig().heightmapGroup).get());
        caveInstancesBox.getChildren().add(new CaveListUIComponent().get());
        decorationInstancesBox.getChildren().add(new DecorationListUIComponent().get());

// Populate biome distribution
        biomeNoiseScaleInput.setText(String.valueOf(world.getBiomeGeneratorConfig().noiseScale));
        for (BiomeEntry entry : world.getBiomeGeneratorConfig().biomes) {
            addBiomeWeightUI(entry);
        }

// Populate biome overrides
        beachWidthInput.setText(String.valueOf(world.getBiomeOverrideConfig().beachWidth));
        mountainHeightInput.setText(String.valueOf(world.getBiomeOverrideConfig().mountainHeight));
        peakHeightInput.setText(String.valueOf(world.getBiomeOverrideConfig().peakHeight));
        lakeMinWidthInput.setText(String.valueOf(world.getBiomeOverrideConfig().lakeMinWidth));
        oceanMinWidthInput.setText(String.valueOf(world.getBiomeOverrideConfig().oceanMinWidth));


        regenButton.setOnAction(e -> handleInitializeWorld());
        draw();
    }

    public void draw() {
        GraphicsContext gc = worldCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
        renderer.render(gc, camera, worldCanvas.getWidth(), worldCanvas.getHeight());
    }

    public void handleInitializeWorld() {
        try {
            world.getWorldConfig().seed = Integer.parseInt(seedInput.getText());
            world.getWorldConfig().width = Integer.parseInt(worldWidthInput.getText());
            world.getWorldConfig().height = Integer.parseInt(worldHeightInput.getText());
            world.getWorldConfig().waterLevel = Integer.parseInt(waterLevelInput.getText());

            world.getHeightmapConfig().baseHeight = Integer.parseInt(baseHeightInput.getText());
            world.getHeightmapConfig().minSubSurfaceDepth = Integer.parseInt(minSubsurfaceInput.getText());
            world.getHeightmapConfig().maxSubSurfaceDepth = Integer.parseInt(maxSubsurfaceInput.getText());


            world.getBiomeGeneratorConfig().noiseScale = Float.parseFloat(biomeNoiseScaleInput.getText());
            world.getBiomeOverrideConfig().beachWidth = Integer.parseInt(beachWidthInput.getText());
            world.getBiomeOverrideConfig().mountainHeight = Integer.parseInt(mountainHeightInput.getText());
            world.getBiomeOverrideConfig().peakHeight = Integer.parseInt(peakHeightInput.getText());
            world.getBiomeOverrideConfig().lakeMinWidth = Integer.parseInt(lakeMinWidthInput.getText());
            world.getBiomeOverrideConfig().oceanMinWidth = Integer.parseInt(oceanMinWidthInput.getText());

            world.regenerate();
            renderer.buildWorldImageAsync();
            draw();
            draw();
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        }
    }

    private void addBiomeWeightUI(BiomeEntry entry) {
        HBox row = new HBox(4);
        row.setStyle("-fx-padding: 2;");

        Label nameLabel = new Label(entry.type.name);
        nameLabel.setPrefWidth(80);

        TextField weightField = new TextField(String.valueOf(entry.weight));
        weightField.textProperty().addListener((obs, o, n) -> {
            try { entry.weight = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });

        row.getChildren().addAll(nameLabel, weightField);
        biomeWeightsBox.getChildren().add(row);
    }

    @Override
    public void onCameraUpdated(Camera camera) {
        draw();
    }
}