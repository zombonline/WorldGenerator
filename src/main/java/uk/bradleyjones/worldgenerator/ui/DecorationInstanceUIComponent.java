package uk.bradleyjones.worldgenerator.ui;

import com.dlsc.gemsfx.ExpandingTextArea;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.biomes.Biome;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationRepository;
import uk.bradleyjones.worldgenerator.world.decorations.PlacementType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class DecorationInstanceUIComponent {

    DecorationInstance instance;

    private VBox parent;

    // Root + layout
    private TitledPane pane;
    private Label titleLabel;
    private VBox params;
    private VBox decorationParamsSection;

    // Basic controls
    private CheckBox enabledBox;

    // Desc
    private Label descLabel;
    private TextField descField;

    // ASCII editor
    private Label asciiRowsLabel;
    private ExpandingTextArea asciiRowsField;
    private Label sizeLabel;
    private HBox sizeBox;

    // Tile key
    private Label tileKeyLabel;
    private VBox tileKeyBox;

    // Biomes
    private Label biomesLabel;
    private TextField biomesField;
    private Tooltip biomesTooltip;

    // Surface tile
    private Label requiredSurfaceTileLabel;
    private ComboBox<TileType> requiredSurfaceTileDropdown;

    // Spawn chance
    private Label chanceLabel;
    private TextField chanceField;

    // Placement type
    private Label placementTypeLabel;
    private ComboBox<PlacementType> placementTypeDropdown;

    private Button saveButton, removeButton;

    public DecorationInstanceUIComponent(DecorationInstance instance, VBox parent)
    {
        this.instance = instance;
        this.parent = parent;
        setUp();
    }

    public TitledPane get() {
        return pane;
    }

    private void setUp(){
        initRootContainers();
        initEnabledCheckbox();
        initDescField();
        initAsciiEditor();
        initTileKeyEditor();
        initBiomesField();
        initRequiredSurfaceField();
        initChanceField();
        initPlacementTypeField();
        initSaveButton();
        initRemoveButton();

        decorationParamsSection.getChildren().addAll(
                descLabel, descField,
                asciiRowsLabel, asciiRowsField,
                sizeBox,
                tileKeyLabel, tileKeyBox,
                biomesLabel, biomesField,
                requiredSurfaceTileLabel, requiredSurfaceTileDropdown,
                chanceLabel, chanceField,
                placementTypeLabel, placementTypeDropdown,
                saveButton, removeButton
        );
        params.getChildren().addAll(enabledBox, decorationParamsSection);
    }

    private void initRootContainers() {
        params = new VBox(4);
        params.setStyle("-fx-padding: 4;");
        pane = new TitledPane();
        titleLabel = new Label();
        updateTitleLabel();
        pane.setGraphic(titleLabel);
        pane.setContent(params);
        pane.setAnimated(true);
        pane.setExpanded(false);
        decorationParamsSection = new VBox(4);
    }

    private void updateTitleLabel() {
        int maxRowLength = 20; // max chars per line
        List<String> lines = new ArrayList<>();


        String desc = instance.decoration.desc;
        while (desc.length() > maxRowLength) {
            lines.add(desc.substring(0, maxRowLength - 3) + "...");
            desc = desc.substring(maxRowLength - 3);
        }
        lines.add(desc);


        if (instance.fileName != null) {
            String fileLine = "\uD83D\uDCBE " + instance.fileName;
            if(fileLine.length() > maxRowLength)
                fileLine = fileLine.substring(0, maxRowLength - 3) + "...";
            lines.add(fileLine);
        }
        titleLabel.setText(String.join("\n", lines));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
    }

    private void initEnabledCheckbox() {
        enabledBox = new CheckBox("Enabled");
        enabledBox.setSelected(instance.enabled);
        enabledBox.selectedProperty().addListener((obs, o, n) -> instance.enabled = n);
    }

    private void initDescField() {
        descLabel = new Label("Description");
        descField = new TextField(String.valueOf(instance.decoration.desc));
        descField.textProperty().addListener((obs, o, n) -> {
            instance.decoration.desc = n;
            updateTitleLabel();
        });
    }

    private void initAsciiEditor() {
        asciiRowsLabel = new Label("Decoration Shape");
        sizeLabel = new Label();
        sizeBox = new HBox(sizeLabel);
        sizeBox.setAlignment(Pos.CENTER_RIGHT);
        sizeLabel.setStyle("-fx-font-size: 10px;");
        StringBuilder asciiRowsValue = new StringBuilder();
        for(String row : instance.decoration.asciiRows) {
            asciiRowsValue.append(row+"\n");
        }
        setAsciiTextSizeLabelValue(asciiRowsValue.toString());
        asciiRowsField = new ExpandingTextArea(asciiRowsValue.toString().replace(" ", "."));
        asciiRowsField.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace;");
        asciiRowsField.textProperty().addListener((obs, o,  n) -> {
            if (!n.equals(n.replace(" ", "."))) {
                asciiRowsField.setText(n.replace(" ", "."));
                return;
            }
            String cleaned = n.replace("·", " ");
            var split = cleaned.split("\\n");
            instance.decoration.asciiRows = List.of(split);
            setAsciiTextSizeLabelValue(n);

            syncMapWithAscii();
            updateTileKeyBox();
        });
    }

    private void setAsciiTextSizeLabelValue(String n) {
        String[] rows = n.split("\\n", -1); // keep empty rows
        int height = rows.length;

        int width = 0;
        for (String row : rows) {
            width = Math.max(width, row.length());
        }
        sizeLabel.setText(String.valueOf(width) + "x" + String.valueOf(height));
    }

    private void initTileKeyEditor() {
        tileKeyLabel = new Label("Tile Key");
        tileKeyBox = new VBox(4);
    }

    private void syncMapWithAscii() {
        var map = instance.decoration.charMap;

        // Collect all chars currently used
        HashMap<Character, Boolean> used = new HashMap<>();

        for (String row : instance.decoration.asciiRows) {
            for (char c : row.toCharArray()) {
                used.put(c, true);

                // Add missing entries
                map.putIfAbsent(c, TileType.NONE);
            }
        }

        // Remove unused entries
        map.keySet().removeIf(c -> !used.containsKey(c));
    }

    private void updateTileKeyBox() {
        var map = instance.decoration.charMap;

        tileKeyBox.getChildren().clear();

        map.keySet().stream()
                .sorted()
                .forEach(key -> {

                    HBox box = new HBox(2);

                    Label charLabel = new Label(String.valueOf(key));

                    ComboBox<TileType> tileTypeComboBox = new ComboBox<>();
                    tileTypeComboBox.getItems().addAll(TileType.values());
                    tileTypeComboBox.setValue(map.get(key));
                    tileTypeComboBox.setMaxWidth(Double.MAX_VALUE);

                    tileTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        map.put(key, newVal);
                    });

                    box.getChildren().addAll(charLabel, tileTypeComboBox);
                    tileKeyBox.getChildren().add(box);
                });
    }

    public void initBiomesField() {
        biomesLabel = new Label("Allowed Biomes");
        biomesLabel.setTooltip(new Tooltip("Leave blank to allow any biome, separate with a comma"));
        biomesField = new TextField(String.join(", ", instance.decoration.allowedBiomes));
        biomesTooltip = new Tooltip();
        biomesField.setTooltip(biomesTooltip);
        biomesField.textProperty().addListener((obs, o,  n) -> {
            String[] items = n.split("\\s*,\\s*");
            List<String> invalid = new ArrayList<>();
            for(String s : items)
            {
                if(Biome.getById(s) == null) {
                    invalid.add(s);
                    break;
                }
            }
            if (!invalid.isEmpty()) {
                biomesTooltip.setText("Invalid: " + String.join(", ", invalid));
                biomesField.setStyle("-fx-border-color: red;");
            } else {
                biomesTooltip.setText(null);
                biomesField.setStyle("");
            }
        });
    }

    private void initRequiredSurfaceField() {
        requiredSurfaceTileLabel = new Label("Required Surface Tile");
        requiredSurfaceTileDropdown = new ComboBox<>();
        requiredSurfaceTileDropdown.getItems().addAll(TileType.values());
        requiredSurfaceTileDropdown.setValue(instance.decoration.requiredSurface);
        requiredSurfaceTileDropdown.setMaxWidth(Double.MAX_VALUE);
    }

    private void initChanceField() {
        chanceLabel = new Label("Chance to spawn");
        chanceField = new TextField(String.valueOf(instance.decoration.chance));
        chanceField.textProperty().addListener((obs, o, n) -> {
            instance.decoration.chance = Float.parseFloat(n);
        });
    }

    private void initPlacementTypeField() {
        placementTypeLabel = new Label("Placement Type");
        placementTypeDropdown = new ComboBox<>();
        placementTypeDropdown.getItems().addAll(PlacementType.values());
        placementTypeDropdown.setValue(instance.decoration.placementType);
        placementTypeDropdown.setMaxWidth(Double.MAX_VALUE);
    }

    private void initSaveButton() {
        saveButton = new Button("Save");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> {
            if (instance.fileName != null) {
                // Already has a file, overwrite
                DecorationRepository.save(instance.decoration, instance.fileName);
            } else {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Save Decoration");

                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Decoration Files", "*.decoration")
                );

                File dir = new File(DecorationRepository.DECORATIONS_DIR);
                if (!dir.exists()) dir.mkdirs();
                chooser.setInitialDirectory(dir);

                chooser.setInitialFileName(
                        instance.decoration.desc.replaceAll("\\s+", "_").toLowerCase() + ".decoration"
                );

                Stage stage = (Stage) saveButton.getScene().getWindow();
                File file = chooser.showSaveDialog(stage);

                if (file != null) {
                    instance.fileName = file.getName();
                    updateTitleLabel();
                    DecorationRepository.save(instance.decoration, instance.fileName);
                }
            }
        });
    }

    private void initRemoveButton() {
        removeButton = new Button("Remove");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(e -> {
            world.removeDecorationInstance(instance);
            parent.getChildren().remove(this.get());

        });
    }
}
