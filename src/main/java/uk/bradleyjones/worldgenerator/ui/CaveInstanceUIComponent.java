package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.ui.commitables.CommitRegistry;
import uk.bradleyjones.worldgenerator.ui.commitables.Commitable;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class CaveInstanceUIComponent implements Commitable {

    private final CaveGeneratorInstance instance;
    private final VBox parentContainer;

    private TitledPane pane;
    private VBox params;
    private Runnable onRemove;

    private RandomizableField fillField, iterField, threshField;
    private RandomizableField scaleXField, scaleYField, lowThreshField, uppThreshField;
    private RandomizableField walkerCountField, stepsField;
    private CheckBox enabledBox;
    private ComboBox<CaveGeneratorType> typeDropdown;
    private CheckBox effectsSurfaceBox;

    public CaveInstanceUIComponent(CaveGeneratorInstance instance, VBox parentContainer, Runnable onRemove) {
        this.instance = instance;
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
        pane = new TitledPane(instance.desc, params);
        pane.setAnimated(true);
        pane.setExpanded(false);

        //desc textField
        Label descLabel = new Label("Description");
        TextField descField = new TextField(instance.desc);
        descField.textProperty().addListener((obs, o, n) -> {
            instance.desc = n;
            pane.setText(n);
        });

        // Type dropdown
        typeDropdown = new ComboBox<>();
        typeDropdown.getItems().addAll(CaveGeneratorType.CA, CaveGeneratorType.NOISE, CaveGeneratorType.DRUNKARD);
        typeDropdown.setValue(instance.type);
        typeDropdown.setMaxWidth(Double.MAX_VALUE);

        // Enabled checkbox
        enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(instance.enabled);
        enabledBox.selectedProperty().addListener((obs, o, n) -> instance.enabled = n);

        // Effects surface checkbox
        effectsSurfaceBox = new CheckBox("Effects Surface");
        effectsSurfaceBox.setSelected(instance.getConfig().effectsSurface);
        effectsSurfaceBox.selectedProperty().addListener((obs, o, n) -> {
            instance.caConfig.effectsSurface = n;
            instance.noiseConfig.effectsSurface = n;
            instance.drunkardConfig.effectsSurface = n;
        });

        // CA params
        VBox caParamsSection = new VBox(4);
        Label fillLabel = new Label("Fill Percent");
        fillField = new RandomizableField();
        fillField.setType("Integer");
        fillField.setMin(1);
        fillField.setMax(100);
        fillField.setValue(String.valueOf(instance.caConfig.fillPercent));
        fillField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.fillPercent = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label iterLabel = new Label("Iterations");
        iterField = new RandomizableField();
        iterField.setType("Integer");
        iterField.setMin(1);
        iterField.setMax(10);
        iterField.setValue(String.valueOf(instance.caConfig.iterations));
        iterField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.iterations = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label threshLabel = new Label("Neighbour Threshold");
        threshField = new RandomizableField();
        threshField.setType("Integer");
        threshField.setMin(1);
        threshField.setMax(9);
        threshField.setValue(String.valueOf(instance.caConfig.neighborThreshold));
        threshField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.caConfig.neighborThreshold = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        caParamsSection.getChildren().addAll(fillLabel, fillField, iterLabel, iterField, threshLabel, threshField);

        // Noise params
        VBox noiseParamsSection = new VBox(4);
        Label scaleXLabel = new Label("Scale X");
        scaleXField = new RandomizableField();
        scaleXField.setType("Float");
        scaleXField.setMin(0.5);
        scaleXField.setMax(4);
        scaleXField.setValue(String.valueOf(instance.noiseConfig.scaleX));
        scaleXField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.scaleX = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label scaleYLabel = new Label("Scale Y");
        scaleYField = new RandomizableField();
        scaleYField.setType("Float");
        scaleYField.setMin(0.5);
        scaleYField.setMax(4);
        scaleYField.setValue(String.valueOf(instance.noiseConfig.scaleY));
        scaleYField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.scaleY = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label lowThreshLabel = new Label("Lower Threshold");
        lowThreshField = new RandomizableField();
        lowThreshField.setType("Float");
        lowThreshField.setMin(-1);
        lowThreshField.setMax(1);
        lowThreshField.setValue(String.valueOf(instance.noiseConfig.lowerThreshold));
        lowThreshField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.lowerThreshold = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        Label uppThreshLabel = new Label("Upper Threshold");
        uppThreshField = new RandomizableField();
        uppThreshField.setType("Float");
        uppThreshField.setMin(-1);
        uppThreshField.setMax(1);
        uppThreshField.setValue(String.valueOf(instance.noiseConfig.upperThreshold));
        uppThreshField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.noiseConfig.upperThreshold = Float.parseFloat(n); }
            catch (NumberFormatException ignored) {}
        });
        noiseParamsSection.getChildren().addAll(scaleXLabel, scaleXField, scaleYLabel, scaleYField,
                lowThreshLabel, lowThreshField, uppThreshLabel, uppThreshField);

        // Drunkard params
        VBox drunkardParamsSection = new VBox(4);
        Label walkerCountLabel = new Label("Walker Count");
        walkerCountField = new RandomizableField();
        walkerCountField.setType("Integer");
        walkerCountField.setMin(100);
        walkerCountField.setMax(800);
        walkerCountField.setValue(String.valueOf(instance.drunkardConfig.walkerCount));
        walkerCountField.getField().textProperty().addListener((obs, o, n) -> {
            try { instance.drunkardConfig.walkerCount = Integer.parseInt(n); }
            catch (NumberFormatException ignored) {}
        });
        Label stepsLabel = new Label("Walker Steps");
        stepsField = new RandomizableField();
        stepsField.setType("Integer");
        stepsField.setMin(10);
        stepsField.setMax(250);
        stepsField.setValue(String.valueOf(instance.drunkardConfig.steps));

        drunkardParamsSection.getChildren().addAll(walkerCountLabel, walkerCountField, stepsLabel, stepsField);

        // Set initial visibility
        caParamsSection.setVisible(instance.type == CaveGeneratorType.CA);
        caParamsSection.setManaged(instance.type == CaveGeneratorType.CA);
        noiseParamsSection.setVisible(instance.type == CaveGeneratorType.NOISE);
        noiseParamsSection.setManaged(instance.type == CaveGeneratorType.NOISE);
        drunkardParamsSection.setVisible(instance.type == CaveGeneratorType.DRUNKARD);
        drunkardParamsSection.setManaged(instance.type == CaveGeneratorType.DRUNKARD);

        // Type dropdown listener
        typeDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            instance.type = newVal;
            caParamsSection.setVisible(newVal == CaveGeneratorType.CA);
            caParamsSection.setManaged(newVal == CaveGeneratorType.CA);
            noiseParamsSection.setVisible(newVal == CaveGeneratorType.NOISE);
            noiseParamsSection.setManaged(newVal == CaveGeneratorType.NOISE);
            drunkardParamsSection.setVisible(newVal == CaveGeneratorType.DRUNKARD);
            drunkardParamsSection.setManaged(newVal == CaveGeneratorType.DRUNKARD);
            pane.setText(switch (newVal) {
                case CA -> "CA Cave Generator";
                case NOISE -> "Noise Cave Generator";
                case DRUNKARD -> "Drunkard Cave Generator";
            });
        });

        // Remove button
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-base:#A82A2A");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            world.getCaveInstances().remove(instance);
            parentContainer.getChildren().remove(pane);
            onRemove.run();
            CommitRegistry.unregister(this);
        });

        params.getChildren().addAll(descLabel, descField, enabledBox, effectsSurfaceBox, typeDropdown,
                caParamsSection, noiseParamsSection, drunkardParamsSection, removeButton);
    }

    @Override
    public void commit() {
        try { instance.caConfig.fillPercent = Integer.parseInt(fillField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.caConfig.iterations = Integer.parseInt(iterField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.caConfig.neighborThreshold = Integer.parseInt(threshField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseConfig.scaleX = Float.parseFloat(scaleXField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseConfig.scaleY = Float.parseFloat(scaleYField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseConfig.lowerThreshold = Float.parseFloat(lowThreshField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.noiseConfig.upperThreshold = Float.parseFloat(uppThreshField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.drunkardConfig.walkerCount = Integer.parseInt(walkerCountField.getValue()); }
        catch (NumberFormatException ignored) {}

        try { instance.drunkardConfig.steps = Integer.parseInt(stepsField.getValue()); }
        catch (NumberFormatException ignored) {}
    }
}
