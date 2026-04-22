package uk.bradleyjones.worldgenerator.ui;
import javafx.util.converter.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;


import java.util.Random;

public class RandomizableField<T extends Number> {
    private final TextField field;
    private final Button randomButton;
    private final HBox root;
    private final Random random = new Random(System.nanoTime());

    public RandomizableField(T initial, T min, T max) {
        field = new TextField(initial.toString());
        randomButton = new Button("🎲");
        randomButton.setOnAction(e -> field.setText(randomize(min, max)));

        if (initial instanceof Integer)
            field.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        else if (initial instanceof Double)
            field.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        else if (initial instanceof Float)
            field.setTextFormatter(new TextFormatter<>(new FloatStringConverter()));
        else if (initial instanceof Long)
            field.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        field.textProperty().setValue(String.valueOf(initial));


        root = new HBox(4, field, randomButton);
    }

    private String randomize(T min, T max) {
        if (min instanceof Integer)
            return String.valueOf(random.nextInt(max.intValue() - min.intValue()) + min.intValue());
        if (min instanceof Double)
            return String.valueOf(min.doubleValue() + random.nextDouble() * (max.doubleValue() - min.doubleValue()));
        if (min instanceof Float)
            return String.valueOf(min.floatValue() + random.nextFloat() * (max.floatValue() - min.floatValue()));
        if (min instanceof Long)
            return String.valueOf(random.nextLong(max.longValue() - min.longValue()) + min.longValue());
        return field.getText();
    }

    public HBox get() { return root; }
    public TextField getField() { return field; }
}