package uk.bradleyjones.worldgenerator.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;

import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class CaveListUIComponent implements Commitable {

    private VBox instancesBox;
    private VBox root;
    private int instanceCount = 0;

    private final List<CaveGeneratorInstance> pendingCaveGenerators = new ArrayList<>();

    public CaveListUIComponent() {
        setUp();
        CommitRegistry.register(this);
    }

    public VBox get() {
        return root;
    }

    private void addInstanceUI(CaveGeneratorInstance instance) {
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane pane = new CaveInstanceUIComponent(instance, instancesBox, () -> {
            pendingCaveGenerators.remove(instance);
            refresh();
        }).get();
        pane.setStyle(style);
        instancesBox.getChildren().add(pane);
        instanceCount++;
    }
    public void refresh() {
        instancesBox.getChildren().clear();
        instanceCount = 0;
        for (CaveGeneratorInstance instance : world.getCaveInstances()) {
            addInstanceUI(instance);
        }
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Cave Generator");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            CaveGeneratorInstance instance = new CaveGeneratorInstance("New CA Cave Generator", CaveGeneratorType.CA);
            pendingCaveGenerators.add(instance);
            addInstanceUI(instance);
        });

        ComboBox<CaveGeneratorInstance> defaultsDropdown = new ComboBox<>();
        defaultsDropdown.getItems().addAll(CaveGeneratorInstance.defaults());
        defaultsDropdown.setMaxWidth(Double.MAX_VALUE);
        defaultsDropdown.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(CaveGeneratorInstance c) { return c == null ? "" : c.desc; }
            @Override public CaveGeneratorInstance fromString(String s) { return null; }
        });
        defaultsDropdown.setPromptText("Add default cave...");
        defaultsDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CaveGeneratorInstance item, boolean empty) {
                super.updateItem(item, empty);
                setText(defaultsDropdown.getPromptText());
            }
        });

        defaultsDropdown.valueProperty().addListener((obs, o, selected) -> {
            if (selected == null) return;
            CaveGeneratorInstance instance = new CaveGeneratorInstance(selected);
            pendingCaveGenerators.add(instance);
            addInstanceUI(instance);
            Platform.runLater(() -> defaultsDropdown.setValue(null));
        });

        for (CaveGeneratorInstance instance : world.getCaveInstances()) {
            addInstanceUI(instance);
        }
        for(CaveGeneratorInstance instance : pendingCaveGenerators) {
            addInstanceUI(instance);
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }

    @Override
    public void commit() {
        world.getCaveInstances().addAll(pendingCaveGenerators);
        pendingCaveGenerators.clear();
    }
}
