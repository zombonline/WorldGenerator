package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGeneratorType;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGroup;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class HeightmapGeneratorInstanceUIComponent {

    private final HeightmapGeneratorInstance instance;
    private final HeightmapGroup parentGroup;
    private final VBox parentContainer;

    private TitledPane pane;
    private VBox params;

    public HeightmapGeneratorInstanceUIComponent(HeightmapGeneratorInstance instance,
                                                 HeightmapGroup parentGroup,
                                                 VBox parentContainer) {
        this.instance = instance;
        this.parentGroup = parentGroup;
        this.parentContainer = parentContainer;
        setUp();
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp() {
        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");
        pane = new TitledPane(titleFor(instance.type), params);
        pane.setAnimated(true);
        pane.setExpanded(false);

        // Type dropdown
        ComboBox<HeightmapGeneratorType> typeDropdown = new ComboBox<>();
        typeDropdown.getItems().addAll(HeightmapGeneratorType.values());
        typeDropdown.setValue(instance.type);
        typeDropdown.setMaxWidth(Double.MAX_VALUE);

        // Enabled
        CheckBox enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(instance.enabled);
        enabledBox.selectedProperty().addListener((obs, o, n) -> instance.enabled = n);

        // Weight
        Label weightLabel = new Label("Weight");
        RandomizableField<Float> weightField = new RandomizableField<>(instance.weight, 0f, 10f);
        weightField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.weight = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });

        // Noise params section
        VBox noiseParamsSection = new VBox(4);
        Label scaleLabel = new Label("Scale");
        RandomizableField<Double> scaleField = new RandomizableField<>(instance.noiseGenerator.getScale(), 0.1d, 5d);
        scaleField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseGenerator.setScale(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label amplitudeLabel = new Label("Amplitude");
        RandomizableField<Double> amplitudeField = new RandomizableField<>(instance.noiseGenerator.getAmplitude(),0.1d, 128d);
        amplitudeField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseGenerator.setAmplitude(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label powerLabel = new Label("Power");
        RandomizableField<Double> powerField = new RandomizableField<>(instance.noiseGenerator.getPower(), 0.1d, 2d);
        powerField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseGenerator.setPower(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });
        CheckBox clampBox = new CheckBox("Clamp to Positive");
        clampBox.setSelected(instance.noiseGenerator.isClampToPositive());
        clampBox.selectedProperty().addListener((obs, o, n) -> instance.noiseGenerator.setClampToPositive(n));
        noiseParamsSection.getChildren().addAll(scaleLabel, scaleField.get(), amplitudeLabel, amplitudeField.get(),
                powerLabel, powerField.get(), clampBox);

        //steps params section
        VBox stepsParamsSection = new VBox(4);
        Label minStepHeightLabel = new Label("Minimum Step Height");
        RandomizableField<Integer> minStepHeightField = new RandomizableField<>(instance.stepGenerator.getMinStepHeight(), 1, 10);
        minStepHeightField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMinStepHeight(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label maxStepHeightLabel = new Label("Maximum Step Height");
        RandomizableField<Integer> maxStepHeightField = new RandomizableField<>(instance.stepGenerator.getMaxStepHeight(), 1, 10);
        maxStepHeightField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMaxStepHeight(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label minGapLabel = new Label("Minimum Step Width");
        RandomizableField<Integer> minGapField = new RandomizableField<>(instance.stepGenerator.getMinStepGap(), 1, 10);
        minGapField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMaxStepGap(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label maxGapLabel = new Label("Maximum Step Width");
        RandomizableField<Integer> maxGapField = new RandomizableField<>(instance.stepGenerator.getMaxStepGap(), 1, 10);
        maxGapField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMaxStepGap(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        stepsParamsSection.getChildren().addAll(minStepHeightLabel, minStepHeightField.get(),
                maxStepHeightLabel, maxStepHeightField.get(),
                minGapLabel, minGapField.get(),
                maxGapLabel, maxGapField.get());

        // Set initial visibility
        noiseParamsSection.setVisible(instance.type == HeightmapGeneratorType.NOISE);
        noiseParamsSection.setManaged(instance.type == HeightmapGeneratorType.NOISE);
        stepsParamsSection.setVisible(instance.type == HeightmapGeneratorType.STEPS);
        stepsParamsSection.setManaged(instance.type == HeightmapGeneratorType.STEPS);

        // Type dropdown listener
        typeDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            instance.setType(newVal);
            noiseParamsSection.setVisible(newVal == HeightmapGeneratorType.NOISE);
            noiseParamsSection.setManaged(newVal == HeightmapGeneratorType.NOISE);
            stepsParamsSection.setVisible(instance.type == HeightmapGeneratorType.STEPS);
            stepsParamsSection.setManaged(instance.type == HeightmapGeneratorType.STEPS);
            pane.setText(titleFor(newVal));
        });

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            parentGroup.children.remove(instance);
            parentContainer.getChildren().remove(pane);
        });

        params.getChildren().addAll(enabledBox, typeDropdown, weightLabel, weightField.get(),
                noiseParamsSection, stepsParamsSection, removeButton);
    }

    private String titleFor(HeightmapGeneratorType type) {
        return switch (type) {
            case NOISE -> "Noise Generator";
            case STEPS -> "Steps Generator";
        };
    }
}
