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
    private final Runnable onRemove;

    private TitledPane pane;
    private VBox params;
    private VBox childrenBox;

    private int instanceCount = 0;

    public HeightmapGroupUIComponent(HeightmapGroup group, HeightmapChild child,
                                     HeightmapGroup parentGroup, VBox parentContainer, Runnable onRemove) {
        this.group = group;
        this.child = child;
        this.parentGroup = parentGroup;
        this.parentContainer = parentContainer;
        this.onRemove = onRemove;
        setUp();
    }

    // Constructor for root group (no parent)
    public HeightmapGroupUIComponent(HeightmapGroup group) {
        this(group, null, null, null, null);
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

        // Enabled + weight (only for non-root sub-groups)
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
        for (HeightmapChild c : group.children) {
            addChildUI(c);
        }

        // Add generator button
        Button addGeneratorButton = new Button("+ Add Generator");
        addGeneratorButton.setMaxWidth(Double.MAX_VALUE);
        addGeneratorButton.setOnAction(e -> {
            HeightmapGeneratorInstance instance = new HeightmapGeneratorInstance(
                    HeightmapGeneratorType.NOISE, world.getWorldConfig().seed
            );
            group.children.add(instance);
            addChildUI(instance);
        });

        // Add sub-group button
        Button addGroupButton = new Button("+ Add Sub Group");
        addGroupButton.setMaxWidth(Double.MAX_VALUE);
        addGroupButton.setOnAction(e -> {
            HeightmapGroup subGroup = new HeightmapGroup(CombineMode.ADDITIVE, world.getWorldConfig().seed);
            HeightmapChild newChild = new HeightmapChild(subGroup);
            group.children.add(newChild);
            addChildUI(newChild);
        });

        // Remove button (only for non-root sub-groups)
        if (parentGroup != null) {
            Button removeButton = new Button("Remove Group");
            removeButton.setStyle("-fx-base:#A82A2A");
            removeButton.setMaxWidth(Double.MAX_VALUE);
            removeButton.setOnAction(e -> {
                parentGroup.children.remove(child);
                parentContainer.getChildren().remove(pane);
                onRemove.run();
            });
            params.getChildren().add(removeButton);
        }

        params.getChildren().addAll(
                modeLabel, modeDropdown,
                noiseScaleLabel, noiseScaleField,
                childrenBox,
                addGeneratorButton, addGroupButton
        );
    }

    private void addChildUI(HeightmapChild child) {
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane newPane;
        if (child instanceof HeightmapGeneratorInstance instance)
            newPane = new HeightmapGeneratorInstanceUIComponent(instance, group, childrenBox, this::refresh).get();
        else if (child.node instanceof HeightmapGroup subGroup)
            newPane = new HeightmapGroupUIComponent(subGroup, child, group, childrenBox, this::refresh).get();
        else return;

        newPane.setStyle(style);
        childrenBox.getChildren().add(newPane);
        instanceCount++;
    }

    public void refresh(){
        childrenBox.getChildren().clear();
        if(parentGroup == null){
            System.out.println("Refreshing root group, children count: " + group.children.size() + ", group combine mode: " + group.mode);
        }
        for(var child:group.children){
            addChildUI(child);
        }
    }
}
