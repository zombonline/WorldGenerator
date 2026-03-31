package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.heightmap.*;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class HeightmapGroupUIComponent {

    private final HeightmapChild child; // null if this is the root group
    private final HeightmapGroup group;
    private final HeightmapGroup parentGroup; // null if root
    private final VBox parentContainer; // null if root

    private TitledPane pane;
    private VBox params;
    private VBox childrenBox;

    public HeightmapGroupUIComponent(HeightmapGroup group, HeightmapChild child,
                                     HeightmapGroup parentGroup, VBox parentContainer) {
        this.group = group;
        this.child = child;
        this.parentGroup = parentGroup;
        this.parentContainer = parentContainer;
        setUp();
    }

    // Constructor for root group (no parent)
    public HeightmapGroupUIComponent(HeightmapGroup group) {
        this(group, null, null, null);
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp() {
        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");

        String title = parentGroup == null ? "Root Heightmap Group" : "Sub Group";
        pane = new TitledPane(title, params);
        pane.setAnimated(true);
        pane.setExpanded(true);

        // Enabled (only for non-root)
        if (child != null) {
            CheckBox enabledBox = new CheckBox("Enabled");
            enabledBox.setSelected(child.enabled);
            enabledBox.selectedProperty().addListener((obs, o, n) -> child.enabled = n);

            Label weightLabel = new Label("Weight");
            TextField weightField = new TextField(String.valueOf(child.weight));
            weightField.textProperty().addListener((obs, o, n) -> {
                try { child.weight = Float.parseFloat(n); }
                catch (NumberFormatException ignored) {}
            });

            params.getChildren().addAll(enabledBox, weightLabel, weightField);
        }

        // Combine mode
        Label modeLabel = new Label("Combine Mode");
        ComboBox<CombineMode> modeDropdown = new ComboBox<>();
        modeDropdown.getItems().addAll(CombineMode.values());
        modeDropdown.setValue(group.mode);
        modeDropdown.setMaxWidth(Double.MAX_VALUE);
        modeDropdown.valueProperty().addListener((obs, o, n) -> group.mode = n);

        // Noise scale (for NOISE_BLEND)
        Label noiseScaleLabel = new Label("Blend Noise Scale");
        TextField noiseScaleField = new TextField(String.valueOf(group.noiseScale));
        noiseScaleField.textProperty().addListener((obs, o, n) -> {
            try { group.noiseScale = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        noiseScaleLabel.setVisible(group.mode == CombineMode.NOISE_BLEND);
        noiseScaleLabel.setManaged(group.mode == CombineMode.NOISE_BLEND);
        noiseScaleField.setVisible(group.mode == CombineMode.NOISE_BLEND);
        noiseScaleField.setManaged(group.mode == CombineMode.NOISE_BLEND);
        modeDropdown.valueProperty().addListener((obs, o, n) -> {
            noiseScaleLabel.setVisible(n == CombineMode.NOISE_BLEND);
            noiseScaleLabel.setManaged(n == CombineMode.NOISE_BLEND);
            noiseScaleField.setVisible(n == CombineMode.NOISE_BLEND);
            noiseScaleField.setManaged(n == CombineMode.NOISE_BLEND);
        });

        // Children box
        childrenBox = new VBox(4);

        // Populate existing children
        for (HeightmapChild c : group.children) {
            addChildUI(c);
        }

        // Add noise generator button
        Button addNoiseButton = new Button("+ Add Noise Generator");
        addNoiseButton.setMaxWidth(Double.MAX_VALUE);
        addNoiseButton.setOnAction(e -> {
            NoiseHeightmapGenerator gen = new NoiseHeightmapGenerator(
                    world.getWorldConfig().seed, 0.1, 64, 1.0, false
            );
            HeightmapChild newChild = new HeightmapChild(gen);
            group.children.add(newChild);
            HeightmapNoiseGeneratorUIComponent component =
                    new HeightmapNoiseGeneratorUIComponent(newChild, group, childrenBox);
            childrenBox.getChildren().add(component.get());
        });

        // Add sub-group button
        Button addGroupButton = new Button("+ Add Sub Group");
        addGroupButton.setMaxWidth(Double.MAX_VALUE);
        addGroupButton.setOnAction(e -> {
            HeightmapGroup subGroup = new HeightmapGroup(CombineMode.ADDITIVE, world.getWorldConfig().seed);
            HeightmapChild newChild = new HeightmapChild(subGroup);
            group.children.add(newChild);
            HeightmapGroupUIComponent component =
                    new HeightmapGroupUIComponent(subGroup, newChild, group, childrenBox);
            childrenBox.getChildren().add(component.get());
        });

        // Remove button (only for non-root)
        if (parentGroup != null) {
            Button removeButton = new Button("Remove Group");
            removeButton.setMaxWidth(Double.MAX_VALUE);
            removeButton.setOnAction(e -> {
                parentGroup.children.remove(child);
                parentContainer.getChildren().remove(pane);
            });
            params.getChildren().add(removeButton);
        }

        params.getChildren().addAll(
                modeLabel, modeDropdown,
                noiseScaleLabel, noiseScaleField,
                childrenBox,
                addNoiseButton, addGroupButton
        );
    }

    private void addChildUI(HeightmapChild child) {
        if (child.node instanceof NoiseHeightmapGenerator gen) {
            HeightmapNoiseGeneratorUIComponent component =
                    new HeightmapNoiseGeneratorUIComponent(child, group, childrenBox);
            childrenBox.getChildren().add(component.get());
        } else if (child.node instanceof HeightmapGroup subGroup) {
            HeightmapGroupUIComponent component =
                    new HeightmapGroupUIComponent(subGroup, child, group, childrenBox);
            childrenBox.getChildren().add(component.get());
        }
    }
}