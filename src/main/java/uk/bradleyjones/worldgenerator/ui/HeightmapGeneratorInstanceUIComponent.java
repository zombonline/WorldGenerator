package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGeneratorType;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGroup;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class HeightmapGeneratorInstanceUIComponent implements Commitable {

    private final HeightmapGeneratorInstance instance;
    private final HeightmapGroup parentGroup;
    private final VBox parentContainer;
    private final Runnable onRemove;

    private RandomizableField weightField, scaleField, amplitudeField, powerField;
    private RandomizableField minStepHeightField, maxStepHeightField, minGapField, maxGapField;
    private CheckBox clampBox, enabledBox;
    private ComboBox<HeightmapGeneratorType> typeDropdown;

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
        CommitRegistry.register(this);
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
        typeDropdown = new ComboBox<>();
        typeDropdown.getItems().addAll(HeightmapGeneratorType.values());
        typeDropdown.setValue(instance.type);
        typeDropdown.setMaxWidth(Double.MAX_VALUE);

        // Enabled
        enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(instance.enabled);

        // Weight
        Label weightLabel = new Label("Weight");
        weightField = new RandomizableField();
        weightField.setType("Float");
        weightField.setMin(0);
        weightField.setMax(10);
        weightField.setValue(String.valueOf(instance.weight));


        // Noise params section
        VBox noiseParamsSection = new VBox(4);
        Label scaleLabel = new Label("Scale");
        scaleField = new RandomizableField();
        scaleField.setType("Double");
        scaleField.setMin(0.1);
        scaleField.setMax(5);
        scaleField.setValue(String.valueOf(instance.noiseGenerator.getScale()));

        Label amplitudeLabel = new Label("Amplitude");
        amplitudeField = new RandomizableField();
        amplitudeField.setType("Double");
        amplitudeField.setMin(0.1);
        amplitudeField.setMax(128);
        amplitudeField.setValue(String.valueOf(instance.noiseGenerator.getAmplitude()));

        Label powerLabel = new Label("Power");
        powerField = new RandomizableField();
        powerField.setType("Double");
        powerField.setMin(0.1);
        powerField.setMax(2);
        powerField.setValue(String.valueOf(instance.noiseGenerator.getPower()));

        clampBox = new CheckBox("Clamp to Positive");
        clampBox.setSelected(instance.noiseGenerator.isClampToPositive());

        noiseParamsSection.getChildren().addAll(scaleLabel, scaleField, amplitudeLabel, amplitudeField,
                powerLabel, powerField, clampBox);

        //steps params section
        VBox stepsParamsSection = new VBox(4);
        Label minStepHeightLabel = new Label("Minimum Step Height");
        minStepHeightField = new RandomizableField();
        minStepHeightField.setType("Integer");
        minStepHeightField.setMin(1);
        minStepHeightField.setMax(10);
        minStepHeightField.setValue(String.valueOf(instance.stepGenerator.getMinStepHeight()));

        Label maxStepHeightLabel = new Label("Maximum Step Height");
        maxStepHeightField = new RandomizableField();
        maxStepHeightField.setType("Integer");
        maxStepHeightField.setMin(1);
        maxStepHeightField.setMax(10);
        maxStepHeightField.setValue(String.valueOf(instance.stepGenerator.getMaxStepHeight()));

        Label minGapLabel = new Label("Minimum Step Width");
        minGapField = new RandomizableField();
        minGapField.setType("Integer");
        minGapField.setMin(1);
        minGapField.setMax(10);
        minGapField.setValue(String.valueOf(instance.stepGenerator.getMinStepGap()));

        Label maxGapLabel = new Label("Maximum Step Width");
        maxGapField = new RandomizableField();
        maxGapField.setType("Integer");
        maxGapField.setMin(1);
        maxGapField.setMax(10);
        maxGapField.setValue(String.valueOf(instance.stepGenerator.getMaxStepGap()));

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
            noiseParamsSection.setVisible(newVal == HeightmapGeneratorType.NOISE);
            noiseParamsSection.setManaged(newVal == HeightmapGeneratorType.NOISE);
            stepsParamsSection.setVisible(newVal == HeightmapGeneratorType.STEPS);
            stepsParamsSection.setManaged(newVal == HeightmapGeneratorType.STEPS);
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
            CommitRegistry.unregister(this);
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

    @Override
    public void commit() {
        instance.enabled = enabledBox.isSelected();
        instance.setType(typeDropdown.getValue());

        try { instance.weight = Float.parseFloat(weightField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseGenerator.setScale(Double.parseDouble(scaleField.getValue())); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseGenerator.setAmplitude(Double.parseDouble(amplitudeField.getValue())); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseGenerator.setPower(Double.parseDouble(powerField.getValue())); }
        catch (NumberFormatException ignored) {}

        instance.noiseGenerator.setClampToPositive(clampBox.isSelected());

        try { instance.stepGenerator.setMinStepHeight(Integer.parseInt(minStepHeightField.getValue())); }
        catch (NumberFormatException ignored) {}

        try { instance.stepGenerator.setMaxStepHeight(Integer.parseInt(maxStepHeightField.getValue())); }
        catch (NumberFormatException ignored) {}

        try { instance.stepGenerator.setMinStepGap(Integer.parseInt(minGapField.getValue())); }
        catch (NumberFormatException ignored) {}

        try { instance.stepGenerator.setMaxStepGap(Integer.parseInt(maxGapField.getValue())); }
        catch (NumberFormatException ignored) {}
    }
}
