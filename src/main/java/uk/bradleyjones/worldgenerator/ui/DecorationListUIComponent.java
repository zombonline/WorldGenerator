package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.world.decorations.Decoration;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationInstance;
import uk.bradleyjones.worldgenerator.world.decorations.DecorationRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class DecorationListUIComponent {

    private VBox instancesBox;
    private VBox root;
    private Button loadButton;

    public DecorationListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Decoration");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            Decoration decoration = new Decoration();
            DecorationInstance instance = new DecorationInstance(decoration, true);
            world.getDecorationInstances().add(instance);
            instancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, instancesBox).get());
        });

        loadButton = new Button("+ Load Decoration");
        loadButton.setMaxWidth(Double.MAX_VALUE);
        loadButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load Decoration");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Decoration Files", "*.decoration")
            );
            File dir = new File(DecorationRepository.DECORATIONS_DIR);
            if (!dir.exists()) dir.mkdirs();
            chooser.setInitialDirectory(dir);

            Stage stage = (Stage) loadButton.getScene().getWindow();
            List<File> files = chooser.showOpenMultipleDialog(stage);
            if (files == null) return;

            try {
                for (File file : files) {
                    var decoration = DecorationRepository.load(file.toPath());
                    var instance = new DecorationInstance(decoration, true);
                    instance.fileName = file.getName();
                    for (var existing : world.getDecorationInstances()) {
                        if (Objects.equals(existing.fileName, file.getName())) return;
                    }
                    world.getDecorationInstances().add(instance);
                    instancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, instancesBox).get());
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        for (DecorationInstance instance : world.getDecorationInstances()) {
            instancesBox.getChildren().add(new DecorationInstanceUIComponent(instance, instancesBox).get());
        }

        root.getChildren().addAll(addButton, loadButton, instancesBox);
    }
}
