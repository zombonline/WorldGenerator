package uk.bradleyjones.worldgenerator.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;


import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;
public class DecorationListUIComponent implements Commitable {

    private VBox instancesBox;
    private VBox root;
    private int instanceCount = 0;
    private final List<DecorationInstance> pendingDecorations = new ArrayList<>();

    public DecorationListUIComponent() {
        setUp();
        CommitRegistry.register(this);
    }

    public VBox get() {
        return root;
    }

    private void addInstanceUI(DecorationInstance instance) {
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane pane = new DecorationInstanceUIComponent(instance, instancesBox, () -> {
            pendingDecorations.remove(instance);
            refresh();
        }).get();
        pane.setStyle(style);
        instancesBox.getChildren().add(pane);
        instanceCount++;
    }

    public void refresh() {
        instancesBox.getChildren().clear();
        instanceCount = 0;
        for (DecorationInstance instance : world.getDecorationInstances()) {
            addInstanceUI(instance);
        }
        for(DecorationInstance instance : pendingDecorations) {
            addInstanceUI(instance);
        }
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Decoration");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            DecorationInstance instance = new DecorationInstance(new Decoration(), true);
            pendingDecorations.add(instance);
            addInstanceUI(instance);
        });

        ComboBox<Decoration> defaultsDropdown = new ComboBox<>();
        defaultsDropdown.getItems().addAll(Decoration.defaults());
        defaultsDropdown.setMaxWidth(Double.MAX_VALUE);
        defaultsDropdown.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Decoration d) { return d == null ? "" : d.desc; }
            @Override public Decoration fromString(String s) { return null; }
        });
        defaultsDropdown.setPromptText("Add default decoration...");
        defaultsDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Decoration item, boolean empty) {
                super.updateItem(item, empty);
                setText(defaultsDropdown.getPromptText());
            }
        });
        defaultsDropdown.valueProperty().addListener((obs, o, selected) -> {
            if (selected == null) return;
            DecorationInstance instance = new DecorationInstance(new Decoration(selected), true);
            pendingDecorations.add(instance);
            addInstanceUI(instance);
            Platform.runLater(() -> {
                defaultsDropdown.setValue(null);
            }
            );
        });

        for (DecorationInstance instance : world.getDecorationInstances()) {
            addInstanceUI(instance);
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }

    @Override
    public void commit() {
        world.getDecorationInstances().addAll(pendingDecorations);
        pendingDecorations.clear();
    }
}