package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapChild;
import uk.bradleyjones.worldgenerator.world.heightmap.HeightmapGroup;
import uk.bradleyjones.worldgenerator.world.heightmap.NoiseHeightmapGenerator;

public class HeightmapNoiseGeneratorUIComponent {

    private final HeightmapChild child;
    private final HeightmapGroup parentGroup;
    private final VBox parentContainer;

    private TitledPane pane;
    private VBox params;

    public HeightmapNoiseGeneratorUIComponent(HeightmapChild child, HeightmapGroup parentGroup, VBox parentContainer) {
        this.child = child;
        this.parentGroup = parentGroup;
        this.parentContainer = parentContainer;
        setUp();
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp() {
        NoiseHeightmapGenerator gen = (NoiseHeightmapGenerator) child.node;

        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");
        pane = new TitledPane("Noise Generator", params);
        pane.setAnimated(true);
        pane.setExpanded(false);

        // Enabled
        CheckBox enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(child.enabled);
        enabledBox.selectedProperty().addListener((obs, o, n) -> child.enabled = n);

        // Weight (always shown, only meaningful in NOISE_BLEND parent)
        Label weightLabel = new Label("Weight");
        TextField weightField = new TextField(String.valueOf(child.weight));
        weightField.textProperty().addListener((obs, o, n) -> {
            try { child.weight = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });

        // Scale
        Label scaleLabel = new Label("Scale");
        TextField scaleField = new TextField(String.valueOf(gen.getScale()));
        scaleField.textProperty().addListener((obs, o, n) -> {
            try { gen.setScale(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });

        // Amplitude
        Label amplitudeLabel = new Label("Amplitude");
        TextField amplitudeField = new TextField(String.valueOf(gen.getAmplitude()));
        amplitudeField.textProperty().addListener((obs, o, n) -> {
            try { gen.setAmplitude(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });

        // Power
        Label powerLabel = new Label("Power");
        TextField powerField = new TextField(String.valueOf(gen.getPower()));
        powerField.textProperty().addListener((obs, o, n) -> {
            try { gen.setPower(Double.parseDouble(n)); }
            catch (NumberFormatException ignored) {}
        });

        // Clamp to positive
        CheckBox clampBox = new CheckBox("Clamp to Positive");
        clampBox.setSelected(gen.isClampToPositive());
        clampBox.selectedProperty().addListener((obs, o, n) -> gen.setClampToPositive(n));

        // Remove
        Button removeButton = new Button("Remove");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            parentGroup.children.remove(child);
            parentContainer.getChildren().remove(pane);
        });

        params.getChildren().addAll(
                enabledBox,
                weightLabel, weightField,
                scaleLabel, scaleField,
                amplitudeLabel, amplitudeField,
                powerLabel, powerField,
                clampBox,
                removeButton
        );
    }
}