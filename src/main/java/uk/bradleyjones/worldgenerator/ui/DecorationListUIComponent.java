package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;


import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;
public class DecorationListUIComponent {

    private VBox instancesBox;
    private VBox root;

    public DecorationListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Decoration");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            Decoration decoration = new Decoration();
            DecorationInstance instance = new DecorationInstance(decoration, true);
            world.getDecorationInstances().add(instance);
            instancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, instancesBox).get());
        });

        ComboBox<Decoration> defaultsDropdown = new ComboBox<>();
        defaultsDropdown.getItems().addAll(Decoration.defaults());
        defaultsDropdown.setMaxWidth(Double.MAX_VALUE);
        defaultsDropdown.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Decoration d) { return d == null ? "" : d.desc; }
            @Override public Decoration fromString(String s) { return null; }
        });
        defaultsDropdown.setPromptText("Add default decoration...");

        defaultsDropdown.valueProperty().addListener((obs, o, selected) -> {
            if (selected == null) return;
            DecorationInstance instance = new DecorationInstance(new Decoration(selected), true);
            world.getDecorationInstances().add(instance);
            instancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, instancesBox).get());
            defaultsDropdown.setValue(null);
        });

        for (DecorationInstance instance : world.getDecorationInstances()) {
            instancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, instancesBox).get());
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }
}