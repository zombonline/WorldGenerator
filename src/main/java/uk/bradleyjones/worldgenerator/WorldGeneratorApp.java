package uk.bradleyjones.worldgenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import uk.bradleyjones.worldgenerator.input.InputHandler;

import java.io.IOException;

public class WorldGeneratorApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WorldGeneratorApp.class.getResource("worldgenerator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1820, 980);
        Font.loadFont(WorldGeneratorApp.class.getResourceAsStream("uk/bradleyjones/worldgenerator/fonts/Roboto-Bold.ttf"), 14);
        Font.loadFont(WorldGeneratorApp.class.getResourceAsStream("uk/bradleyjones/worldgenerator/fonts/Roboto-Regular.ttf"), 14);
        WorldGeneratorController controller = fxmlLoader.getController();
        stage.setScene(scene);
        controller.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}