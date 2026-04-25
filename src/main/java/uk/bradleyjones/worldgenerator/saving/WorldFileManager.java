package uk.bradleyjones.worldgenerator.saving;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.world.World;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class WorldFileManager {



    private static Path currentPath = null;
    private static String currentName = null;
    public static Path getCurrentPath() {
        return currentPath;
    }

    public static String getCurrentName() {
        return currentName;
    }
    public static void saveAs(Stage owner, World world) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save World");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("World Files", "*.world")
        );
        File file = chooser.showSaveDialog(owner);
        if (file != null) {
            currentPath = file.toPath();
            currentName = file.getName().replace(".world", "");
            try {
                WorldSaveManager.save(world, currentName, currentPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save(Stage owner, World world) {
        if (currentPath != null) {
            try {
                WorldSaveManager.save(world, currentName, currentPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveAs(owner, world);
        }
    }

    public static void load(Stage owner, World world) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load World");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("World Files", "*.world")
        );
        File file = chooser.showOpenDialog(owner);
        if (file != null) {
            currentPath = file.toPath();
            currentName = file.getName().replace(".world", "");
            try {
                WorldSaveManager.load(world, currentPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
