package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class CaveListUIComponent {

    private VBox instancesBox;
    private VBox root;
    private int instanceCount = 0;

    public CaveListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void addInstance(CaveGeneratorInstance instance) {
        //CAVE BASE 7D9C19
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane pane = new CaveInstanceUIComponent(instance, instancesBox, this::refresh).get();
        pane.setStyle(style);
        instancesBox.getChildren().add(pane);
        instanceCount++;
    }
    public void refresh() {
        instancesBox.getChildren().clear();
        instanceCount = 0;
        for (CaveGeneratorInstance instance : world.getCaveInstances()) {
            addInstance(instance);
        }
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Cave Generator");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            CaveGeneratorInstance instance = new CaveGeneratorInstance("New CA Cave Generator", CaveGeneratorType.CA);
            world.getCaveInstances().add(instance);
            addInstance(instance);
        });

        ComboBox<CaveGeneratorInstance> defaultsDropdown = new ComboBox<>();
        defaultsDropdown.getItems().addAll(CaveGeneratorInstance.defaults());
        defaultsDropdown.setMaxWidth(Double.MAX_VALUE);
        defaultsDropdown.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(CaveGeneratorInstance c) { return c == null ? "" : c.desc; }
            @Override public CaveGeneratorInstance fromString(String s) { return null; }
        });
        defaultsDropdown.setPromptText("Add default cave...");

        defaultsDropdown.valueProperty().addListener((obs, o, selected) -> {
            if (selected == null) return;
            CaveGeneratorInstance instance = new CaveGeneratorInstance(selected);
            world.getCaveInstances().add(instance);
            addInstance(instance);
            defaultsDropdown.setValue(null);
        });

        for (CaveGeneratorInstance instance : world.getCaveInstances()) {
            addInstance(instance);
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }
}
