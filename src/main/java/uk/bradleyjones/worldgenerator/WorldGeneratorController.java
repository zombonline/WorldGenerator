package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.CameraListener;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.ui.DecorationInstanceUIComponent;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.biomes.BiomeEntry;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class WorldGeneratorController implements CameraListener {

    Stage stage;

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

    @FXML public VBox caveInstancesBox;
    @FXML public Button addCaveButton;

    @FXML public VBox decorationInstancesBox;
    @FXML public Button addDecorationButton;
    @FXML public Button loadDecorationButton;

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
        renderer = new WorldRenderer();
        renderer.loadImageMap();
        renderer.buildWorldImageAsync(world);
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

        baseHeightInput.setText(String.valueOf(world.getTerrainConfig().baseHeight));
        noiseScaleAInput.setText(String.valueOf(world.getTerrainConfig().scaleA));
        noiseScaleBInput.setText(String.valueOf(world.getTerrainConfig().scaleB));
        noiseScaleCInput.setText(String.valueOf(world.getTerrainConfig().scaleC));
        noiseAmplitudeAInput.setText(String.valueOf(world.getTerrainConfig().ampA));
        noiseAmplitudeBInput.setText(String.valueOf(world.getTerrainConfig().ampB));
        noiseAmplitudeCInput.setText(String.valueOf(world.getTerrainConfig().ampC));



        addCaveButton.setOnAction(e -> {
            world.addCaveInstance(CaveGeneratorType.CA);
            CaveGeneratorInstance instance = world.getCaveInstances().get(world.getCaveInstances().size() - 1);
            addCaveInstanceUI(instance);
        });

        addDecorationButton.setOnAction(e -> {
            Decoration decoration = new Decoration();
            DecorationInstance instance = new DecorationInstance(decoration, true);
            world.addDecorationInstance(instance);
            addDecorationInstanceUI(instance);
        });

        loadDecorationButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load Decoration");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Decoration Files", "*.decoration")
            );
            File dir = new File(DecorationRepository.DECORATIONS_DIR);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            chooser.setInitialDirectory(dir);
            List<File> files = chooser.showOpenMultipleDialog(stage);
            try {
                for(File file : files) {
                    var decoration = DecorationRepository.load(file.toPath());
                    var instance = new DecorationInstance(decoration, true);
                    instance.fileName = file.getName();
                    var existingInstances = world.getDecorationInstances();
                    for (var existingInstance : existingInstances) {
                        System.out.println("Existing instance file name: " + existingInstance.fileName + ", loaded file name: " + file.getName());
                        if (Objects.equals(existingInstance.fileName, file.getName()))
                            return;
                    }
                    world.addDecorationInstance(instance);
                    addDecorationInstanceUI(instance);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

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
        renderer.render(gc, world, camera, worldCanvas.getWidth(), worldCanvas.getHeight());
    }

    public void handleInitializeWorld() {
        try {
            world.getWorldConfig().seed = Integer.parseInt(seedInput.getText());
            world.getWorldConfig().width = Integer.parseInt(worldWidthInput.getText());
            world.getWorldConfig().height = Integer.parseInt(worldHeightInput.getText());
            world.getWorldConfig().waterLevel = Integer.parseInt(waterLevelInput.getText());

            world.getTerrainConfig().baseHeight = Integer.parseInt(baseHeightInput.getText());
            world.getTerrainConfig().ampA = Double.parseDouble(noiseAmplitudeAInput.getText());
            world.getTerrainConfig().ampB = Double.parseDouble(noiseAmplitudeBInput.getText());
            world.getTerrainConfig().ampC = Double.parseDouble(noiseAmplitudeCInput.getText());
            world.getTerrainConfig().scaleA = Double.parseDouble(noiseScaleAInput.getText());
            world.getTerrainConfig().scaleB = Double.parseDouble(noiseScaleBInput.getText());
            world.getTerrainConfig().scaleC = Double.parseDouble(noiseScaleCInput.getText());

            world.getBiomeGeneratorConfig().noiseScale = Float.parseFloat(biomeNoiseScaleInput.getText());
            world.getBiomeOverrideConfig().beachWidth = Integer.parseInt(beachWidthInput.getText());
            world.getBiomeOverrideConfig().mountainHeight = Integer.parseInt(mountainHeightInput.getText());
            world.getBiomeOverrideConfig().peakHeight = Integer.parseInt(peakHeightInput.getText());
            world.getBiomeOverrideConfig().lakeMinWidth = Integer.parseInt(lakeMinWidthInput.getText());
            world.getBiomeOverrideConfig().oceanMinWidth = Integer.parseInt(oceanMinWidthInput.getText());

            world.regenerate();
            renderer.buildWorldImageAsync(world);
            draw();
            draw();
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        }
    }

    private void addCaveInstanceUI(CaveGeneratorInstance instance) {
        VBox params = new VBox(4);
        params.setStyle("-fx-padding: 4;");

        TitledPane pane = new TitledPane("CA Cave Generator", params);
        pane.setAnimated(true);
        pane.setExpanded(false);

        // Type dropdown
        ComboBox<CaveGeneratorType> typeDropdown = new ComboBox<>();
        typeDropdown.getItems().addAll(CaveGeneratorType.CA, CaveGeneratorType.NOISE, CaveGeneratorType.DRUNKARD);
        typeDropdown.setValue(instance.type);
        typeDropdown.setMaxWidth(Double.MAX_VALUE);

        // Enabled checkbox
        CheckBox enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(instance.enabled);
        enabledBox.selectedProperty().addListener((obs, o, n) -> instance.enabled = n);

        //Effects surface checkbox
        CheckBox effectsSurfaceBox = new CheckBox("Effects Surface");
        effectsSurfaceBox.setSelected(instance.getConfig().effectsSurface);
        effectsSurfaceBox.selectedProperty().addListener((obs, o, n) -> {
            instance.caConfig.effectsSurface = n;
            instance.noiseConfig.effectsSurface = n;
            instance.drunkardConfig.effectsSurface = n;
        });

        // CA params
        VBox caParamsSection = new VBox(4);
        Label fillLabel = new Label("Fill Percent");
        TextField fillField = new TextField(String.valueOf(instance.caConfig.fillPercent));
        fillField.textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.fillPercent = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label iterLabel = new Label("Iterations");
        TextField iterField = new TextField(String.valueOf(instance.caConfig.iterations));
        iterField.textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.iterations = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label threshLabel = new Label("Neighbour Threshold");
        TextField threshField = new TextField(String.valueOf(instance.caConfig.neighbourThreshold));
        threshField.textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.neighbourThreshold = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        caParamsSection.getChildren().addAll(fillLabel, fillField, iterLabel, iterField, threshLabel, threshField);

        // Noise params
        VBox noiseParamsSection = new VBox(4);
        Label scaleXLabel = new Label("Scale X");
        TextField scaleXField = new TextField(String.valueOf(instance.noiseConfig.scaleX));
        scaleXField.textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.scaleX = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label scaleYLabel = new Label("Scale Y");
        TextField scaleYField = new TextField(String.valueOf(instance.noiseConfig.scaleY));
        scaleYField.textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.scaleY = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label lowThreshLabel = new Label("Lower Threshold");
        TextField lowThreshField = new TextField(String.valueOf(instance.noiseConfig.lowerThreshold));
        lowThreshField.textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.lowerThreshold = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label uppThreshLabel = new Label("Upper Threshold");
        TextField uppThreshField = new TextField(String.valueOf(instance.noiseConfig.upperThreshold));
        uppThreshField.textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.upperThreshold = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        noiseParamsSection.getChildren().addAll(scaleXLabel, scaleXField, scaleYLabel, scaleYField,
                lowThreshLabel, lowThreshField, uppThreshLabel, uppThreshField);

        //Drunkard Params
        VBox drunkardParamsSection = new VBox(4);
        Label walkerCountLabel = new Label("Walker Count");
        TextField walkerCountField = new TextField(String.valueOf(instance.drunkardConfig.walkerCount));
        walkerCountField.textProperty().addListener((obs, o, n) -> {
            try { instance.drunkardConfig.walkerCount = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label stepsLabel = new Label("Walker Steps");
        TextField stepsField = new TextField(String.valueOf(instance.drunkardConfig.steps));
        stepsField.textProperty().addListener((obs, o, n) -> {
            try { instance.drunkardConfig.steps = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        drunkardParamsSection.getChildren().addAll(walkerCountLabel,walkerCountField,stepsLabel,stepsField);

        // Set initial visibility
        caParamsSection.setVisible(instance.type == CaveGeneratorType.CA);
        caParamsSection.setManaged(instance.type == CaveGeneratorType.CA);
        noiseParamsSection.setVisible(instance.type == CaveGeneratorType.NOISE);
        noiseParamsSection.setManaged(instance.type == CaveGeneratorType.NOISE);
        drunkardParamsSection.setVisible(instance.type == CaveGeneratorType.DRUNKARD);
        drunkardParamsSection.setManaged(instance.type == CaveGeneratorType.DRUNKARD);

        // Type dropdown listener
        typeDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            instance.type = newVal;
            caParamsSection.setVisible(newVal == CaveGeneratorType.CA);
            caParamsSection.setManaged(newVal == CaveGeneratorType.CA);
            noiseParamsSection.setVisible(newVal == CaveGeneratorType.NOISE);
            noiseParamsSection.setManaged(newVal == CaveGeneratorType.NOISE);
            drunkardParamsSection.setVisible(newVal == CaveGeneratorType.DRUNKARD);
            drunkardParamsSection.setManaged(newVal == CaveGeneratorType.DRUNKARD);
            pane.setText(switch (newVal) {
                case CA -> "CA Cave Generator";
                case NOISE -> "Noise Cave Generator";
                case DRUNKARD -> "Drunkard Cave Generator";
            });
        });

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            world.removeCaveInstance(instance);
            caveInstancesBox.getChildren().remove(pane);
        });

        params.getChildren().addAll(enabledBox, effectsSurfaceBox, typeDropdown, caParamsSection, noiseParamsSection, drunkardParamsSection, removeButton);
        caveInstancesBox.getChildren().add(pane);
    }

    public void addDecorationInstanceUI(DecorationInstance instance){
        decorationInstancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, decorationInstancesBox).get());
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