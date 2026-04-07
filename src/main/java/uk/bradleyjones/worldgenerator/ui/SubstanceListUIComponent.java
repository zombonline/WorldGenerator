package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceListUIComponent {

    private VBox instancesBox;
    private VBox root;

    public SubstanceListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Substance Rule");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            SubstanceRule rule = new SubstanceRule();
            world.addSubstanceRule(rule);
            instancesBox.getChildren().add(new SubstanceRuleUIComponent(rule, instancesBox).get());
        });

        for (SubstanceRule rule : world.getSubstanceRules()) {
            instancesBox.getChildren().add(new SubstanceRuleUIComponent(rule, instancesBox).get());
        }

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
            world.addSubstanceRule(copy);
            instancesBox.getChildren().add(new SubstanceRuleUIComponent(copy, instancesBox).get());
            defaultsDropdown.setValue(null);
        });

        root.getChildren().addAll(addButton, defaultsDropdown, instancesBox);

    }
}