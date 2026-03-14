package uk.bradleyjones.worldgenerator.world.caves;

import uk.bradleyjones.worldgenerator.util.Vector2Int;

import java.util.Random;


public class DrunkardCaveGenerator implements CaveGenerator {
    private int width, height, seed;
    private DrunkardCaveConfig config;
    private boolean[][] map;
    private Vector2Int[] directions = new Vector2Int[]{Vector2Int.UP,Vector2Int.DOWN,Vector2Int.LEFT,Vector2Int.RIGHT};
    public DrunkardCaveGenerator(int width, int height, int seed, DrunkardCaveConfig config)
    {
        this.width = width;
        this.height = height;
        map = new boolean[width][height];
        this.seed = seed;
        this.config = config;
        generate();
    }

    private void generate(){
        Vector2Int[] walkerPositions = new Vector2Int[config.walkerCount];
        Random random = new Random(seed);
        for(int i = 0; i < walkerPositions.length; i++)
        {
            walkerPositions[i] = new Vector2Int(random.nextInt(0, width), random.nextInt(0,height));
            map[walkerPositions[i].x][ walkerPositions[i].y] = true;
            for(int j = 0; j < config.steps; j++)
            {
                Vector2Int dir = directions[random.nextInt(0, directions.length)];
                walkerPositions[i].add(dir);
                walkerPositions[i].clamp(0,0,width-1,height-1);
                map[walkerPositions[i].x][ walkerPositions[i].y] = true;
            }
        }

    }

    @Override
    public boolean isCave(int x, int y) {
        if(map[x][y]) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Checking if ");
            stringBuilder.append(x);
            stringBuilder.append(y);
            stringBuilder.append("is cave: ");
            stringBuilder.append(map[x][y]);
            System.out.println(stringBuilder.toString());
        }
        return map[x][y];
    }
}
