package uk.bradleyjones.worldgenerator.world.biomes;

import uk.bradleyjones.worldgenerator.world.TileType;

public class Biome {

    public Biome(String name, TileType surfaceTile, TileType subsurfaceTile) {
        this.name = name;
        this.surfaceTile = surfaceTile;
        this.subsurfaceTile = subsurfaceTile;
    }

    public String name;
    public TileType surfaceTile;
    public TileType subsurfaceTile;


    public static final Biome PLAINS = new Biome("Plains", TileType.GRASS, TileType.DIRT);
    public static final Biome DESERT = new Biome("Desert", TileType.SAND, TileType.SAND);
    public static final Biome TUNDRA = new Biome("Tundra", TileType.SNOW, TileType.DIRT);
    public static final Biome FOREST = new Biome("Forest", TileType.GRASS, TileType.DIRT);
    public static final Biome BEACH = new Biome("Beach", TileType.SAND, TileType.SAND);
    public static final Biome OCEAN = new Biome("Ocean", TileType.SAND, TileType.SAND);
    public static final Biome LAKE = new Biome("Lake", TileType.SAND, TileType.SAND);
    public static final Biome MOUNTAIN = new Biome("Mountain", TileType.STONE, TileType.STONE);
    public static final Biome MOUNTAIN_PEAK = new Biome("Mountain PEAK", TileType.SNOW, TileType.STONE);

}

