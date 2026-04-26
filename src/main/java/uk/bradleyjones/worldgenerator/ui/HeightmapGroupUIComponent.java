package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.heightmap.*;

import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class HeightmapGroupUIComponent implements Commitable {

    private final HeightmapChild child; // null if this is the root group
    private final HeightmapGroup group;
    private final HeightmapGroup parentGroup; // null if root
    private final VBox parentContainer; // null if root
    private final Runnable onRemove;

    private TitledPane pane;
    private VBox params;
    private VBox childrenBox;

    private int instanceCount = 0;

    private CheckBox enabledBox;
    private RandomizableField weightField, noiseScaleField;
    private ComboBox<CombineMode> modeDropdown;
    private final List<HeightmapChild> pendingChildren = new ArrayList<>();


    public HeightmapGroupUIComponent(HeightmapGroup group, HeightmapChild child,
                                     HeightmapGroup parentGroup, VBox parentContainer, Runnable onRemove) {
        this.group = group;
        this.child = child;
        this.parentGroup = parentGroup;
        this.parentContainer = parentContainer;
        this.onRemove = onRemove;
        setUp();
        CommitRegistry.register(this);
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
            enabledBox = new CheckBox("Enabled");
            enabledBox.setSelected(child.enabled);

            Label weightLabel = new Label("Weight");
            weightField = new RandomizableField();
            weightField.setType("Float");
            weightField.setMin(0);
            weightField.setMax(1);
            weightField.setValue(String.valueOf(child.weight));


            params.getChildren().addAll(enabledBox, weightLabel, weightField);
        }

        // Combine mode
        Label modeLabel = new Label("Combine Mode");
        modeDropdown = new ComboBox<>();
        modeDropdown.getItems().addAll(CombineMode.values());
        modeDropdown.setValue(group.mode);
        modeDropdown.setMaxWidth(Double.MAX_VALUE);

        // Noise scale (for NOISE_BLEND)
        Label noiseScaleLabel = new Label("Blend Noise Scale");
        noiseScaleField = new RandomizableField();
        noiseScaleField.setType("Float");
        noiseScaleField.setMin(.001f);
        noiseScaleField.setMax(2f);
        noiseScaleField.setValue(String.valueOf(group.noiseScale));


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
            pendingChildren.add(instance);
            addChildUI(instance);
        });

        // Add sub-group button
        Button addGroupButton = new Button("+ Add Sub Group");
        addGroupButton.setMaxWidth(Double.MAX_VALUE);
        addGroupButton.setOnAction(e -> {
            HeightmapGroup subGroup = new HeightmapGroup(CombineMode.ADDITIVE, world.getWorldConfig().seed);
            HeightmapChild newChild = new HeightmapChild(subGroup);
            pendingChildren.add(newChild);
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
                CommitRegistry.unregister(this);
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
            newPane = new HeightmapGeneratorInstanceUIComponent(instance, group, childrenBox, () -> {
                pendingChildren.remove(child);
                refresh();
            }).get();
        else if (child.node instanceof HeightmapGroup subGroup)
            newPane = new HeightmapGroupUIComponent(subGroup, child, group, childrenBox, () -> {
                pendingChildren.remove(child);
                refresh();
            }).get();
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
        for(var child:pendingChildren){
            addChildUI(child);
        }
    }


    @Override
    public void commit() {
        group.children.addAll(pendingChildren);
        pendingChildren.clear();

        // Child-specific fields (non-root only)
        if (child != null) {
            child.enabled = enabledBox.isSelected();

            try {
                child.weight = Float.parseFloat(weightField.getValue());
            } catch (NumberFormatException ignored) {}
        }

        // Group fields
        group.mode = modeDropdown.getValue();

        try {
            group.noiseScale = Float.parseFloat(noiseScaleField.getValue());
        } catch (NumberFormatException ignored) {}
    }
}

