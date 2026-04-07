package uk.bradleyjones.worldgenerator.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.substances.SubstanceRule;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class SubstanceRuleUIComponent {

    private final SubstanceRule rule;
    private final VBox parentContainer;

    private TitledPane pane;
    private VBox params;

    public SubstanceRuleUIComponent(SubstanceRule rule, VBox parentContainer) {
        this.rule = rule;
        this.parentContainer = parentContainer;
        setUp();
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

        Label descLabel = new Label("Description");
        TextField descField = new TextField(rule.desc != null ? rule.desc : "");
        descField.textProperty().addListener((obs, o, n) -> {
            rule.desc = n;
            pane.setText(n.isBlank() ? "Substance Rule" : n);
        });
        pane.setText(rule.desc != null && !rule.desc.isBlank() ? rule.desc : "Substance Rule");

        // Output tile type
        Label outputLabel = new Label("Output Tile");
        ComboBox<TileType> outputDropdown = new ComboBox<>();
        outputDropdown.getItems().addAll(TileType.values());
        outputDropdown.setValue(rule.output);
        outputDropdown.setMaxWidth(Double.MAX_VALUE);
        outputDropdown.valueProperty().addListener((obs, o, n) -> {
            rule.output = n;
            pane.setText(n != null ? n.toString() : "Substance Rule");
        });


        // Replaces — multi-select ListView
        Label replacesLabel = new Label("Replaces");
        ListView<TileType> replacesList = new ListView<>(FXCollections.observableArrayList(TileType.values()));
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
        replacesList.getSelectionModel().getSelectedItems().addListener(
                (javafx.collections.ListChangeListener<TileType>) c -> {
                    rule.replaces = new java.util.ArrayList<>(replacesList.getSelectionModel().getSelectedItems());
                    selectedCountLabel.setText(String.valueOf((long) replacesList.getSelectionModel().getSelectedItems().size()));
                }
        );

        // Noise scale X
        Label scaleXLabel = new Label("Noise Scale X");
        TextField scaleXField = new TextField(String.valueOf(rule.noiseScaleX));
        scaleXField.textProperty().addListener((obs, o, n) -> {
            try { rule.noiseScaleX = Double.parseDouble(n); }
            catch (NumberFormatException ignored) {}
        });

        // Noise scale Y
        Label scaleYLabel = new Label("Noise Scale Y");
        TextField scaleYField = new TextField(String.valueOf(rule.noiseScaleY));
        scaleYField.textProperty().addListener((obs, o, n) -> {
            try { rule.noiseScaleY = Double.parseDouble(n); }
            catch (NumberFormatException ignored) {}
        });

        // Noise threshold
        Label thresholdLabel = new Label("Noise Threshold");
        TextField thresholdField = new TextField(String.valueOf(rule.noiseThreshold));
        thresholdField.textProperty().addListener((obs, o, n) -> {
            try { rule.noiseThreshold = Double.parseDouble(n); }
            catch (NumberFormatException ignored) {}
        });

        // Min depth
        Label minDepthLabel = new Label("Min Depth");
        TextField minDepthField = new TextField(String.valueOf(rule.minDepth));
        minDepthField.textProperty().addListener((obs, o, n) -> {
            try { rule.minDepth = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });

        // Max depth
        Label maxDepthLabel = new Label("Max Depth");
        TextField maxDepthField = new TextField(String.valueOf(rule.maxDepth));
        maxDepthField.textProperty().addListener((obs, o, n) -> {
            try { rule.maxDepth = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });

        // Requires near water
        CheckBox nearWaterBox = new CheckBox("Requires Near Water");
        nearWaterBox.setSelected(rule.requiresNearWater);
        nearWaterBox.selectedProperty().addListener((obs, o, n) -> rule.requiresNearWater = n);

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            world.removeSubstanceRule(rule);
            parentContainer.getChildren().remove(pane);
        });

        params.getChildren().addAll(
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
}