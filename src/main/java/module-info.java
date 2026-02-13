module uk.bradleyjones.worldgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires opensimplex;


    opens uk.bradleyjones.worldgenerator to javafx.fxml;
    exports uk.bradleyjones.worldgenerator;
}