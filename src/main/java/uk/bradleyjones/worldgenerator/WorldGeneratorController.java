package uk.bradleyjones.worldgenerator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import uk.bradleyjones.worldgenerator.render.Camera;
import uk.bradleyjones.worldgenerator.render.CameraListener;
import uk.bradleyjones.worldgenerator.render.WorldRenderer;
import uk.bradleyjones.worldgenerator.world.World;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;

import java.security.PrivateKey;

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

    @FXML public VBox caveInstancesBox;
    @FXML public Button addCaveButton;

//    @FXML public VBox caParamsBox;
//    @FXML public CheckBox caCavesEnabledCheckbox;
//    @FXML public TextField caFillPercentInput;
//    @FXML public TextField caIterationsInput;
//    @FXML public TextField caNeighbourThresholdInput;

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



//        caCavesEnabledCheckbox.setSelected(world.caveConfig.enabled);
//        caFillPercentInput.setText(String.valueOf(world.caveConfig.fillPercent));
//        caIterationsInput.setText(String.valueOf(world.caveConfig.iterations));
//        caNeighbourThresholdInput.setText(String.valueOf(world.caveConfig.neighbourThreshold));
//
//        caParamsBox.setDisable(!world.caveConfig.enabled);
//        caCavesEnabledCheckbox.selectedProperty().addListener((obs, oldVal, newVal) ->
//                caParamsBox.setDisable(!newVal)
//        );

        addCaveButton.setOnAction(e -> {
            world.addCaveInstance(CaveGeneratorType.CA);
            CaveGeneratorInstance instance = world.caveInstances.get(world.caveInstances.size() - 1);
            addCaveInstanceUI(instance);
        });

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

//            world.caveConfig.enabled = caCavesEnabledCheckbox.isSelected();
//            world.caveConfig.fillPercent = Integer.parseInt(caFillPercentInput.getText());
//            world.caveConfig.iterations = Integer.parseInt(caIterationsInput.getText());
//            world.caveConfig.neighbourThreshold = Integer.parseInt(caNeighbourThresholdInput.getText());

            world.regenerate();
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

        params.getChildren().addAll(enabledBox, typeDropdown, caParamsSection, noiseParamsSection, drunkardParamsSection, removeButton);
        caveInstancesBox.getChildren().add(pane);
    }

    @Override
    public void onCameraUpdated(Camera camera) {
        draw();
    }
}