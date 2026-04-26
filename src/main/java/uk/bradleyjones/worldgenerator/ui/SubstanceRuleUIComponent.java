package uk.bradleyjones.worldgenerator.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceRuleUIComponent implements Commitable {

    private final SubstanceRule rule;
    private final VBox parentContainer;

    private TitledPane pane;
    private VBox params;
    private Runnable onRemove;

    private CheckBox enabledBox, nearWaterBox;
    private TextField descField;
    private RandomizableField scaleXField, scaleYField, thresholdField, minDepthField, maxDepthField;
    private ComboBox<TileType> outputDropdown;
    private ListView<TileType> replacesList;

    public SubstanceRuleUIComponent(SubstanceRule rule, VBox parentContainer, Runnable onRemove) {
        this.rule = rule;
        this.parentContainer = parentContainer;
        this.onRemove = onRemove;
        setUp();
        CommitRegistry.register(this);
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp() {
        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");
        pane = new TitledPane("Substance Rule", params);
        pane.setAnimated(true);
        pane.setExpanded(false);

        // Enabled
        enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(rule.enabled);
        enabledBox.selectedProperty().addListener((obs, o, n) -> rule.enabled = n);


        Label descLabel = new Label("Description");
        descField = new TextField(rule.desc != null ? rule.desc : "");
        descField.textProperty().addListener((obs, o, n) -> {
            rule.desc = n;
            pane.setText(n.isBlank() ? "Substance Rule" : n);
        });
        pane.setText(rule.desc != null && !rule.desc.isBlank() ? rule.desc : "Substance Rule");

        // Output tile type
        Label outputLabel = new Label("Output Tile");
        outputDropdown = new ComboBox<>();
        outputDropdown.getItems().addAll(TileType.values());
        outputDropdown.setValue(rule.output);
        outputDropdown.setMaxWidth(Double.MAX_VALUE);


        Label replacesLabel = new Label("Replaces");
        replacesList = new ListView<>(FXCollections.observableArrayList(TileType.values()));
        Label selectedCountLabel = new Label(String.valueOf((long) replacesList.getSelectionModel().getSelectedItems().size()));
        HBox selectedCountBox = new HBox(selectedCountLabel);
        selectedCountLabel.setAlignment(Pos.CENTER_RIGHT);
        selectedCountBox.setStyle("-fx-font-size: 10px;");
        replacesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        replacesList.setPrefHeight(100);
        if (rule.replaces != null) {
            for (TileType t : rule.replaces) {
                replacesList.getSelectionModel().select(t);
            }
            selectedCountLabel.setText(String.valueOf((long) replacesList.getSelectionModel().getSelectedItems().size()));
        }

        // Noise scale X
        Label scaleXLabel = new Label("Noise Scale X");
        scaleXField = new RandomizableField();
        scaleXField.setType("Double");
        scaleXField.setMin(0.001d);
        scaleXField.setMax(2d);
        scaleXField.setValue(String.valueOf(rule.noiseScaleX));



        // Noise scale Y
        Label scaleYLabel = new Label("Noise Scale Y");
        scaleYField = new RandomizableField();
        scaleYField.setType("Double");
        scaleYField.setMin(0.001d);
        scaleYField.setMax(2d);
        scaleYField.setValue(String.valueOf(rule.noiseScaleY));

        // Noise threshold
        Label thresholdLabel = new Label("Noise Threshold");
        thresholdField = new RandomizableField();
        thresholdField.setType("Double");
        thresholdField.setMin(0d);
        thresholdField.setMax(1d);
        thresholdField.setValue(String.valueOf(rule.noiseThreshold));


        // Min depth
        Label minDepthLabel = new Label("Min Depth");
        minDepthField = new RandomizableField();
        minDepthField.setType("Integer");
        minDepthField.setMin(0);
        minDepthField.setMax(500);
        minDepthField.setValue(String.valueOf(rule.minDepth));


        // Max depth
        Label maxDepthLabel = new Label("Max Depth");
        maxDepthField = new RandomizableField();
        maxDepthField.setType("Integer");
        maxDepthField.setMin(100);
        maxDepthField.setMax(800);
        maxDepthField.setValue(String.valueOf(rule.maxDepth));


        // Requires near water
        nearWaterBox = new CheckBox("Requires Near Water");
        nearWaterBox.setSelected(rule.requiresNearWater);

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-base:#A82A2A");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            world.getSubstanceRules().remove(rule);
            parentContainer.getChildren().remove(pane);
            onRemove.run();
            CommitRegistry.unregister(this);
        });

        params.getChildren().addAll(
                enabledBox,
                descLabel,descField,
                outputLabel, outputDropdown,
                replacesLabel, replacesList,
                selectedCountBox,
                scaleXLabel, scaleXField,
                scaleYLabel, scaleYField,
                thresholdLabel, thresholdField,
                minDepthLabel, minDepthField,
                maxDepthLabel, maxDepthField,
                nearWaterBox,
                removeButton
        );
    }

    @Override
    public void commit() {
        rule.enabled = enabledBox.isSelected();
        rule.desc = descField.getText();
        rule.output = outputDropdown.getValue();
        rule.requiresNearWater = nearWaterBox.isSelected();

        try { rule.noiseScaleX = Double.parseDouble(scaleXField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { rule.noiseScaleY = Double.parseDouble(scaleYField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { rule.noiseThreshold = Double.parseDouble(thresholdField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { rule.minDepth = Integer.parseInt(minDepthField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { rule.maxDepth = Integer.parseInt(maxDepthField.getValue()); }
        catch (NumberFormatException ignored) {}
    }
}