package uk.bradleyjones.worldgenerator.world.decorations;

import uk.bradleyjones.worldgenerator.world.TileType;
import uk.bradleyjones.worldgenerator.world.biomes.Biome;

import java.util.List;
import java.util.Map;

public class Decoration {
    public final String name;
    public final List<DecorationCell> cells;
    public final List<Biome> allowedBiomes;
    public final TileType requiredSurface;
    public final float chance;
    public final PlacementSide placementSide;

    public Decoration(String name, List<DecorationCell> cells, List<Biome> allowedBiomes, TileType requiredSurface, float chance, PlacementSide placementSide) {
        this.name = name;
        this.cells = cells;
        this.allowedBiomes = allowedBiomes;
        this.requiredSurface = requiredSurface;
        this.chance = chance;
        this.placementSide = placementSide;
    }

    private static final DecorationParser parser = new DecorationParser();
    public static final Decoration FLOWER = new Decoration(
            "Flower",
            parser.parse(new String[]{
                    "F",
                    "*"
            }, Map.of('F', TileType.FLOWER)),
            List.of(Biome.PLAINS, Biome.FOREST),
            TileType.GRASS,
            0.1f,
            PlacementSide.FLOOR
    );
    public static final Decoration RED_MUSHROOM = new Decoration(
            "Red Mushroom",
            parser.parse(new String[]{
                    "M",
                    "*"
            }, Map.of('M', TileType.RED_MUSHROOM)),
            List.of(Biome.PLAINS, Biome.FOREST),
            TileType.GRASS,
            0.15f,
            PlacementSide.FLOOR
    );
    public static final Decoration PINK_MUSHROOM = new Decoration(
            "Pink Mushroom",
            parser.parse(new String[]{
                    "M",
                    "*"
            }, Map.of('M', TileType.PINK_MUSHROOM)),
            Biome.ALL,
            TileType.STONE,
            0.03f,
            PlacementSide.FLOOR
    );
    public static final Decoration HANGING_MOSS = new Decoration(
            "Hanging Red Moss",
            parser.parse(new String[]{
                    "*",
                    "F"
            }, Map.of('F', TileType.HANGING_MOSS)),
            Biome.ALL,
            TileType.STONE,
            .35F,
            PlacementSide.CEILING
    );
    public static final Decoration HANGING_RED_MOSS = new Decoration(
            "Hanging Red Moss",
            parser.parse(new String[]{
                    "*",
                    "F"
            }, Map.of('F', TileType.HANGING_RED_MOSS)),
            Biome.ALL,
            TileType.STONE,
            .15F,
            PlacementSide.CEILING
    );
    public static final Decoration TREE = new Decoration(
            "Tree",
            parser.parse(new String[]{
                    " L ",
                    "LLL",
                    "LLL",
                    " T ",
                    " T ",
                    " T ",
                    " * "
            }, Map.of('L', TileType.LEAVES, 'T', TileType.LOG)),
            List.of(Biome.PLAINS, Biome.FOREST),
            TileType.GRASS,
            .02F,
            PlacementSide.FLOOR
    );
    public static final Decoration FOREST_TREE = new Decoration(
            "Tree",
            parser.parse(new String[]{
                    " L ",
                    "LLL",
                    "LLL",
                    " T ",
                    " T ",
                    " T ",
                    " * "
            }, Map.of('L', TileType.LEAVES, 'T', TileType.LOG)),
            List.of(Biome.FOREST),
            TileType.GRASS,
            .5F,
            PlacementSide.FLOOR
    );
    public static final Decoration HOUSE = new Decoration(
            "House",
            parser.parse(new String[]{
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
            }, Map.of('L', TileType.LOG, 'S', TileType.STONE)),
            List.of(Biome.PLAINS),
            TileType.GRASS,
            .12F,
            PlacementSide.FLOOR
    );
    public static final Decoration SEAWEED = new Decoration(
            "Seaweed",
            parser.parse(new String[]{
                    "S",
                    "B",
                    "*"
            }, Map.of('S', TileType.SEAWEED,'B', TileType.SEAWEED_BOTTOM)),
            List.of(Biome.OCEAN),
            TileType.SAND,
            .3F,
            PlacementSide.UNDERWATER_FLOOR
    );
    public static final Decoration SEAWEED_SMALL = new Decoration(
            "Small Seaweed",
            parser.parse(new String[]{
                    "S",
                    "*"
            }, Map.of('S', TileType.SEAWEED,'B', TileType.SEAWEED_BOTTOM)),
            List.of(Biome.OCEAN),
            TileType.SAND,
            .3F,
            PlacementSide.UNDERWATER_FLOOR
    );
    public static final Decoration CACTUS = new Decoration(
            "Cactus",
            parser.parse(new String[]{
                    "C",
                    "C",
                    "*"
            }, Map.of('C', TileType.CACTUS)),
            List.of(Biome.DESERT),
            TileType.SAND,
            .05F,
            PlacementSide.FLOOR
    );


    public static final List<Decoration> ALL = List.of(FLOWER,HANGING_RED_MOSS,TREE, FOREST_TREE, HOUSE, SEAWEED, SEAWEED_SMALL, HANGING_MOSS, PINK_MUSHROOM, RED_MUSHROOM, CACTUS);


}
