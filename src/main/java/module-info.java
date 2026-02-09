module uk.bradleyjones.worldgenerator.worldgenerator {
    requires javafx.controls;
    requires javafx.fxml;


    opens uk.bradleyjones.worldgenerator.worldgenerator to javafx.fxml;
    exports uk.bradleyjones.worldgenerator.worldgenerator;
}