package uk.bradleyjones.worldgenerator.world.biomes;

import uk.bradleyjones.worldgenerator.world.TileType;

import java.util.List;

public class Biome {

    public final String id;   // stable identifier
    public final String name;
    public final TileType surfaceTile;
    public final TileType subsurfaceTile;

    public Biome(String id, String name, TileType surfaceTile, TileType subsurfaceTile) {
        this.id = id;
        this.name = name;
        this.surfaceTile = surfaceTile;
        this.subsurfaceTile = subsurfaceTile;
    }

    public static Biome getById(String id) {
        for (Biome b : ALL) {
            if (b.id.equals(id)) return b;
        }
        return null;
    }

    public static final Biome PLAINS = new Biome("plains", "Plains", TileType.GRASS, TileType.DIRT);
    public static final Biome DESERT = new Biome("desert", "Desert", TileType.SAND, TileType.SAND);
    public static final Biome TUNDRA = new Biome("tundra", "Tundra", TileType.SNOW, TileType.DIRT);
    public static final Biome FOREST = new Biome("forest", "Forest", TileType.GRASS, TileType.DIRT);
    public static final Biome BEACH = new Biome("beach", "Beach", TileType.SAND, TileType.SAND);
    public static final Biome OCEAN = new Biome("ocean", "Ocean", TileType.SAND, TileType.SAND);
    public static final Biome LAKE = new Biome("lake", "Lake", TileType.SAND, TileType.SAND);
    public static final Biome MOUNTAIN = new Biome("mountain", "Mountain", TileType.STONE, TileType.STONE);
    public static final Biome MOUNTAIN_PEAK = new Biome("mountain_peak", "Mountain Peak", TileType.SNOW, TileType.STONE);

    public static final List<Biome> ALL = List.of(
            PLAINS, DESERT, TUNDRA, FOREST, BEACH, OCEAN, LAKE, MOUNTAIN, MOUNTAIN_PEAK
    );
}