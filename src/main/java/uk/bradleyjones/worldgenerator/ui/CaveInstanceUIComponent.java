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
    private Runnable onRemove;

    public CaveInstanceUIComponent(CaveGeneratorInstance instance, VBox parentContainer, Runnable onRemove) {
        this.instance = instance;
        this.parentContainer = parentContainer;
        this.onRemove = onRemove;
        setUp();
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp() {
        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");
        pane = new TitledPane(instance.desc, params);
        pane.setAnimated(true);
        pane.setExpanded(false);

        //desc textField
        Label descLabel = new Label("Description");
        TextField descField = new TextField(instance.desc);
        descField.textProperty().addListener((obs, o, n) -> {
            instance.desc = n;
            pane.setText(n);
        });

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
        RandomizableField<Integer> fillField = new RandomizableField<>(instance.caConfig.fillPercent, 1, 100);
        fillField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.fillPercent = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label iterLabel = new Label("Iterations");
        RandomizableField<Integer> iterField = new RandomizableField<>(instance.caConfig.iterations,1, 10);
        iterField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.iterations = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label threshLabel = new Label("Neighbour Threshold");
        RandomizableField<Integer> threshField = new RandomizableField<>(instance.caConfig.neighborThreshold,1, 9);
        threshField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.neighborThreshold = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        caParamsSection.getChildren().addAll(fillLabel, fillField.get(), iterLabel, iterField.get(), threshLabel, threshField.get());

        // Noise params
        VBox noiseParamsSection = new VBox(4);
        Label scaleXLabel = new Label("Scale X");
        RandomizableField<Float> scaleXField = new RandomizableField<>(instance.noiseConfig.scaleX, 0.5f, 4f);
        scaleXField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.scaleX = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label scaleYLabel = new Label("Scale Y");
        RandomizableField<Float> scaleYField = new RandomizableField<>(instance.noiseConfig.scaleY, 0.5f, 4f);
        scaleYField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.scaleY = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label lowThreshLabel = new Label("Lower Threshold");
        RandomizableField<Float> lowThreshField = new RandomizableField<>(instance.noiseConfig.lowerThreshold, -1f, 1f);
        lowThreshField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.lowerThreshold = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label uppThreshLabel = new Label("Upper Threshold");
        RandomizableField<Float> uppThreshField = new RandomizableField<>(instance.noiseConfig.upperThreshold, -1f, 1f);
        uppThreshField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.upperThreshold = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        noiseParamsSection.getChildren().addAll(scaleXLabel, scaleXField.get(), scaleYLabel, scaleYField.get(),
                lowThreshLabel, lowThreshField.get(), uppThreshLabel, uppThreshField.get());

        // Drunkard params
        VBox drunkardParamsSection = new VBox(4);
        Label walkerCountLabel = new Label("Walker Count");
        RandomizableField<Integer> walkerCountField = new RandomizableField<>(instance.drunkardConfig.walkerCount,100, 800);
        walkerCountField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.drunkardConfig.walkerCount = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label stepsLabel = new Label("Walker Steps");
        RandomizableField<Integer> stepsField = new RandomizableField<>(instance.drunkardConfig.steps,10, 250);
        stepsField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.drunkardConfig.steps = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        drunkardParamsSection.getChildren().addAll(walkerCountLabel, walkerCountField.get(), stepsLabel, stepsField.get());

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
            onRemove.run();
        });

        params.getChildren().addAll(descLabel, descField, enabledBox, effectsSurfaceBox, typeDropdown,
                caParamsSection, noiseParamsSection, drunkardParamsSection, removeButton);
    }
}
