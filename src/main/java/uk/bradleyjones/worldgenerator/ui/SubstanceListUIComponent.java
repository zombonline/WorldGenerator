package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceListUIComponent {

    private VBox instancesBox;
    private VBox root;
    private int instanceCount = 0;

    public SubstanceListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void addInstance(SubstanceRule rule) {
        //24199C
        String style = instanceCount % 2 == 0
                ? "-fx-base: #696969;"
                : "-fx-base: #3D3D3D;";
        TitledPane pane = new SubstanceRuleUIComponent(rule, instancesBox, this::refresh).get();
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
            world.getSubstanceRules().add(rule);
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
        defaultsDropdown.valueProperty().addListener((obs, o, selected) -> {
            if (selected == null) return;
            SubstanceRule copy = new SubstanceRule(selected);
            world.getSubstanceRules().add(copy);
            addInstance(copy);
            defaultsDropdown.setValue(null);
        });

        for (SubstanceRule rule : world.getSubstanceRules()) {
            addInstance(rule);
        }

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);
    }
}