package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class CaveListUIComponent {

    private VBox instancesBox;
    private VBox root;

    public CaveListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Cave Generator");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            CaveGeneratorInstance instance = new CaveGeneratorInstance("New CA Cave Generator", CaveGeneratorType.CA);
            world.getCaveInstances().add(instance);
            instancesBox.getChildren().add(new CaveInstanceUIComponent(instance, instancesBox).get());
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
            instancesBox.getChildren().add(new CaveInstanceUIComponent(instance, instancesBox).get());
            defaultsDropdown.setValue(null);
        });


        for (CaveGeneratorInstance instance : world.getCaveInstances()) {
            instancesBox.getChildren().add(new CaveInstanceUIComponent(instance, instancesBox).get());
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }
}
