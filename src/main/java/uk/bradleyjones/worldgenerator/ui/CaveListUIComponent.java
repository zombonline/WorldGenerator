package uk.bradleyjones.worldgenerator.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorInstance;
import uk.bradleyjones.worldgenerator.world.caves.CaveGeneratorType;

import static uk.bradleyjones.worldgenerator.WorldGeneratorController.world;

public class CaveListUIComponent {

    private VBox instancesBox;
    private VBox root;

    public CaveListUIComponent() {
        setUp();
    }

    public VBox get() {
        return root;
    }

    private void setUp() {
        instancesBox = new VBox(4);
        root = new VBox(4);

        Button addButton = new Button("+ Add Cave Generator");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> {
            CaveGeneratorInstance instance = new CaveGeneratorInstance(CaveGeneratorType.CA);
            world.getCaveInstances().add(instance);
            instancesBox.getChildren().add(new CaveInstanceUIComponent(instance, instancesBox).get());
        });

        for (CaveGeneratorInstance instance : world.getCaveInstances()) {
            instancesBox.getChildren().add(new CaveInstanceUIComponent(instance, instancesBox).get());
        }

        root.getChildren().addAll(addButton, instancesBox);
    }
}
