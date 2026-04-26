package uk.bradleyjones.worldgenerator.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;

import java.util.ArrayList;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceListUIComponent implements Commitable {

    private VBox instancesBox;
    private VBox root;
    private int instanceCount = 0;

    private final List<SubstanceRule> pendingRules = new ArrayList<>();

    public SubstanceListUIComponent() {
        setUp();
        CommitRegistry.register(this);
    }

    public VBox get() {
        return root;
    }

    private void addInstance(SubstanceRule rule) {
        //24199C
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane pane = new SubstanceRuleUIComponent(rule, instancesBox, () -> {
            pendingRules.remove(rule);
            refresh();
        }).get();
        pane.setStyle(style);
        instancesBox.getChildren().add(pane);
        instanceCount++;
    }

    public void refresh() {
        instancesBox.getChildren().clear();
        instanceCount = 0;
        for (SubstanceRule rule : world.getSubstanceRules()) {
            addInstance(rule);
        }
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Substance Rule");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            SubstanceRule rule = new SubstanceRule();
            pendingRules.add(rule);
            addInstance(rule);
        });

        ComboBox<SubstanceRule> defaultsDropdown = new ComboBox<>();
        defaultsDropdown.getItems().addAll(SubstanceRule.defaults());
        defaultsDropdown.setMaxWidth(Double.MAX_VALUE);
        defaultsDropdown.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(SubstanceRule r) { return r == null ? "" : r.desc; }
            @Override public SubstanceRule fromString(String s) { return null; }
        });
        defaultsDropdown.setPromptText("Add default rule...");
        defaultsDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SubstanceRule item, boolean empty) {
                super.updateItem(item, empty);
                setText(defaultsDropdown.getPromptText());
            }
        });
        defaultsDropdown.valueProperty().addListener((obs, o, selected) -> {
            if (selected == null) return;
            SubstanceRule copy = new SubstanceRule(selected);
            pendingRules.add(copy);
            addInstance(copy);
            Platform.runLater(() -> defaultsDropdown.setValue(null));
        });

        for (SubstanceRule rule : world.getSubstanceRules()) {
            addInstance(rule);
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }

    @Override
    public void commit() {
        world.getSubstanceRules().addAll(pendingRules);
        pendingRules.clear();
    }
}