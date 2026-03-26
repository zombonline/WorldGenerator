package uk.bradleyjones.worldgenerator.world.decorations;

import uk.bradleyjones.worldgenerator.world.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecorationParser {
    public static final char ROOT_CHARACTER = '*';
    public List<DecorationCell> parse(List<String> rows, Map<Character, TileType> characterTileTypeMap){
        List<DecorationCell> cells = new ArrayList<>();

        int rootRow = Integer.MAX_VALUE, rootCol = Integer.MAX_VALUE;
        for(int i = 0; i < rows.size(); i++)
        {
            for(int j = 0; j < rows.get(i).length(); j++)
            {
                if(rows.get(i).charAt(j) == ROOT_CHARACTER) {
                    rootRow = i;
                    rootCol = j;
                }
            }
        }
        if(rootRow == Integer.MAX_VALUE)
        {
            return null;
        }

        for(int i = 0; i < rows.size(); i++)
        {
            for(int j = 0; j < rows.get(i).length(); j++)
            {
                char c = rows.get(i).charAt(j);
                if(!characterTileTypeMap.containsKey(c))
                    continue;
                int row = i - rootRow;
                int col = j - rootCol;
                TileType tileType = characterTileTypeMap.get(c);
                cells.add(new DecorationCell(col, row, tileType));
            }
        }
        return cells;
    }
}
