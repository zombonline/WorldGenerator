module uk.bradleyjones.worldgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens uk.bradleyjones.worldgenerator to javafx.fxml;
    exports uk.bradleyjones.worldgenerator;
}