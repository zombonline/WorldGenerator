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

}
