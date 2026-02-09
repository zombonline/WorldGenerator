module uk.bradleyjones.worldgenerator {
    requires javafx.controls;
    requires javafx.fxml;


    opens uk.bradleyjones.worldgenerator to javafx.fxml;
    exports uk.bradleyjones.worldgenerator;
}