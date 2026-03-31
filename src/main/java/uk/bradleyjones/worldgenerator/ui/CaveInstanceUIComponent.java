package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class CaveInstanceUIComponent {

    private final CaveGeneratorInstance instance;
    private final VBox parentContainer;

    private TitledPane pane;
    private VBox params;

    public CaveInstanceUIComponent(CaveGeneratorInstance instance, VBox parentContainer) {
        this.instance = instance;
        this.parentContainer = parentContainer;
        setUp();
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp() {
        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");
        pane = new TitledPane("CA Cave Generator", params);
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

        // Effects surface checkbox
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

        // Drunkard params
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
        drunkardParamsSection.getChildren().addAll(walkerCountLabel, walkerCountField, stepsLabel, stepsField);

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
            world.getCaveInstances().remove(instance);
            parentContainer.getChildren().remove(pane);
        });

        params.getChildren().addAll(enabledBox, effectsSurfaceBox, typeDropdown,
                caParamsSection, noiseParamsSection, drunkardParamsSection, removeButton);
    }
}
