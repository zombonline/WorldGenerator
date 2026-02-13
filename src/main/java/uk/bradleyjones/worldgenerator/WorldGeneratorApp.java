package uk.bradleyjones.worldgenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WorldGeneratorApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WorldGeneratorApp.class.getResource("worldgenerator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 900);
        WorldGeneratorController controller = fxmlLoader.getController();
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnScroll(controller::handleScroll);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}