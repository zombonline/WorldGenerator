package uk.bradleyjones.worldgenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.input.InputHandler;

import java.io.IOException;

public class WorldGeneratorApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WorldGeneratorApp.class.getResource("worldgenerator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1820, 980);
        InputHandler inputHandler = new InputHandler();
        //maximize the window

        WorldGeneratorController controller = fxmlLoader.getController();
        scene.setOnKeyPressed(inputHandler::handleKeyPressed);
        scene.setOnKeyReleased(inputHandler::handleKeyReleased);
        scene.setOnScroll(inputHandler::handleScroll);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}