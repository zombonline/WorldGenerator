package uk.bradleyjones.worldgenerator.world.decorations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DecorationRepository {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DECORATIONS_DIR = "decorations";

    // Load all Decoration JSONs from the decorations folder
    public static List<Decoration> loadAll() {
        List<Decoration> result = new ArrayList<>();
        Path dir = Path.of(DECORATIONS_DIR);

        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                System.err.println("Could not create decorations directory: " + e.getMessage());
                return result;
            }
        }

        try (var stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            Decoration decoration = load(path);
                            if (decoration != null) result.add(decoration);
                        } catch (Exception e) {
                            System.err.println("Failed to load decoration: " + path + " — " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to list decorations directory: " + e.getMessage());
        }
        System.out.println("Loaded " + result.size() + " decorations");
        for(Decoration decoration : result) {

        }
        return result;
    }

    private static Decoration load(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, Decoration.class);
        }
    }

    public static void save(Decoration decoration) {
        Path dir = Path.of(DECORATIONS_DIR);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Could not create decorations directory: " + e.getMessage());
            return;
        }

        String fileName = decoration.name.toLowerCase().replace(" ", "_") + ".json";
        Path path = dir.resolve(fileName);

        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(decoration, writer);
            System.out.println("Saved decoration: " + fileName + " to " + path);
        } catch (IOException e) {
            System.err.println("Failed to save decoration: " + e.getMessage());
        }
    }

}