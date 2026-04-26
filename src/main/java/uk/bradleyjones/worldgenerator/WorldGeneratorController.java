package uk.bradleyjones.worldgenerator;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.input.InputHandler;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.CameraListener;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.saving.WorldFileManager;
import uk.bradleyjones.worldgenerator.ui.*;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.biomes.Biome;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeEntry;

import java.util.HashMap;


public class WorldGeneratorController implements CameraListener {

    Stage stage;
    @FXML BorderPane root;

    //WORLD CONFIG
    @FXML public RandomizableField seedInput;
    @FXML public RandomizableField worldWidthInput;
    @FXML public RandomizableField worldHeightInput;
    @FXML public RandomizableField bedrockHeightInput;
    @FXML public RandomizableField bedrockNoiseScaleInput;

    //WATER CONFIG
    @FXML public RandomizableField waterLevelInput;
    @FXML public RandomizableField lakeMinWidthInput;
    @FXML public RandomizableField oceanMinWidthInput;
    @FXML public RandomizableField pressurePerDepthInput;
    @FXML public RandomizableField upwardCostInput;
    @FXML public RandomizableField minPressureToFloodInput;
    //HEIGHTMAP CONFIG
    @FXML public RandomizableField baseHeightInput;
    @FXML public RandomizableField minSubsurfaceInput;
    @FXML public RandomizableField maxSubsurfaceInput;
    @FXML public VBox heightmapInstancesBox;

    @FXML private Pane canvasPane;
    @FXML private Canvas worldCanvas;
    @FXML private Button regenButton;
    @FXML private ProgressBar regenProgress;

    @FXML public VBox caveInstancesBox;

    @FXML public VBox decorationInstancesBox;

    //BIOME DISTRIBUTIO CONFIG
    @FXML public RandomizableField biomeNoiseScaleInput;
    @FXML public VBox biomeWeightsBox;

    //BIOME OVERRIDE CONFIG
    @FXML public RandomizableField beachWidthInput;
    @FXML public RandomizableField mountainHeightInput;
    @FXML public RandomizableField peakHeightInput;

    @FXML public VBox substanceRulesBox;

    @FXML public Button saveAsButton;
    @FXML public Button saveButton;
    @FXML public Button loadButton;
    @FXML public Label loadedFileName;

    public HashMap<BiomeEntry, RandomizableField> biomeEntryRandomizableFieldHashMap = new HashMap<>();

    private HeightmapGroupUIComponent heightmapGroupUIComponent;
    private CaveListUIComponent caveListUIComponent;
    private DecorationListUIComponent decorationListUIComponent;
    private SubstanceListUIComponent substanceListUIComponent;

    public static World world;
    public static WorldRenderer renderer;
    public static Camera camera;

    public void setStage(Stage stage) {
        this.stage = stage;
        InputHandler inputHandler = new InputHandler(canvasPane);
        stage.getScene().setOnKeyPressed(inputHandler::handleKeyPressed);
        stage.getScene().setOnKeyReleased(inputHandler::handleKeyReleased);
        stage.getScene().setOnScroll(inputHandler::handleScroll);
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


        //set fonts
        root.setStyle("-fx-font-family: 'Jost';");

        worldCanvas.widthProperty().bind(canvasPane.widthProperty());
        worldCanvas.heightProperty().bind(canvasPane.heightProperty());
        worldCanvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
        worldCanvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());

        //Create list ui components
        heightmapGroupUIComponent = new HeightmapGroupUIComponent(world.getHeightmapConfig().heightmapGroup);
        caveListUIComponent = new CaveListUIComponent();
        decorationListUIComponent = new DecorationListUIComponent();
        substanceListUIComponent = new SubstanceListUIComponent();

        heightmapInstancesBox.getChildren().add(heightmapGroupUIComponent.get());
        caveInstancesBox.getChildren().add(caveListUIComponent.get());
        decorationInstancesBox.getChildren().add(decorationListUIComponent.get());
        substanceRulesBox.getChildren().add(substanceListUIComponent.get());


        //Hook up buttons
        regenButton.setOnAction(e -> handleInitializeWorld());
        saveAsButton.setOnAction(e -> {
            handleInitializeWorld();
            WorldFileManager.saveAs(stage, world);
            loadedFileName.setText("Loaded: " + WorldFileManager.getCurrentName() + ".world");
        });
        saveButton.setOnAction(e ->{
            handleInitializeWorld();
            WorldFileManager.save(stage, world);
        });
        loadButton.setOnAction(e -> {
            WorldFileManager.load(stage, world);
            refreshUI();
            handleInitializeWorld();
            loadedFileName.setText("Loaded: " + WorldFileManager.getCurrentName() + ".world");
        });

        //refresh valyes
        refreshUI();

