package uk.bradleyjones.worldgenerator.world.decorations;

import uk.bradleyjones.worldgenerator.world.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decoration {
    public String desc;
    public List<String> asciiRows;
    public Map<Character, TileType> charMap;
    public List<String> allowedBiomes;
    public TileType requiredSurface;
    public float chance;
    public PlacementType placementType;

    private transient List<DecorationCell> cells;

    private static final DecorationParser parser = new DecorationParser();

    public Decoration(String desc, List<String> asciiRows, Map<Character, TileType> map, List<String> allowedBiomes, TileType requiredSurface, float chance, PlacementType placementType) {
        this.desc = desc;
        this.asciiRows = asciiRows;
        this.charMap = map;
        this.allowedBiomes = allowedBiomes;
        this.requiredSurface = requiredSurface;
        this.chance = chance;
        this.placementType = placementType;
    }
    public Decoration(Decoration other) {
        this.desc = other.desc;
        this.asciiRows = new ArrayList<>(other.asciiRows);
        this.charMap = new HashMap<>(other.charMap);
        this.allowedBiomes = new ArrayList<>(other.allowedBiomes);
        this.requiredSurface = other.requiredSurface;
        this.chance = other.chance;
        this.placementType = other.placementType;
    }
    public Decoration() {
        this.desc = "New Decoration";
        this.asciiRows = new ArrayList<>();
        this.charMap = new HashMap<>();
        this.allowedBiomes = new ArrayList<>();
        this.requiredSurface = TileType.NONE;
        this.chance = 0;
        this.placementType = PlacementType.FLOOR;
    }

    public List<DecorationCell> getCells()
    {
        if(cells==null || cells.isEmpty())
            cells = parser.parse(asciiRows, charMap);
        return cells;
    }

    public static final Decoration FLOWER = new Decoration(
            "Flower",
            List.of("F", "*"),
            Map.of('F', TileType.FLOWER),
            List.of("plains", "forest"),
            TileType.GRASS,
            0.1f,
            PlacementType.FLOOR
    );

    public static final Decoration RED_MUSHROOM = new Decoration(
            "Red Mushroom",
            List.of("M", "*"),
            Map.of('M', TileType.RED_MUSHROOM),
            List.of("plains", "forest"),
            TileType.GRASS,
            0.15f,
            PlacementType.FLOOR
    );

    public static final Decoration PINK_MUSHROOM = new Decoration(
            "Pink Mushroom",
            List.of("M", "*"),
            Map.of('M', TileType.PINK_MUSHROOM),
            List.of("plains", "forest", "desert"),
            TileType.STONE,
            0.03f,
            PlacementType.FLOOR
    );

    public static final Decoration HANGING_MOSS = new Decoration(
            "Hanging Moss",
            List.of("*", "F"),
            Map.of('F', TileType.HANGING_MOSS),
            List.of(),
            TileType.STONE,
            0.35f,
            PlacementType.CEILING
    );

    public static final Decoration HANGING_RED_MOSS = new Decoration(
            "Hanging Red Moss",
            List.of("*", "F"),
            Map.of('F', TileType.HANGING_RED_MOSS),
            List.of(),
            TileType.STONE,
            0.15f,
            PlacementType.CEILING
    );

    public static final Decoration CACTUS = new Decoration(
            "Cactus",
            List.of("C", "C", "*"),
            Map.of('C', TileType.CACTUS),
            List.of("desert"),
            TileType.SAND,
            0.05f,
            PlacementType.FLOOR
    );

    public static final Decoration TREE = new Decoration(
            "Tree",
            List.of(" L ", "LLL", "LLL", " T ", " T ", " T ", " * "),
            Map.of('T', TileType.LOG, 'L', TileType.LEAVES),
            List.of("forest"),
            TileType.GRASS,
            0.5f,
            PlacementType.FLOOR
    );

    public static final Decoration SEAWEED = new Decoration(
            "Seaweed",
            List.of("S", "B", "*"),
            Map.of('S', TileType.SEAWEED, 'B', TileType.SEAWEED_BOTTOM),
            List.of("ocean"),
            TileType.SAND,
            0.3f,
            PlacementType.UNDERWATER_FLOOR
    );

    public static final Decoration SEAWEED_SMALL = new Decoration(
            "Small Seaweed",
            List.of("S", "*"),
            Map.of('S', TileType.SEAWEED),
            List.of("ocean"),
            TileType.SAND,
            0.3f,
            PlacementType.UNDERWATER_FLOOR
    );

    public static final Decoration HOUSE = new Decoration(
            "House",
            List.of(
                    "       L       ",
                    "      LLL      ",
                    "    LLLLLLL    ",
                    "  LLLLLLLLLLL  ",
                    "LLLLLLLLLLLLLLL",
                    "SSSSSSSSSSSSSSS",
                    "SS    SSS    SS",
                    "SSSSSSSSSSSSSSS",
                    "SS  SSSSS    SS",
                    "SS  SSSSSSSSSSS",
                    "       *       "
            ),
            Map.of('S', TileType.STONE, 'L', TileType.LOG),
            List.of("plains"),
            TileType.GRASS,
            0.12f,
            PlacementType.FLOOR
    );

    public static List<Decoration> defaults() {
        return List.of(
                FLOWER, RED_MUSHROOM, PINK_MUSHROOM,
                HANGING_MOSS, HANGING_RED_MOSS,
                CACTUS, TREE,
                SEAWEED, SEAWEED_SMALL,
                HOUSE
        );
    }

}
