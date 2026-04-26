package uk.bradleyjones.worldgenerator.ui;

import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.converter.*;

import java.util.Random;

public class RandomizableField extends HBox {

    private final TextField field = new TextField();
    private final Button randomButton = new Button("🎲");
    private final Random random = new Random(System.nanoTime());

    private double min = 0;
    private double max = 100;
    private String type = "Integer";

    public RandomizableField() {
        setSpacing(4);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(field, Priority.ALWAYS);
        randomButton.setMinWidth(Region.USE_PREF_SIZE);
        randomButton.setPrefWidth(32);
        getChildren().addAll(field, randomButton);
        randomButton.setOnAction(e -> field.setText(randomize()));
        applyFormatter();
    }

    // FXML properties
    public void setMin(double min) { this.min = min; }
    public double getMin() { return min; }

    public void setMax(double max) { this.max = max; }
    public double getMax() { return max; }

    public void setType(String type) {
        this.type = type;
        applyFormatter();
    }
    public String getType() { return type; }

    public void setValue(String value) { field.setText(value); }
    public String getValue() { return field.getText(); }

    public TextField getField() { return field; }

    private void applyFormatter() {
        field.setTextFormatter(switch (type) {
            case "Double" -> new TextFormatter<>(new DoubleStringConverter());
            case "Float" -> new TextFormatter<>(new FloatStringConverter());
            case "Long" -> new TextFormatter<>(new LongStringConverter());
            default -> new TextFormatter<>(new IntegerStringConverter());
        });
    }

    private String randomize() {
        return switch (type) {
            case "Double" -> String.valueOf(min + random.nextDouble() * (max - min));
            case "Float" -> String.valueOf((float)(min + random.nextFloat() * (max - min)));
            case "Long" -> String.valueOf((long)(min + random.nextLong((long)(max - min))));
            default -> String.valueOf((int)(min + random.nextInt((int)(max - min))));
        };
    }
}