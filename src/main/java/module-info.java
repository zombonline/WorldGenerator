module uk.bradleyjones.worldgenerator {
    requires javafx.fxml;
    requires opensimplex;
    requires com.google.gson;
    requires com.dlsc.gemsfx;
    requires org.apache.commons.lang3;
    requires com.github.weisj.jsvg;
    requires jdk.unsupported.desktop;
    requires commons.validator;

    opens uk.bradleyjones.worldgenerator to javafx.fxml;
    opens uk.bradleyjones.worldgenerator.images;
    opens uk.bradleyjones.worldgenerator.saving to com.google.gson;
    opens uk.bradleyjones.worldgenerator.world.caves to com.google.gson;
    opens uk.bradleyjones.worldgenerator.world.heightmap to com.google.gson;
    opens uk.bradleyjones.worldgenerator.world.biomes to com.google.gson;
    opens uk.bradleyjones.worldgenerator.world.decorations to com.google.gson;
    opens uk.bradleyjones.worldgenerator.world.substances to com.google.gson;
    opens uk.bradleyjones.worldgenerator.world to com.google.gson;
    opens uk.bradleyjones.worldgenerator.ui to javafx.fxml;
    exports uk.bradleyjones.worldgenerator.ui to javafx.fxml;
    exports uk.bradleyjones.worldgenerator;
    exports uk.bradleyjones.worldgenerator.world to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.decorations to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.biomes to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.heightmap to com.google.gson;
    exports uk.bradleyjones.worldgenerator.world.water to com.google.gson;
    exports uk.bradleyjones.worldgenerator.ui.commitables to javafx.fxml;
    opens uk.bradleyjones.worldgenerator.ui.commitables to javafx.fxml;

}