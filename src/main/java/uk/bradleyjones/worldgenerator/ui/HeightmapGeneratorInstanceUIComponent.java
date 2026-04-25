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
    private final Runnable onRemove;

    private TitledPane pane;
    private VBox params;

    public HeightmapGeneratorInstanceUIComponent(HeightmapGeneratorInstance instance,
                                                 HeightmapGroup parentGroup,
                                                 VBox parentContainer,
                                                 Runnable onRemove) {
        this.instance = instance;
        this.parentGroup = parentGroup;
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
        RandomizableField weightField = new RandomizableField();
        weightField.setType("Float");
        weightField.setMin(0);
        weightField.setMax(10);
        weightField.setValue(String.valueOf(instance.weight));
        weightField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.weight = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });

        // Noise params section
        VBox noiseParamsSection = new VBox(4);
        Label scaleLabel = new Label("Scale");
        RandomizableField scaleField = new RandomizableField();
        scaleField.setType("Double");
        scaleField.setMin(0.1);
        scaleField.setMax(5);
        scaleField.setValue(String.valueOf(instance.noiseGenerator.getScale()));
        scaleField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseGenerator.setScale(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label amplitudeLabel = new Label("Amplitude");
        RandomizableField amplitudeField = new RandomizableField();
        amplitudeField.setType("Double");
        amplitudeField.setMin(0.1);
        amplitudeField.setMax(128);
        amplitudeField.setValue(String.valueOf(instance.noiseGenerator.getAmplitude()));
        amplitudeField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseGenerator.setAmplitude(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label powerLabel = new Label("Power");
        RandomizableField powerField = new RandomizableField();
        powerField.setType("Double");
        powerField.setMin(0.1);
        powerField.setMax(2);
        powerField.setValue(String.valueOf(instance.noiseGenerator.getPower()));
        powerField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseGenerator.setPower(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });
        CheckBox clampBox = new CheckBox("Clamp to Positive");
        clampBox.setSelected(instance.noiseGenerator.isClampToPositive());
        clampBox.selectedProperty().addListener((obs, o, n) -> instance.noiseGenerator.setClampToPositive(n));
        noiseParamsSection.getChildren().addAll(scaleLabel, scaleField, amplitudeLabel, amplitudeField,
                powerLabel, powerField, clampBox);

        //steps params section
        VBox stepsParamsSection = new VBox(4);
        Label minStepHeightLabel = new Label("Minimum Step Height");
        RandomizableField minStepHeightField = new RandomizableField();
        minStepHeightField.setType("Integer");
        minStepHeightField.setMin(1);
        minStepHeightField.setMax(10);
        minStepHeightField.setValue(String.valueOf(instance.stepGenerator.getMinStepHeight()));
        minStepHeightField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMinStepHeight(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label maxStepHeightLabel = new Label("Maximum Step Height");
        RandomizableField maxStepHeightField = new RandomizableField();
        maxStepHeightField.setType("Integer");
        maxStepHeightField.setMin(1);
        maxStepHeightField.setMax(10);
        maxStepHeightField.setValue(String.valueOf(instance.stepGenerator.getMaxStepHeight()));
        maxStepHeightField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMaxStepHeight(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label minGapLabel = new Label("Minimum Step Width");
        RandomizableField minGapField = new RandomizableField();
        minGapField.setType("Integer");
        minGapField.setMin(1);
        minGapField.setMax(10);
        minGapField.setValue(String.valueOf(instance.stepGenerator.getMinStepGap()));
        minGapField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMaxStepGap(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        Label maxGapLabel = new Label("Maximum Step Width");
        RandomizableField maxGapField = new RandomizableField();
        maxGapField.setType("Integer");
        maxGapField.setMin(1);
        maxGapField.setMax(10);
        maxGapField.setValue(String.valueOf(instance.stepGenerator.getMaxStepGap()));
        maxGapField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.stepGenerator.setMaxStepGap(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        stepsParamsSection.getChildren().addAll(minStepHeightLabel, minStepHeightField,
                maxStepHeightLabel, maxStepHeightField,
                minGapLabel, minGapField,
                maxGapLabel, maxGapField);

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
        removeButton.setStyle("-fx-base:#A82A2A");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            parentGroup.children.remove(instance);
            onRemove.run();
            parentContainer.getChildren().remove(pane);
        });

        params.getChildren().addAll(enabledBox, typeDropdown, weightLabel, weightField,
                noiseParamsSection, stepsParamsSection, removeButton);
    }

    private String titleFor(HeightmapGeneratorType type) {
        return switch (type) {
            case NOISE -> "Noise Generator";
            case STEPS -> "Steps Generator";
        };
    }
}
