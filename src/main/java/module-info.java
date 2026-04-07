module uk.bradleyjones.worldgenerator {
    requires javafx.fxml;
    requires opensimplex;
    requires com.google.gson;
    requires com.dlsc.gemsfx;
    requires org.apache.commons.lang3;

    opens uk.bradleyjones.worldgenerator to javafx.fxml;
    opens uk.bradleyjones.worldgenerator.images;
    exports uk.bradleyjones.worldgenerator;
    exports uk.bradleyjones.worldgenerator.world to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.decorations to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.biomes to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.heightmap to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.water to com.google.gson;

}