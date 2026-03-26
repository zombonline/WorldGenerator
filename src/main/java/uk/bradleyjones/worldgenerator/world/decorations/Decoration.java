package uk.bradleyjones.worldgenerator.world.decorations;

import uk.bradleyjones.worldgenerator.world.TileType;

import java.util.List;
import java.util.Map;

public class Decoration {
    public String name;
    public List<String> asciiRows;
    public Map<Character, TileType> map;
    public List<String> allowedBiomes;
    public TileType requiredSurface;
    public float chance;
    public PlacementSide placementSide;
    private transient List<DecorationCell> cells;
    private static DecorationParser parser = new DecorationParser();
    public Decoration(String name, List<String> asciiRows, Map<Character, TileType> map, List<String> allowedBiomes, TileType requiredSurface, float chance, PlacementSide placementSide) {
        this.name = name;
        this.asciiRows = asciiRows;
        this.map = map;
        this.allowedBiomes = allowedBiomes;
        this.requiredSurface = requiredSurface;
        this.chance = chance;
        this.placementSide = placementSide;
    }

    public List<DecorationCell> getCells()
    {
        if(cells==null || cells.isEmpty())
            cells = parser.parse(asciiRows, map);
        return cells;
    }

}
