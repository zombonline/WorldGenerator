package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;


import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;
public class DecorationListUIComponent {

    private VBox instancesBox;
    private VBox root;
    private int instanceCount = 0;

    public DecorationListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void addInstance(DecorationInstance instance) {
        //decoration base 209C19
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane pane = new DecorationInstanceUIComponent(instance, instancesBox, this::refresh).get();
        pane.setStyle(style);
        instancesBox.getChildren().add(pane);
        instanceCount++;
    }

    public void refresh() {
        instancesBox.getChildren().clear();
        instanceCount = 0;
        for (DecorationInstance instance : world.getDecorationInstances()) {
            addInstance(instance);
        }
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Decoration");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            DecorationInstance instance = new DecorationInstance(new Decoration(), true);
            world.getDecorationInstances().add(instance);
            addInstance(instance);
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
            addInstance(instance);
            defaultsDropdown.setValue(null);
        });

        for (DecorationInstance instance : world.getDecorationInstances()) {
            addInstance(instance);
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }
}