        draw();
    }

    public void draw() {
        GraphicsContext gc = worldCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
        renderer.render(gc, camera, worldCanvas.getWidth(), worldCanvas.getHeight());
    }


    public void handleInitializeWorld() {
        regenProgress.setVisible(true);
        regenButton.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    CommitRegistry.commitAll();
                    world.getWorldConfig().seed = Long.parseLong(seedInput.getValue());
                    world.getWorldConfig().width = Integer.parseInt(worldWidthInput.getValue());
                    world.getWorldConfig().height = Integer.parseInt(worldHeightInput.getValue());
                    world.getWorldConfig().bedrockHeight = Integer.parseInt(bedrockHeightInput.getValue());
                    world.getWorldConfig().bedrockNoiseScale = Float.parseFloat(bedrockNoiseScaleInput.getValue());

                    world.getWaterConfig().waterLevel = Integer.parseInt(waterLevelInput.getValue());
                    world.getWaterConfig().lakeMinWidth = Integer.parseInt(lakeMinWidthInput.getValue());
                    world.getWaterConfig().oceanMinWidth = Integer.parseInt(oceanMinWidthInput.getValue());
                    world.getWaterConfig().pressurePerDepth = Float.parseFloat(pressurePerDepthInput.getValue());
                    world.getWaterConfig().upwardCost = Float.parseFloat(upwardCostInput.getValue());
                    world.getWaterConfig().minPressureToFlood = Float.parseFloat(minPressureToFloodInput.getValue());

                    world.getHeightmapConfig().baseHeight = Integer.parseInt(baseHeightInput.getValue());
                    world.getHeightmapConfig().minSubSurfaceDepth = Integer.parseInt(minSubsurfaceInput.getValue());
                    world.getHeightmapConfig().maxSubSurfaceDepth = Integer.parseInt(maxSubsurfaceInput.getValue());

                    world.getBiomeGeneratorConfig().noiseScale = Float.parseFloat(biomeNoiseScaleInput.getValue());
                    for (var kvp : biomeEntryRandomizableFieldHashMap.entrySet()) {
                        kvp.getKey().weight = Float.parseFloat(kvp.getValue().getValue());
                    }

                    world.getBiomeOverrideConfig().beachWidth = Integer.parseInt(beachWidthInput.getValue());
                    world.getBiomeOverrideConfig().mountainHeight = Integer.parseInt(mountainHeightInput.getValue());
                    world.getBiomeOverrideConfig().peakHeight = Integer.parseInt(peakHeightInput.getValue());

                    world.regenerate();
                } catch (NumberFormatException e) {
                    System.out.println("NumberFormatException: " + e.getMessage());
                }
                return null;
            }
        };

        task.setOnSucceeded(ev -> {
            regenProgress.setVisible(false);
            regenButton.setDisable(false);
            renderer.buildWorldImageAsync();
            draw();
        });

        new Thread(task).start();
    }

    private void addBiomeWeightUI(BiomeEntry entry) {
        HBox row = new HBox(4);
        row.setStyle("-fx-padding: 2;");

        Label nameLabel = new Label(entry.biome.getName());
        nameLabel.setPrefWidth(80);

        RandomizableField weightField = new RandomizableField();
        weightField.setType("Float");
        weightField.setMin(0);
        weightField.setMax(1);
        weightField.setValue(String.valueOf(entry.weight));
        weightField.setMaxWidth(Double.MAX_VALUE);
        biomeEntryRandomizableFieldHashMap.put(entry, weightField);

        row.getChildren().addAll(nameLabel, weightField);
        biomeWeightsBox.getChildren().add(row);
    }

    @Override
    public void onCameraUpdated(Camera camera) {
        draw();
    }

    public void refreshUI(){
        //WORLD CONFIG
        seedInput.setValue(String.valueOf(world.getWorldConfig().seed));
        worldWidthInput.setValue(String.valueOf(world.getWorldConfig().width));
        worldHeightInput.setValue(String.valueOf(world.getWorldConfig().height));
        bedrockHeightInput.setValue(String.valueOf(world.getWorldConfig().bedrockHeight));
        bedrockNoiseScaleInput.setValue(String.valueOf(world.getWorldConfig().bedrockNoiseScale));

        //WATER CONFIG
        waterLevelInput.setValue(String.valueOf(world.getWaterConfig().waterLevel));
        lakeMinWidthInput.setValue(String.valueOf(world.getWaterConfig().lakeMinWidth));
        oceanMinWidthInput.setValue(String.valueOf(world.getWaterConfig().oceanMinWidth));
        pressurePerDepthInput.setValue(String.valueOf(world.getWaterConfig().pressurePerDepth));
        upwardCostInput.setValue(String.valueOf(world.getWaterConfig().upwardCost));
        minPressureToFloodInput.setValue(String.valueOf(world.getWaterConfig().minPressureToFlood));

        //HEIGHTMAP CONFIG
        baseHeightInput.setValue(String.valueOf(world.getHeightmapConfig().baseHeight));
        minSubsurfaceInput.setValue(String.valueOf(world.getHeightmapConfig().minSubSurfaceDepth));
        maxSubsurfaceInput.setValue(String.valueOf(world.getHeightmapConfig().maxSubSurfaceDepth));

        //We have to create a new isntance of heightmapgroup ui component as loading a save replaces the heightmap group object in World's heightmap config
        heightmapInstancesBox.getChildren().clear();
        heightmapGroupUIComponent = new HeightmapGroupUIComponent(world.getHeightmapConfig().heightmapGroup);
        heightmapInstancesBox.getChildren().add(heightmapGroupUIComponent.get());


        caveListUIComponent.refresh();
        decorationListUIComponent.refresh();
        substanceListUIComponent.refresh();

        //BIOME DISTRIBUTION  CONFIG
        biomeNoiseScaleInput.setValue(String.valueOf(world.getBiomeGeneratorConfig().noiseScale));
        biomeWeightsBox.getChildren().clear();
        biomeEntryRandomizableFieldHashMap.clear();
        for (BiomeEntry entry : world.getBiomeGeneratorConfig().biomes) {
            addBiomeWeightUI(entry);
        }

        //BIOME OVERRIDE CONFIG
        beachWidthInput.setValue(String.valueOf(world.getBiomeOverrideConfig().beachWidth));
        mountainHeightInput.setValue(String.valueOf(world.getBiomeOverrideConfig().mountainHeight));
        peakHeightInput.setValue(String.valueOf(world.getBiomeOverrideConfig().peakHeight));

    }
}