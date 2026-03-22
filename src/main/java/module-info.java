module uk.bradleyjones.worldgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires opensimplex;

    opens uk.bradleyjones.worldgenerator to javafx.fxml;
    opens uk.bradleyjones.worldgenerator.images;
    exports uk.bradleyjones.worldgenerator;
}